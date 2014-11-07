package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET)
public class BillSearchCtrl extends BillBaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillSearchCtrl.class);

    /** --- Request Handlers --- */

    /**
     * Bill Search API
     *
     * Search all bills:    (GET) /api/3/bills/search
     * Request Parameters:  term - The lucene query string
     *                      sort - The lucene sort string (blank by default)
     *                      full - Set to true to retrieve full bill responses (false by default)
     *                      limit - Limit the number of results (default 25)
     *                      offset - Start results from offset
     */
    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam(required = true) String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.TWENTY_FIVE);
        SearchResults<BaseBillId> results = billSearch.searchBills(term, sort, limOff);
        return getBillSearchResponse(results, full, limOff);
    }

    /**
     * Bill Search By Session API
     *
     * Search all bills in a given session year: (GET) /api/3/bills/{session}/search
     * @see #globalSearch for request params
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/search")
    public BaseResponse sessionSearch(@PathVariable int sessionYear,
                                      @RequestParam(required = true) String term,
                                      @RequestParam(defaultValue = "") String sort,
                                      @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.TWENTY_FIVE);
        SearchResults<BaseBillId> results = billSearch.searchBills(term, SessionYear.of(sessionYear), sort, limOff);
        return getBillSearchResponse(results, full, limOff);
    }

    /** --- Internal --- */

    private BaseResponse getBillSearchResponse(SearchResults<BaseBillId> results, boolean full, LimitOffset limOff) {
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> new SearchResultView((full)
                        ? new BillView(billData.getBill(r.getResult()))
                        : new BillInfoView(billData.getBillInfo(r.getResult())), r.getRank()))
                .collect(toList()), results.getTotalResults(), limOff);
    }
}