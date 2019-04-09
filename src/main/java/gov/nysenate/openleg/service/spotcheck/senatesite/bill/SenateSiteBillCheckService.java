package gov.nysenate.openleg.service.spotcheck.senatesite.bill;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.bill.*;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBillVote;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.bill.BillVoteType.COMMITTEE;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class SenateSiteBillCheckService implements SpotCheckService<BillId, Bill, SenateSiteBill> {

    private final SpotCheckUtils spotCheckUtils;
    private final MemberService memberService;

    @Autowired
    public SenateSiteBillCheckService(MemberService memberService, SpotCheckUtils spotCheckUtils) {
        this.memberService = memberService;
        this.spotCheckUtils = spotCheckUtils;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckObservation<BillId> check(Bill content, SenateSiteBill reference) {

        BillId billId = reference.getBillId();

        SpotCheckObservation<BillId> observation = new SpotCheckObservation<>(reference.getReferenceId(), billId);

        BillView contentBillView = new BillView(content);
        BillAmendmentView amendment;
        try {
            Version refVersion = reference.getBillId().getVersion();
            amendment = Optional.of(contentBillView)
                    .map(BillView::getAmendments)
                    .map(MapView::getItems)
                    .map(amendments -> amendments.get(refVersion.toString()))
                    .orElseThrow(() -> new BillAmendNotFoundEx(reference.getBillId()));
        } catch (IllegalArgumentException | BillAmendNotFoundEx ex) {
            if (isUnpublished(billId, content)) {
                observation.addMismatch(new SpotCheckMismatch(BILL_AMENDMENT_PUBLISH,
                        "not published", "published"));
                return observation;
            } else {
                return SpotCheckObservation.getObserveDataMissingObs(reference.getReferenceId(), billId);
            }
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
        checkVotes(contentBillView, reference, observation);

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
        spotCheckUtils.checkString(content.getBasePrintNo(), reference.getBasePrintNo(), observation, BILL_BASE_PRINT_NO);
    }

    private void checkChamber(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        Chamber contentChamber = new BillId(content.getPrintNo(), content.getSession()).getChamber();
        spotCheckUtils.checkObject(contentChamber, StringUtils.upperCase(reference.getChamber()), observation, BILL_CHAMBER);
    }

    private void checkActiveVersion(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkObject(content.getActiveVersion(), StringUtils.upperCase(reference.getActiveVersion()),
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
        spotCheckUtils.checkCollection(olSameAs, refSameAs, observation, BILL_SAME_AS);
    }

    private void checkPrevVersions(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        TreeSet<BillId> olPrevVers = Optional.ofNullable(content.getPreviousVersions())
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(BillIdView::toBillId)
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<BillId> refPrevVers = new TreeSet<>(reference.getPreviousVersions());
        spotCheckUtils.checkCollection(olPrevVers, refPrevVers, observation, BILL_PREVIOUS_VERSIONS);
    }

    private void checkIsAmended(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<PublishStatusView> published = content.getPublishStatusMap().getItems().values().stream()
                .filter(PublishStatusView::isPublished)
                .collect(toList());
        boolean olIsAmended = published.size() > 1;
        boolean refIsAmended = reference.isAmended();
        spotCheckUtils.checkBoolean(olIsAmended, refIsAmended, "Amended", observation, BILL_IS_AMENDED);
    }

    private void checkHasSameAs(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        boolean contentHasSameAs = Optional.ofNullable(content.getSameAs())
                .map(ListView::getSize)
                .orElse(0) > 0;
        spotCheckUtils.checkBoolean(contentHasSameAs, reference.isHasSameAs(), "Has SameAs", observation, BILL_HAS_SAME_AS);
    }

    private void checkPublishDate(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        LocalDateTime olPubDateTime = content.getPublishedDateTime().withNano(0);
        LocalDateTime refPubDateTime = reference.getPublishDate();
        spotCheckUtils.checkObject(olPubDateTime, refPubDateTime, observation, BILL_PUBLISH_DATE);
    }

    private String billActionToString(BillAction action) {
        return action.getSequenceNo() + " " +
                // Only check base print number, don't check the amendment version associated with the action.
                // Public website models this differently causing amendment errors we are not interested in.
                action.getBillId().getBasePrintNo() + " " +
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
                .collect(toList());
        spotCheckUtils.checkCollection(contentActions, reference.getActions(), observation, BILL_ACTION,
                this::billActionToString, "\n");
    }

    private String billStatusToString(BillStatusView status) {
        return status.getBillCalNo() + " " +
                status.getActionDate() + " " +
                status.getCommitteeName() + " " +
                status.getStatusType() + " " +
                status.getStatusDesc();
    }

    private void checkMilestones(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        List<BillStatusView> contentMilestones = Optional.ofNullable(content.getMilestones())
                .map(ListView::getItems)
                .orElse(ImmutableList.of());
        spotCheckUtils.checkCollection(contentMilestones, reference.getMilestones(), observation, BILL_MILESTONES,
                this::billStatusToString, "\n");
    }

    private void checkLastStatus(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String contentStatus = Optional.ofNullable(content.getStatus())
                .map(BillStatusView::getStatusType)
                .orElse(null);
        spotCheckUtils.checkObject(contentStatus, reference.getLastStatus(), observation, BILL_LAST_STATUS);
    }

    private void checkLastStatusComm(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        String contentLastStatusComm = Optional.ofNullable(content.getStatus())
                .map(BillStatusView::getCommitteeName)
                .orElse(null);
        spotCheckUtils.checkStringUpper(contentLastStatusComm, reference.getLatestStatusCommittee(),
                observation, BILL_LAST_STATUS_COMM);
    }

    private void checkLastStatusDate(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        LocalDateTime contentStatusDate = Optional.ofNullable(content.getStatus())
                .map(BillStatusView::getActionDate)
                .map(LocalDate::atStartOfDay)
                .orElse(null);
        spotCheckUtils.checkObject(contentStatusDate, reference.getLastStatusDate(), observation, BILL_LAST_STATUS_DATE);
    }


    private void checkSponsor(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        SessionYear session = observation.getKey().getSession();
        Chamber chamber = observation.getKey().getChamber();
        String contentSponsor = Optional.ofNullable(content.getSponsor())
                .map(SponsorView::getMember)
                .map(m -> spotCheckUtils.getPrimaryShortname(session, m.getMemberId()))
                .orElse(null);
        String refSponsor = spotCheckUtils.getPrimaryShortname(session, chamber, reference.getSponsor());
        spotCheckUtils.checkString(contentSponsor, refSponsor, observation, BILL_SPONSOR);
    }

    private List<String> extractShortnames(SessionYear session, ListView<MemberView> sponsorList) {
        return Optional.ofNullable(sponsorList)
                .map(ListView::getItems)
                .orElse(ImmutableList.of())
                .stream()
                .map(mv -> spotCheckUtils.getPrimaryShortname(session, mv.getMemberId()))
                .collect(toList());
    }

    private void checkCoSponsors(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        SessionYear session = observation.getKey().getSession();
        Chamber chamber = observation.getKey().getChamber();
        List<String> contentCoSponsors = extractShortnames(session, content.getCoSponsors());
        List<String> refCoSponsors = reference.getCoSponsors().stream()
                .map(sn -> spotCheckUtils.getPrimaryShortname(session, chamber, sn))
                .collect(toList());
        spotCheckUtils.checkCollection(contentCoSponsors, refCoSponsors, observation, BILL_COSPONSOR);
    }

    private void checkMultiSponsors(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        SessionYear session = observation.getKey().getSession();
        Chamber chamber = observation.getKey().getChamber();
        List<String> contentMultiSponsors = extractShortnames(session, content.getMultiSponsors());
        List<String> refMultiSponsors = reference.getMultiSponsors().stream()
                .map(sn -> spotCheckUtils.getPrimaryShortname(session, chamber, sn))
                .collect(toList());
        spotCheckUtils.checkCollection(contentMultiSponsors, refMultiSponsors, observation, BILL_MULTISPONSOR);
    }

    private void checkTitle(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkString(content.getTitle(), reference.getTitle(), observation, BILL_TITLE);
    }

    private void checkSummary(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkString(content.getSummary(), reference.getSummary(), observation, BILL_SUMMARY);
    }

    private void checkMemo(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkString(content.getMemo(), reference.getMemo(), observation, BILL_MEMO);
    }

    private void checkText(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkString(content.getFullText(), reference.getText(), observation, BILL_TEXT);
    }

    private void checkLawCode(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkString(content.getLawCode(), reference.getLawCode(), observation, BILL_LAW_CODE);
    }

    private void checkLawSection(BillAmendmentView content, SenateSiteBill reference, SpotCheckObservation<BillId> observation) {
        spotCheckUtils.checkString(content.getLawSection(), reference.getLawSection(), observation, BILL_LAW_SECTION);
    }

    private void checkVotes(BillView content, SenateSiteBill reference, SpotCheckObservation<BillId> obs) {
        BillId billId = reference.getBillId();
        List<SenateSiteBillVote> contentVoteList = content.getVotes().getItems().stream()
                .filter(vote -> vote.getVersion().equalsIgnoreCase(billId.getVersion().toString()))
                .map(SenateSiteBillVote::new)
                .collect(toList());
        TreeMap<BillVoteId, SenateSiteBillVote> contentVoteMap = getVoteMap(contentVoteList);
        TreeMap<BillVoteId, SenateSiteBillVote> refVoteMap = getVoteMap(reference.getVotes());

        Set<BillVoteId> contentVoteInfos = contentVoteMap.keySet();
        Set<BillVoteId> refVoteInfos = refVoteMap.keySet();

        spotCheckUtils.checkCollection(contentVoteInfos, refVoteInfos, obs, BILL_VOTE_INFO,
                this::getVoteInfoString, "\n");

        Set<BillVoteId> intersection = Sets.intersection(contentVoteMap.keySet(), refVoteMap.keySet());

        boolean infoSetsEqual =
                intersection.size() == contentVoteInfos.size() && intersection.size() == refVoteInfos.size();
        // Test to make sure that the set comparison and string comparison get the same result
        if (infoSetsEqual == obs.hasMismatch(BILL_VOTE_INFO)) {
            throw new IllegalStateException(
                    "Vote comparison result differs between set and string representation for bill " + billId);
        }

        // Get lists of content and ref votes that are present in both
        List<SenateSiteBillVote> checkedContentVotes = intersection.stream()
                .map(contentVoteMap::get)
                .collect(toList());

        List<SenateSiteBillVote> checkedRefVotes = intersection.stream()
                .map(refVoteMap::get)
                .collect(toList());

        spotCheckUtils.checkCollection(checkedContentVotes, checkedRefVotes, obs, BILL_VOTE_ROLL, this::getVoteString, "\n");
    }

    /**
     * Return true if an amendment exists for the given bill id and it is unpublished
     */
    private boolean isUnpublished(BillId billId, Bill content) {
        return Optional.ofNullable(content)
                .map(bill -> bill.getAmendPublishStatusMap().get(billId.getVersion()))
                .map(pubStatus -> !pubStatus.isPublished())
                .orElse(false);
    }

    private String getVoteInfoString(BillVoteId voteId) {
        StringBuilder builder = new StringBuilder();
        builder.append(voteId.getVoteType())
                .append(" - ")
                .append(voteId.getVoteDate());
        if (voteId.getVoteType() == COMMITTEE) {
            builder.append(" - ")
                    .append(Optional.ofNullable(voteId.getCommitteeId())
                            .map(CommitteeId::getName)
                            .map(StringUtils::upperCase)
                            .orElse(null)
                    );
        }
        return builder.toString();
    }

    private String getVoteString(SenateSiteBillVote vote) {
        StringBuilder builder = new StringBuilder();
        builder.append(getVoteInfoString(vote.getVoteId()))
                .append("\n");
        for (BillVoteCode code : BillVoteCode.values()) {
            builder.append(code)
                    .append(" votes - count: ")
                    .append(Optional.ofNullable(vote.getVoteCounts().get(code)).orElse(0))
                    .append("\n");
            for (int memberId : vote.getVoteRoll().get(code)) {
                String shortName;
                try {
                    FullMember member = memberService.getMemberById(memberId);
                    shortName = member.getLatestSessionMember()
                            .orElseThrow(MemberNotFoundEx::new)
                            .getLbdcShortName();
                } catch (MemberNotFoundEx ex) {
                    shortName = "Unknown id: " + memberId;
                }
                builder.append("    ")
                        .append(shortName)
                        .append("\n");
            }
        }
        return builder.toString();
    }

    private TreeMap<BillVoteId, SenateSiteBillVote> getVoteMap(Collection<SenateSiteBillVote> votes) {
        return votes.stream()
                .collect(toMap(SenateSiteBillVote::getVoteId, Function.identity(), (a, b) -> b, TreeMap::new));
    }
}
