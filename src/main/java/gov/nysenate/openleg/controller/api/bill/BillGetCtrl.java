package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.bill.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.StringDiffer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static gov.nysenate.openleg.model.bill.BillTextFormat.PLAIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Bill retrieval APIs
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class BillGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillGetCtrl.class);

    @Autowired protected BillDataService billData;
    @Autowired protected BillSearchService billSearch;

    protected enum BillViewLevel
    {
        DEFAULT,                // Basic bill view (models the BillView class)
        INFO,                   // Bill info view
        NO_FULLTEXT,            // Basic bill view with full text stripped
        ONLY_FULLTEXT,          // Only the full text for a specific amendment
        WITH_REFS,              // Bill view with summary views for all related bills
        WITH_REFS_NO_FULLTEXT;  // Bill view with no full text and with summary views for all related bills

        public static BillViewLevel getValue(String type) {
            if (StringUtils.isNotBlank(type)) {
                try {
                    return BillViewLevel.valueOf(type.toUpperCase());
                }
                catch (IllegalArgumentException ex) {
                    return BillViewLevel.DEFAULT;
                }
            }
            else {
                return DEFAULT;
            }
        }
    }

    /**
     * Bill listing API
     * ----------------
     *
     * Retrieve bills for session year: (GET) /api/3/bills/{session}
     * Request Parameters: sort - Lucene syntax for sorting by any field from the bill response.
     *                     full - If true, the full bill view should be returned. Otherwise just the info.
     *                     limit - Limit the number of results.
     *                     offset - Start results from an offset.
     *                     fullTextFormat - String[] - default PLAIN - desired formats for bill text
     *
     * Expected Output: List of BillInfoView or BillView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}")
    public BaseResponse getBills(@PathVariable int sessionYear,
                                 @RequestParam(defaultValue = "publishedDateTime:asc") String sort,
                                 @RequestParam(defaultValue = "false") boolean full,
                                 @RequestParam(defaultValue = "false") boolean idsOnly,
                                 WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<BaseBillId> results =
            billSearch.searchBills(getSessionYearParam(sessionYear, "sessionYear"), sort, limOff);
        // The bill data is retrieved from the data service so the data is always fresh.
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> {
                    BaseBillId baseBillId = r.getResult();
                    if (idsOnly) {
                        return new BaseBillIdView(baseBillId);
                    }
                    if (full) {
                        return new BillView(billData.getBill(baseBillId), getFullTextFormats(webRequest));
                    }
                    return new BillInfoView(billData.getBillInfo(baseBillId));
                })
                .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    /**
     * Single Bill retrieval API
     * -------------------------
     *
     * Retrieve a single bill via printNo and session: (GET) /api/3/bills/{session}/{printNo}/
     * The version on the printNo is not needed since bills are returned with all amendments.
     *
     * Request Parameters: view - Specify the level of detail (defaults to BillViewLevel.DEFAULT)
     *                     fullTextFormat - String[] - default PLAIN - desired formats for bill text
     *
     * Expected Output: BillView, DetailedBillView, or BillInfoView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public BaseResponse getBill(@PathVariable int sessionYear, @PathVariable String printNo, WebRequest request) {
        BaseBillId baseBillId = getBaseBillId(printNo, sessionYear, "printNo");
        BillViewLevel level = BillViewLevel.getValue(request.getParameter("view"));
        ViewObject viewObject;
        LinkedHashSet<BillTextFormat> fullTextFormats = getFullTextFormats(request);
        switch (level) {
            case INFO:
                viewObject = new BillInfoView(billData.getBillInfo(baseBillId));
                break;
            case WITH_REFS:
                viewObject = new DetailBillView(billData.getBill(baseBillId), billData, fullTextFormats);
                break;
            case NO_FULLTEXT:
                viewObject = new BillView(getFullTextStrippedBill(baseBillId), new HashSet<>());
                break;
            case WITH_REFS_NO_FULLTEXT:
                viewObject = new DetailBillView(getFullTextStrippedBill(baseBillId), billData, new HashSet<>());
                break;
            case ONLY_FULLTEXT: {
                Version amdVersion = Version.ORIGINAL;
                if (request.getParameter("version") != null) {
                    amdVersion = parseVersion(request.getParameter("version"), "version");
                }
                BillTextFormat firstFormat = fullTextFormats.stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("No bill text formats available!"));
                Bill bill = billData.getBill(baseBillId);

                String fullText = bill.getAmendment(amdVersion).getFullText(firstFormat);
                viewObject = new BillFullTextView(bill.getBaseBillId(), amdVersion.toString(),
                        fullText, firstFormat);
                break;
            }
            default: viewObject = new BillView(billData.getBill(baseBillId), fullTextFormats);
        }
        return new ViewObjectResponse<>(viewObject, "Data for bill " + baseBillId);
    }

    /**
     * Returns a Bill with the full text removed.
     * @param baseBillId BaseBillId
     * @return Bill
     */
    private Bill getFullTextStrippedBill(BaseBillId baseBillId) {
        return billData.getBill(baseBillId);
    }

    /**
     * Single Bill PDF retrieval API
     * -----------------------------
     *
     * Retrieve a single bill amendment full text: (GET) /api/3/bills/{session}/{printNo}.pdf
     * The version on the printNo will dictate which full text to output.
     *
     * Request Parameters: None
     *
     * Expected Output: PDF response
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}.pdf")
    public ResponseEntity<byte[]> getBillPdf(@PathVariable int sessionYear, @PathVariable String printNo)
                           throws Exception {
        BillId billId = getBillId(printNo, sessionYear, "printNo");
        Bill bill = billData.getBill(BaseBillId.of(billId));
        ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
        BillPdfView.writeBillPdf(bill, billId.getVersion(), pdfBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
    }

    /**
     * Bill Diff API
     * -------------
     *
     * Returns an html diff between 'version1' and 'version2' of a given bill.
     *
     * TODO: Handle case with default amendment. Or rather make it so that it's possible to diff any two bills.
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}/diff/{version1}/{version2}")
    public BaseResponse getBillDiff(@PathVariable int sessionYear, @PathVariable String printNo, @PathVariable String version1,
                            @PathVariable String version2) {
        StringDiffer stringDiffer = new StringDiffer();
        BaseBillId baseBillId = getBaseBillId(printNo, sessionYear, "printNo");
        Bill bill = billData.getBill(baseBillId);
        BillAmendment amend1 = bill.getAmendment(parseVersion(version1, "version1"));
        BillAmendment amend2 = bill.getAmendment(parseVersion(version2, "version2"));
        String fullText1 = BillTextUtils.getPlainTextWithoutLineNumbers(amend1);
        String fullText2 = BillTextUtils.getPlainTextWithoutLineNumbers(amend2);
        LinkedList<StringDiffer.Diff> diffs = stringDiffer.diff_main(fullText1, fullText2);
        stringDiffer.diff_cleanupEfficiency(diffs);
        stringDiffer.diff_cleanupSemantic(diffs);
        stringDiffer.diff_cleanupMerge(diffs);
        String prettyHtml = stringDiffer.diff_prettyHtml(diffs).replace("&para;", " ");
        return new ViewObjectResponse<>(
            new BillDiffView(
                new BaseBillIdView(baseBillId), amend1.getVersion().toString(), amend2.getVersion().toString(),
                    prettyHtml));
    }


    /** --- Exception Handlers --- */

    @ExceptionHandler(BillNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ViewObjectErrorResponse billNotFoundHandler(BillNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.BILL_NOT_FOUND, new BillIdView(ex.getBillId()));
    }

    @ExceptionHandler(BillAmendNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ViewObjectErrorResponse billAmendNotFoundHandler(BillAmendNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.BILL_AMENDMENT_NOT_FOUND, new BillIdView(ex.getBillId()));
    }
}