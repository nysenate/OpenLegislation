package gov.nysenate.openleg.service.spotcheck.senatesite;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.view.bill.BillStatusView;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBill;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

@Service
public class SenateSiteBillCheckService extends BaseSpotCheckService<BillId,Bill,SenateSiteBill> {

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<BillId> check(Bill content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<BillId> check(Bill content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<BillId> check(Bill content, SenateSiteBill reference) {
        SpotCheckObservation<BillId> observation = new SpotCheckObservation<>(reference.getReferenceId(), reference.getBillId());

        BillAmendment amendment;
        try {
            Version refVersion = Version.of(reference.getActiveVersion());
            amendment = content.getAmendment(refVersion);
        } catch (IllegalArgumentException | BillAmendNotFoundEx ex) {
            observation.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, null, reference.getBillId()));
            return observation;
        }

        checkBasePrintNo(content, reference, observation);
        checkChamber(content, reference, observation);
        checkActiveVersion(content, reference, observation);
        checkIsAmended(content, reference, observation);
        checkPublishDate(content, reference, observation);
        checkActions(content, reference, observation);
        checkMilestones(content, reference, observation);
        checkLastStatus(content, reference, observation);
        checkLastStatusComm(content, reference, observation);
        checkLastStatusDate(content, reference, observation);
        checkSponsor(content, reference, observation);
        checkTitle(content, reference, observation);
        checkSummary(content, reference, observation);

        checkMemo(amendment, reference, observation);
        checkCoSponsors(amendment, reference, observation);
        checkMultiSponsors(amendment, reference, observation);
        checkHasSameAs(amendment, reference, observation);
        checkSameAs(amendment, reference, observation);
        checkPrevVersions(amendment, reference, observation);

        return observation;
    }

    private void checkBasePrintNo(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getBasePrintNo(), reference.getBasePrintNo(), observation, BILL_BASE_PRINT_NO);
    }

    private void checkChamber(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkObject(content.getChamber(), StringUtils.upperCase(reference.getChamber()), observation, BILL_CHAMBER);
    }

    private void checkActiveVersion(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkObject(content.getActiveVersion(), StringUtils.upperCase(reference.getActiveVersion()),
                observation, BILL_ACTIVE_AMENDMENT);
    }

    private void checkSameAs(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        TreeSet<BillId> olSameAs = new TreeSet<>(content.getSameAs());
        TreeSet<BillId> refSameAs = new TreeSet<>(reference.getSameAs());
        checkCollection(olSameAs, refSameAs, observation, BILL_SAME_AS);
    }

    private void checkPrevVersions(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        TreeSet<BillId> olPrevVers = new TreeSet<>(content.getSameAs());
        TreeSet<BillId> refPrevVers = new TreeSet<>(reference.getSameAs());
        checkCollection(olPrevVers, refPrevVers, observation, BILL_PREVIOUS_VERSIONS);
    }

    private void checkIsAmended(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        Version refVersion = Version.of(reference.getActiveVersion());
        boolean olIsAmended = content.getActiveVersion() != refVersion;
        boolean refIsAmended = reference.isAmended();
        checkObject(olIsAmended, refIsAmended, observation, BILL_IS_AMENDED);
    }

    private void checkHasSameAs(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkObject(!content.getSameAs().isEmpty(), reference.isHasSameAs(), observation, BILL_HAS_SAME_AS);
    }

    private void checkPublishDate(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        LocalDateTime olPubDateTime = null;
        try {
            olPubDateTime = content.getPublishStatus(Version.of(reference.getActiveVersion()))
                    .map(PublishStatus::getEffectDateTime).orElse(null);
        } catch (IllegalArgumentException ignored) {}
        LocalDateTime refPubDateTime = reference.getPublishDate();
        checkObject(olPubDateTime, refPubDateTime, observation, BILL_PUBLISH_DATE);
    }

    private String billActionToString(BillAction action) {
        return String.valueOf(action.getSequenceNo()) + " " +
                action.getBillId() + " " +
                action.getChamber() + " " +
                action.getDate() + " " +
                action.getText();
    }

    private void checkActions(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkCollection(content.getActions(), reference.getActions(), observation, BILL_ACTION,
                this::billActionToString, "\n");
    }

    private String billStatusToString(BillStatusView status) {
        return String.valueOf(status.getBillCalNo()) + " " +
                status.getActionDate() + " " +
                status.getCommitteeName() + " " +
                status.getStatusType() + " " +
                status.getStatusDesc();
    }

    private void checkMilestones(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<BillStatusView> olMilestones = Optional.ofNullable(content.getMilestones())
                .orElse(Collections.emptyList()).stream()
                .map(BillStatusView::new).collect(Collectors.toList());
        checkCollection(olMilestones, reference.getMilestones(), observation, BILL_MILESTONES,
                this::billStatusToString, "\n");
    }

    private void checkLastStatus(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkObject(Optional.ofNullable(content.getStatus()).map(BillStatus::getStatusType).orElse(null),
                reference.getLastStatus(), observation, BILL_LAST_STATUS);
    }

    private void checkLastStatusComm(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String olLastStatusComm = Optional.ofNullable(content.getStatus())
                        .map(BillStatus::getCommitteeId)
                        .map(CommitteeId::getName).orElse(null);
        checkStringUpper(olLastStatusComm, reference.getLatestStatusCommittee(), observation, BILL_LAST_STATUS_COMM);
    }

    private void checkLastStatusDate(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        LocalDateTime olLastStatusDate = Optional.ofNullable(content.getStatus())
                .map(BillStatus::getActionDate).map(LocalDate::atStartOfDay).orElse(null);
        checkObject(olLastStatusDate, reference.getLastStatusDate(), observation, BILL_LAST_STATUS_DATE);
    }

    private void checkSponsor(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String olSponsor = Optional.ofNullable(content.getSponsor())
                .map(BillSponsor::getMember).map(SessionMember::getLbdcShortName).orElse(null);
        checkString(olSponsor, reference.getSponsor(), observation, BILL_SPONSOR);
    }

    private List<String> extractAdditionalSponsors(List<SessionMember> sponsorList) {
        return Optional.ofNullable(sponsorList)
                .orElse(Collections.emptyList()).stream()
                .map(SessionMember::getLbdcShortName)
                .collect(Collectors.toList());
    }

    private void checkCoSponsors(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkCollection(extractAdditionalSponsors(content.getCoSponsors()), reference.getCoSponsors(),
                observation, BILL_COSPONSOR);
    }

    private void checkMultiSponsors(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkCollection(extractAdditionalSponsors(content.getMultiSponsors()), reference.getMultiSponsors(),
                observation, BILL_MULTISPONSOR);
    }

    private void checkTitle(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getTitle(), reference.getTitle(), observation, BILL_TITLE);
    }

    private void checkSummary(Bill content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getSummary(), reference.getSummary(), observation, BILL_SUMMARY);
    }

    private void checkMemo(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getMemo(), reference.getMemo(), observation, BILL_MEMO);
    }

    private void checkText(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getFullText(), reference.getText(), observation, BILL_TEXT);
    }

    private void checkLawCode(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getLaw(), reference.getLawCode(), observation, BILL_LAW_CODE);
    }

    private void checkLawSection(BillAmendment content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getLawSection(), reference.getLawSection(), observation, BILL_LAW_SECTION);
    }
}
