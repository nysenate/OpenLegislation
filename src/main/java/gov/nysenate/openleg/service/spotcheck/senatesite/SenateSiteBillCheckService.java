package gov.nysenate.openleg.service.spotcheck.senatesite;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.bill.*;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

@Service
public class SenateSiteBillCheckService extends BaseSpotCheckService<BillId, Bill, SenateSiteBill> {

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

        BillView contentBillView = new BillView(content);

        BillAmendmentView amendment;
        try {
            Version refVersion = reference.getBillId().getVersion();
            amendment = Optional.of(contentBillView)
                    .map(BillView::getAmendments).map(MapView::getItems)
                    .map(amendments -> amendments.get(refVersion.toString()))
                    .orElseThrow(() -> new BillAmendNotFoundEx(reference.getBillId()));
        } catch (IllegalArgumentException | BillAmendNotFoundEx ex) {
            observation.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, null, reference.getBillId()));
            return observation;
        }

        checkBasePrintNo(contentBillView, reference, observation);
        checkChamber(contentBillView, reference, observation);
        checkActiveVersion(contentBillView, reference, observation);
        checkIsAmended(contentBillView, reference, observation);
        checkPublishDate(contentBillView, reference, observation);
        checkActions(contentBillView, reference, observation);
        // Don't check milestones, last status, last status date, or last status committee for resolutions.
        if (!reference.getBaseBillId().getBillType().isResolution()) {
            checkMilestones(contentBillView, reference, observation);
            checkLastStatus(contentBillView, reference, observation);
            // Public website bill models last status date and last status committee are always incorrect when status = STRICKEN.
            // So only check for those errors if there is a last status mismatch or the status != STRICKEN.
            if (observation.hasMismatch(BILL_LAST_STATUS) || !StringUtils.equals(reference.getLastStatus(), BillStatusType.STRICKEN.name())) {
                checkLastStatusDate(contentBillView, reference, observation);
                checkLastStatusComm(contentBillView, reference, observation);
            }
        }
        checkSponsor(contentBillView, reference, observation);
        checkTitle(contentBillView, reference, observation);
        checkSummary(contentBillView, reference, observation);
        checkPrevVersions(contentBillView, reference, observation);

        checkText(amendment, reference, observation);
        checkMemo(amendment, reference, observation);
        checkCoSponsors(amendment, reference, observation);
        checkMultiSponsors(amendment, reference, observation);
        checkHasSameAs(amendment, reference, observation);
        checkSameAs(amendment, reference, observation);
        checkLawCode(amendment, reference, observation);
        checkLawSection(amendment, reference, observation);

        return observation;
    }

    private void checkBasePrintNo(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getBasePrintNo(), reference.getBasePrintNo(), observation, BILL_BASE_PRINT_NO);
    }

    private void checkChamber(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        Chamber contentChamber = new BillId(content.getPrintNo(), content.getSession()).getChamber();
        checkObject(contentChamber, StringUtils.upperCase(reference.getChamber()), observation, BILL_CHAMBER);
    }

    private void checkActiveVersion(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkObject(content.getActiveVersion(), StringUtils.upperCase(reference.getActiveVersion()),
                observation, BILL_ACTIVE_AMENDMENT);
    }

    private void checkSameAs(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        TreeSet<BillId> olSameAs = Optional.ofNullable(content.getSameAs())
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(BillIdView::toBillId)
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<BillId> refSameAs = new TreeSet<>(reference.getSameAs());
        checkCollection(olSameAs, refSameAs, observation, BILL_SAME_AS);
    }

    private void checkPrevVersions(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        TreeSet<BillId> olPrevVers = Optional.ofNullable(content.getPreviousVersions())
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(BillIdView::toBillId)
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<BillId> refPrevVers = new TreeSet<>(reference.getPreviousVersions());
        checkCollection(olPrevVers, refPrevVers, observation, BILL_PREVIOUS_VERSIONS);
    }

    private void checkIsAmended(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String amendVersion = Optional.ofNullable(reference.getBillId())
                .map(billid -> billid.getVersion().toString())
                .orElse("");
        boolean olIsAmended = Optional.ofNullable(content.getAmendmentVersions())
                .map(ListView::getItems)
                .flatMap(items -> items.stream()
                        .filter(version -> version.compareTo(amendVersion) > 0)
                        .findAny())
                .isPresent();
        boolean refIsAmended = reference.isAmended();
        checkObject(olIsAmended, refIsAmended, observation, BILL_IS_AMENDED);
    }

    private void checkHasSameAs(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        boolean contentHasSameAs = Optional.ofNullable(content.getSameAs())
                .map(ListView::getSize)
                .orElse(0) > 0;
        checkObject(contentHasSameAs, reference.isHasSameAs(), observation, BILL_HAS_SAME_AS);
    }

    private void checkPublishDate(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        LocalDateTime olPubDateTime = content.getPublishedDateTime();
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

    private void checkActions(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<BillAction> contentActions = Optional.ofNullable(content.getActions())
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(BillActionView::toBillAction)
                .collect(Collectors.toList());
        checkCollection(contentActions, reference.getActions(), observation, BILL_ACTION,
                this::billActionToString, "\n");
    }

    private String billStatusToString(BillStatusView status) {
        return String.valueOf(status.getBillCalNo()) + " " +
                status.getActionDate() + " " +
                status.getCommitteeName() + " " +
                status.getStatusType() + " " +
                status.getStatusDesc();
    }

    private void checkMilestones(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<BillStatusView> contentMilestones = Optional.ofNullable(content.getMilestones())
                .map(ListView::getItems)
                .orElse(ImmutableList.of());
        checkCollection(contentMilestones, reference.getMilestones(), observation, BILL_MILESTONES,
                this::billStatusToString, "\n");
    }

    private void checkLastStatus(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String contentStatus = Optional.ofNullable(content.getStatus())
                .map(BillStatusView::getStatusType)
                .orElse(null);
        checkObject(contentStatus, reference.getLastStatus(), observation, BILL_LAST_STATUS);
    }

    private void checkLastStatusComm(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String contentLastStatusComm = Optional.ofNullable(content.getStatus())
                .map(BillStatusView::getCommitteeName)
                .orElse(null);
        checkStringUpper(contentLastStatusComm, reference.getLatestStatusCommittee(),
                observation, BILL_LAST_STATUS_COMM);
    }

    private void checkLastStatusDate(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        LocalDateTime contentStatusDate = Optional.ofNullable(content.getStatus())
                .map(BillStatusView::getActionDate)
                .map(LocalDate::atStartOfDay)
                .orElse(null);
        checkObject(contentStatusDate, reference.getLastStatusDate(), observation, BILL_LAST_STATUS_DATE);
    }

    private void checkSponsor(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String contentSponsor = Optional.ofNullable(content.getSponsor())
                .map(SponsorView::getMember)
                .map(MemberView::getShortName)
                .orElse(null);
        checkString(contentSponsor, reference.getSponsor(), observation, BILL_SPONSOR);
    }

    private void checkCoSponsors(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<String> contentCoSponsors = Optional.ofNullable(content.getCoSponsors())
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(MemberView::getShortName)
                .collect(Collectors.toList());
        checkCollection(contentCoSponsors, reference.getCoSponsors(), observation, BILL_COSPONSOR);
    }

    private void checkMultiSponsors(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<String> contentMultiSponsors = Optional.ofNullable(content.getMultiSponsors())
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(MemberView::getShortName)
                .collect(Collectors.toList());
        checkCollection(contentMultiSponsors, reference.getMultiSponsors(), observation, BILL_MULTISPONSOR);
    }

    private void checkTitle(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getTitle(), reference.getTitle(), observation, BILL_TITLE);
    }

    private void checkSummary(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getSummary(), reference.getSummary(), observation, BILL_SUMMARY);
    }

    private void checkMemo(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getMemo(), reference.getMemo(), observation, BILL_MEMO);
    }

    private void checkText(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getFullText(), reference.getText(), observation, BILL_TEXT);
    }

    private void checkLawCode(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getLawCode(), reference.getLawCode(), observation, BILL_LAW_CODE);
    }

    private void checkLawSection(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        checkString(content.getLawSection(), reference.getLawSection(), observation, BILL_LAW_SECTION);
    }
}
