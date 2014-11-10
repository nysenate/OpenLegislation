package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.BillPdfView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

/**
 * Bill retrieval APIs
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET)
public class BillGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillGetCtrl.class);

    @Autowired
    protected BillDataService billData;

    @Autowired
    protected BillSearchService billSearch;

    /**
     * Bill listing API
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
                                 @RequestParam(defaultValue = "actionDate:desc") String sort,
                                 @RequestParam(defaultValue = "false") boolean full,
                                 WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.FIFTY);
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
     *
     * Retrieve a single bill via printNo and session: (GET) /api/3/bills/{session}/{printNo}/
     * The version on the printNo is not needed since bills are returned with all amendments.
     *
     * Request Parameters: None
     *
     * Expected Output: BillView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public BaseResponse getBill(@PathVariable int sessionYear, @PathVariable String printNo) {
        BaseBillId baseBillId = new BaseBillId(printNo, sessionYear);
        return new ViewObjectResponse<>(new BillView(billData.getBill(baseBillId)), "Data for bill " + baseBillId);
    }

    /**
     * Single Bill PDF retrieval API
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

    /** --- Exception Handlers --- */

    @ExceptionHandler(BillNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ViewObjectErrorResponse billNotFoundHandler(BillNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.BILL_NOT_FOUND, new BillIdView(ex.getBillId()));
    }
}