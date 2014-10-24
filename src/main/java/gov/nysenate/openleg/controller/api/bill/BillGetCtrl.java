package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.*;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
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
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private BillDataService billDataService;

    @Autowired
    private BillSearchService billSearchService;

    /**
     * Bill listing API
     *
     * Retrieve bills for session year: /api/3/bills/{sessionYear}
     * Request Parameters: sort - Lucene syntax for sorting by any field from the bill response.
     *                     full - If true, the full bill view should be returned. Otherwise just the info.
     *                     limit - Limit the number of results.
     *                     offset - Start results from an offset.
     *
     * Expected Output: List of BillInfoView or BillView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}")
    public BaseResponse getBills(@PathVariable int sessionYear,
                                 @RequestParam(defaultValue = "printNo:asc") String sort,
                                 @RequestParam(defaultValue = "false") boolean full,
                                 WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.FIFTY);
        // We're using the search service to get the listing to take advantage of the sorting features.
        SearchResults<BaseBillId> results =
            billSearchService.searchBills("session:" + SessionYear.of(sessionYear).toString(), sort, limOff);
        // The bill data is retrieved from the data service to minimize chances of un-synchronized data.
        return ListViewResponse.of(
            results.getResults().parallelStream()
                .map(r -> (full) ? new BillView(billDataService.getBill(r.getResult()))
                                 : new BillInfoView(billDataService.getBillInfo(r.getResult())))
                .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    /**
     * Bill retrieval API
     *
     * Retrieve a single bill via printNo and sessionYear: /api/3/bills/{sessionYear}/{printNo}/
     * The version on the printNo is not needed since bills are returned with all amendments.
     *
     * Request Parameters: None
     *
     * Expected Output: BillView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public BaseResponse getBill(@PathVariable int sessionYear, @PathVariable String printNo) {
        return new ViewObjectResponse<>(new BillView(
            billDataService.getBill(new BaseBillId(printNo, sessionYear))));
    }

    /**
     * Bill PDF retrieval API
     *
     * Retrieve a single bill amendment full text: /api/3/bills/{sessionYear}/{printNo}.pdf
     * The version on the printNo will dictate which full text to output.
     *
     * Request Parameters: None
     *
     * Expected Output: PDF response
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}.pdf")
    public void getBillPdf(@PathVariable int sessionYear, @PathVariable String printNo, HttpServletResponse response) throws Exception {
        BillId billId = new BillId(printNo, sessionYear);
        Bill bill = billDataService.getBill(BaseBillId.of(billId));
        new BillPdfView(bill, billId.getVersion(), response.getOutputStream());
        response.setContentType("application/pdf");
    }
}