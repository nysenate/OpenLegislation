package gov.nysenate.openleg.service.spotcheck.scrape;

import com.google.common.collect.*;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeReference;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeVote;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by kyle on 2/19/15.
 */
@Service
public class BillScrapeCheckService implements SpotCheckService<BaseBillId, Bill, BillScrapeReference>{
    private static final Logger logger = LogManager.getLogger(BillScrapeCheckService.class);

    @Autowired
    BillDataService billDataService;

    @PostConstruct
    public void init(){

    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, BillScrapeReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("BillScrapeSpotcheckReference cannot be null when performing spot check");
        }

        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<>(reference.getReferenceId(), bill.getBaseBillId());

        //Add mismatches to observation

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
        String dataText = billAmendment.getFullText();
        String refText = reference.getText();
        String strippedDataText = stripNonAlpha(dataText);
        String strippedRefText = stripNonAlpha(refText);
        // Check normalized text and report on non-normalized text as well if there is a mismatch
        if (!StringUtils.equals(strippedRefText, strippedDataText)) {
            String pureContentRefText = stripNonContent(refText);
            String pureContentDataText = stripNonContent(dataText);
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

        if (!Sets.symmetricDifference(referenceVotes, openlegVotes).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.BILL_SCRAPE_VOTE,
                    openlegVotes.toString(), referenceVotes.toString()));
        }
    }

    private Set<BillScrapeVote> createOpenlegVotes(Bill bill) {
        Set<BillScrapeVote> votes = new HashSet<>();
        for (BillVote vote : fetchBillFloorVotes(bill)) {
            SortedSetMultimap<BillVoteCode, String> voteMultiList = TreeMultimap.create();
            LocalDate voteDate = vote.getVoteDate();
            for (BillVoteCode code : vote.getMemberVotes().keySet()) {
                for (SessionMember member : vote.getMembersByVote(code)) {
                    voteMultiList.put(code, member.getLastName());
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
                .collect(Collectors.toList());
    }

    private void checkMemoText(BillAmendment billAmendment, BillScrapeReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataMemo = billAmendment.getMemo();
        String refMemo = reference.getMemo();
        if (!StringUtils.equalsIgnoreCase(dataMemo, refMemo)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_MEMO, dataMemo, refMemo));
        }
    }

    /**
     * Removes all non alpha characters
     */
    private String stripNonAlpha(String text) {
        return text.replaceAll("(?:[^\\w]|_)+", "");
    }

    private static final String lineNumberRegex = "(?:^( {4}\\d| {3}\\d\\d))";
    private static final String pageMarkerRegex = "^ {7}[A|S]\\. \\d+(--[A-Z])?[ ]+\\d+([ ]+[A|S]\\. \\d+(--[A-Z])?)?$";
    private static final String budgetPageMargerRegex = "^[ ]{42,43}\\d+[ ]+\\d+-\\d+-\\d+$";
    private static final String explanationRegex = "^[ ]+EXPLANATION--Matter in ITALICS \\(underscored\\) is new; matter in brackets\\n";
    private static final String explanationRegex2 = "^[ ]+\\[ ] is old law to be omitted.\\n[ ]+LBD\\d+-\\d+-\\d+$";
    private static final String ultraNormalizeRegex = "(?m)" + String.join("|", Arrays.asList(
            lineNumberRegex, pageMarkerRegex, budgetPageMargerRegex, explanationRegex, explanationRegex2));
    /**
     * Removes all whitespace, line numbers, and page numbers
     */
    private String stripNonContent(String text) {
        String stripped = text.replaceAll(ultraNormalizeRegex, "");
        return stripNonAlpha(stripped);
    }

}
