package gov.nysenate.openleg.spotchecks.scraping.lrs.bill;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.common.util.BillTextCheckUtils;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.bill.utils.BillTextUtils;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.spotchecks.base.SpotCheckService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckUtils;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.openleg.legislation.bill.BillTextFormat.PLAIN;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.*;

/**
 * Created by kyle on 2/19/15.
 */
@Service
public class BillScrapeCheckService implements SpotCheckService<BaseBillId, Bill, BillScrapeReference> {

    @Autowired private SpotCheckUtils spotCheckUtils;
    @Autowired private BillScrapeVoteMismatchService voteMismatchService;

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, BillScrapeReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("BillScrapeSpotcheckReference cannot be null when performing spot check");
        }

        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<>(reference.getReferenceId(), bill.getBaseBillId());

        // Add mismatches to observation

        // If not found on LRS and not published in openleg, don't create mismatch. LRS removes bills when unpublished.
        Optional<PublishStatus> publishStatus = bill.getPublishStatus(bill.getActiveVersion());
        if (reference.isNotFound() && publishStatus.isPresent() && !publishStatus.get().isPublished()) {
            return observation;
        }
        else if (reference.isNotFound()) {
            observation.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, "", reference.getText()));
        } else {
            checkAmendment(bill, reference, observation);
            checkVotes(bill, reference, observation);
            if (bill.hasAmendment(reference.getActiveVersion())) {
                BillAmendment amendment = bill.getAmendment(reference.getActiveVersion());
                checkBillText(amendment, reference, observation);
                // Only check senate, non-resolution bills for sponsor memos
                // Todo find a better way of checking memo text
                //  currently, memos are sent daily in batches and are not guaranteed to be present in sobi data if on lrs
                //  also, memos are formatted a bit differently
//                if (Chamber.SENATE.equals(baseBillId.getChamber()) && !baseBillId.getBillType().isResolution()) {
//                    checkMemoText(amendment, reference, observation);
//                }
            }
        }
        return observation;
    }

    private void checkAmendment(Bill bill, BillScrapeReference reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (bill.getActiveVersion() == null || !bill.getActiveVersion().equals(reference.getActiveVersion())) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_AMENDMENT,
                    bill.getActiveVersion(), reference.getActiveVersion()));
        }
    }

    /**
     * Checks text with all whitespace removed, and generates several mismatches with different levels of text
     * normalization if there was a mismatch in the no-whitespace text
     */
    private void checkBillText(BillAmendment billAmendment, BillScrapeReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataText = billAmendment.getFullText(PLAIN);
        String refText = reference.getText();
        String strippedDataText = BillTextCheckUtils.basicNormalize(dataText);
        String strippedRefText = BillTextCheckUtils.basicNormalize(refText);
        // Check normalized text and report on non-normalized text as well if there is a mismatch
        if (!StringUtils.equals(strippedRefText, strippedDataText)) {
            // If its a resolution, check if its a header problem
            if (billAmendment.getBillId().getBillType().isResolution()) {
                // Try removing the resolution header from the ref in case we are checking against sobi data
                String refTextNoHeader = BillTextCheckUtils.basicNormalize(BillTextUtils.formatHtmlExtractedResoText(refText));
                if (StringUtils.equals(strippedDataText, refTextNoHeader)) {
                    // todo remove this when we have bill text for all sobi years.
                    return;
                }
                // Try stripping the data header as well, to see if the header is the only issue.
                String dataTextNoHeader = BillTextCheckUtils.basicNormalize(BillTextUtils.formatHtmlExtractedResoText(dataText));
                if (StringUtils.equals(refTextNoHeader, dataTextNoHeader)) {
                    obsrv.addMismatch(new SpotCheckMismatch(BILL_TEXT_RESO_HEADER, dataText, refText));
                    return;
                }
            }

            String pureContentRefText = BillTextCheckUtils.ultraNormalize(refText);
            String pureContentDataText = BillTextCheckUtils.ultraNormalize(dataText);
            if (!StringUtils.equals(pureContentRefText, pureContentDataText)) {
                obsrv.addMismatch(new SpotCheckMismatch(BILL_TEXT_CONTENT, dataText, refText));
            } else {
                obsrv.addMismatch(new SpotCheckMismatch(BILL_TEXT_LINE_OFFSET, dataText, refText));
            }
        }
    }

    private void checkVotes(Bill bill, BillScrapeReference reference, SpotCheckObservation<BaseBillId> observation) {
        Set<BillScrapeVote> referenceVotes = reference.getVotes();
        Set<BillScrapeVote> openlegVotes = createOpenlegVotes(bill);

        voteMismatchService.compareVotes(openlegVotes, referenceVotes)
                .ifPresent(observation::addMismatch);
    }

    private Set<BillScrapeVote> createOpenlegVotes(Bill bill) {
        Set<BillScrapeVote> votes = new HashSet<>();
        for (BillVote vote : fetchBillFloorVotes(bill)) {
            SortedSetMultimap<BillVoteCode, String> voteMultiList = TreeMultimap.create();
            LocalDate voteDate = vote.getVoteDate();
            for (BillVoteCode code : vote.getMemberVotes().keySet()) {
                for (SessionMember sessionMember : vote.getMembersByVote(code)) {
                    voteMultiList.put(code, sessionMember.getMember().getPerson().name().lastName());
                }
            }
            BillScrapeVote v = new BillScrapeVote(voteDate, voteMultiList);
            votes.add(v);
        }
        return votes;
    }

    // Returns floor votes from all amendments of this bill.
    private List<BillVote> fetchBillFloorVotes(Bill bill) {
        return bill.getAmendmentList().stream()
                .map(BillAmendment::getVotesList)
                .flatMap(Collection::stream)
                .filter(v -> v.getVoteType() == BillVoteType.FLOOR)
                .toList();
    }

    private void checkMemoText(BillAmendment billAmendment, BillScrapeReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataMemo = billAmendment.getMemo();
        String refMemo = reference.getMemo();
        if (!StringUtils.equalsIgnoreCase(dataMemo, refMemo)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_MEMO, dataMemo, refMemo));
        }
    }
}
