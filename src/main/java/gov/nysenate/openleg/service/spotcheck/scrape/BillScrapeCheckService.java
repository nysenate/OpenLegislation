package gov.nysenate.openleg.service.spotcheck.scrape;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeReference;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeVote;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import gov.nysenate.openleg.util.BillTextUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.bill.BillTextFormat.HTML;
import static gov.nysenate.openleg.model.bill.BillTextFormat.PLAIN;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

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
                // TODO remove session check when we get xml bill text for previous years
                if (bill.getSession().getYear() >= 2017) {
                    checkHtmlBillText(amendment, reference, observation);
                }
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
     * Cleans unnecessary elements e.g. style from observation html bill text
     */
    private String cleanHtml(String rawHtml) {
        if (StringUtils.isBlank(rawHtml)) {
            return "";
        }
        Document doc = Jsoup.parse(rawHtml);
        Elements billTextElements = doc.getElementsByTag("pre");
        String preHtml = billTextElements.html();
        return preHtml
                .replaceAll("\r\n", "\n")
                .replaceAll(" +(?=$|\n)", "");
    }

    /**
     * Check the html version of bill text.
     */
    private void checkHtmlBillText(BillAmendment amend, BillScrapeReference reference,
                                   SpotCheckObservation<BaseBillId> obs) {
        ensureTextFormatExists(amend, HTML);
        String contentHtmlText = cleanHtml(Optional.ofNullable(amend.getFullText(HTML)).orElse(""));
        String refHtmlText = cleanHtml(reference.getHtmlText());
        spotCheckUtils.checkString(contentHtmlText, refHtmlText, obs, BILL_HTML_TEXT);
    }

    /**
     * Checks text with all whitespace removed, and generates several mismatches with different levels of text
     * normalization if there was a mismatch in the no-whitespace text
     */
    private void checkBillText(BillAmendment billAmendment, BillScrapeReference reference, SpotCheckObservation<BaseBillId> obsrv){
        ensureTextFormatExists(billAmendment, PLAIN);
        String dataText = Optional.ofNullable(billAmendment.getFullText(PLAIN)).orElse("");
        String refText = reference.getText();
        String strippedDataText = basicNormalize(dataText);
        String strippedRefText = basicNormalize(refText);
        // Check normalized text and report on non-normalized text as well if there is a mismatch
        if (!StringUtils.equals(strippedRefText, strippedDataText)) {
            // If its a resolution, check if its a header problem
            if (billAmendment.getBillId().getBillType().isResolution()) {
                // Try removing the resolution header from the ref in case we are checking against sobi data
                String refTextNoHeader = basicNormalize(BillTextUtils.formatHtmlExtractedResoText(refText));
                if (StringUtils.equals(strippedDataText, refTextNoHeader)) {
                    // todo remove this when we have bill text for all sobi years.
                    return;
                }
                // Try stripping the data header as well, to see if the header is the only issue.
                String dataTextNoHeader = basicNormalize(BillTextUtils.formatHtmlExtractedResoText(dataText));
                if (StringUtils.equals(refTextNoHeader, dataTextNoHeader)) {
                    obsrv.addMismatch(new SpotCheckMismatch(BILL_TEXT_RESO_HEADER, dataText, refText));
                    return;
                }
            }

            String pureContentRefText = ultraNormalize(refText);
            String pureContentDataText = ultraNormalize(dataText);
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
     * Performs a simple normalization to eliminate potential for mismatches that we would never care about.
     *
     * Removes all non alpha characters
     * Replace section symbol(§) with S
     * CAPITALIZE EVERYTHING.
     */
    private String basicNormalize(String text) {
        return Optional.ofNullable(text).orElse("")
                .replaceAll("§", "S")
                .replaceAll("(?:[^\\w]|_)+", "")
                .toUpperCase();
    }

    private static final String lineNumberRegex = "(?:^( {4}\\d| {3}\\d\\d))";
    private static final String pageMarkerRegex = "^ {7}[A|S]\\. \\d+(--[A-Z])?[ ]+\\d+([ ]+[A|S]\\. \\d+(--[A-Z])?)?$";
    private static final String budgetPageMargerRegex = "^[ ]{42,43}\\d+[ ]+\\d+-\\d+-\\d+$";
    private static final String explanationRegex = "^[ ]+EXPLANATION--Matter in ITALICS \\(underscored\\) is new; matter in brackets\\n";
    private static final String explanationRegex2 = "^[ ]+\\[ ] is old law to be omitted.\\n[ ]+LBD\\d+-\\d+-\\d+$";
    private static final String ultraNormalizeRegex = "(?m)" + String.join("|", Arrays.asList(
            lineNumberRegex, pageMarkerRegex, budgetPageMargerRegex, explanationRegex, explanationRegex2));
    /**
     * Performs a more advanced normalization of text,
     * removing specific sections that do not contribute to overall content.
     *
     * Removes all whitespace, line numbers, and page numbers
     * also performs {@link #basicNormalize(String)}
     */
    private String ultraNormalize(String text) {
        String stripped = Optional.ofNullable(text).orElse("").replaceAll(ultraNormalizeRegex, "");
        return basicNormalize(stripped);
    }

    private void ensureTextFormatExists(BillAmendment billAmendment, BillTextFormat format) {
        if (!billAmendment.isTextFormatLoaded(format)) {
            throw new IllegalStateException("Bill text format " + format +
                    " is not represented in bill reference for " + billAmendment.getBillId());
        }
    }
}
