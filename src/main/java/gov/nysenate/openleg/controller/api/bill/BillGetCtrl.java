package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.base.ModelView;
import gov.nysenate.openleg.client.view.bill.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.OutputUtils;
import gov.nysenate.openleg.util.StringDiffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
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

    /**
     * Bill listing API
     * ----------------
     *
     * Retrieve bills for session year: (GET) /api/3/bills/{session}
     * Request Parameters: sort - Lucene syntax for sorting by any field from the bill response.
     *                     full - If true, the full bill view should be returned. Otherwise just the info.
     *                     limit - Limit the number of results.
     *                     offset - Start results from an offset.
     *
     * Expected Output: List of BillInfoView or BillView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}")
    public BaseResponse getBills(@PathVariable int sessionYear,
                                 @RequestParam(defaultValue = "status.actionDate:desc") String sort,
                                 @RequestParam(defaultValue = "false") boolean full,
                                 WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<BaseBillId> results =
            billSearch.searchBills(SessionYear.of(sessionYear), sort, limOff);
        // The bill data is retrieved from the data service so the data is always fresh.
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> (full) ? new BillView(billData.getBill(r.getResult()))
                        : new BillInfoView(billData.getBillInfo(r.getResult())))
                .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    /**
     * Single Bill retrieval API
     * -------------------------
     *
     * Retrieve a single bill via printNo and session: (GET) /api/3/bills/{session}/{printNo}/
     * The version on the printNo is not needed since bills are returned with all amendments.
     *
     * Request Parameters: summary - If true, then only a BillInfoView will be returned.
     *                     detail - If true, then a DetailedBillView will be returned.
     *
     * Can't use 'summary' and 'detail' at the same time. If 'summary' is true, it will disregard 'detail'.
     *
     * Expected Output: BillView, DetailedBillView, or BillInfoView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public BaseResponse getBill(@PathVariable int sessionYear, @PathVariable String printNo,
                                @RequestParam(defaultValue = "false") boolean summary,
                                @RequestParam(defaultValue = "false") boolean detail) {
        BaseBillId baseBillId = new BaseBillId(printNo, sessionYear);
        return new ViewObjectResponse<>(
            (summary)
                ? new BillInfoView(billData.getBillInfo(baseBillId))
                : (detail)
                    ? new DetailBillView(billData.getBill(baseBillId), billData)
                    : new BillView(billData.getBill(baseBillId)),
            "Data for bill " + baseBillId);
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
    public void getBillPdf(@PathVariable int sessionYear, @PathVariable String printNo, HttpServletResponse response) throws Exception {
        BillId billId = new BillId(printNo, sessionYear);
        Bill bill = billData.getBill(BaseBillId.of(billId));
        new BillPdfView(bill, billId.getVersion(), response.getOutputStream());
        response.setContentType("application/pdf");
    }

    /**
     * Bill Diff API
     * -------------
     *
     *
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}/diff/{version1}/{version2}")
    public BaseResponse getBillDiff(@PathVariable int sessionYear, @PathVariable String printNo, @PathVariable String version1,
                            @PathVariable String version2) {
        StringDiffer stringDiffer = new StringDiffer();
        BaseBillId baseBillId = new BaseBillId(printNo, sessionYear);
        Bill bill = billData.getBill(baseBillId);
        BillAmendment amend1 = bill.getAmendment(Version.of(version1));
        BillAmendment amend2 = bill.getAmendment(Version.of(version2));
        String fullText1 = BillTextUtils.formatBillText(bill.isResolution(), amend1.getFullText());
        String fullText2 = BillTextUtils.formatBillText(bill.isResolution(), amend2.getFullText());
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
}