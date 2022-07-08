package gov.nysenate.openleg.api.legislation.bill;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillInfoView;
import gov.nysenate.openleg.api.legislation.bill.view.BillView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillTextFormat;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.bill.BillSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Set;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Bill Search API
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class BillSearchCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillSearchCtrl.class);

    @Autowired protected BillDataService billData;
    @Autowired protected BillSearchService billSearch;

    /**
     * Bill Search API
     * ---------------
     *
     * Search all bills:    (GET) /api/3/bills/search
     * Request Parameters:  term - The lucene query string
     *                      sort - The lucene sort string (blank by default)
     *                      full - Set to true to retrieve full bill responses (false by default)
     *                      fullTextFormat - Which texts will be included in responses if full is true.
     *                      limit - Limit the number of results (default 25)
     *                      offset - Start results from offset
     */
    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam(required = true) String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     @RequestParam(defaultValue = "false") boolean idOnly,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<BaseBillId> results = billSearch.searchBills(term, sort, limOff);
        return getBillSearchResponse(results, full, idOnly, limOff, webRequest);
    }

    /**
     * Bill Search By Session API
     * --------------------------
     *
     * Search all bills in a given session year: (GET) /api/3/bills/{session}/search
     * @see #globalSearch for request params
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/search")
    public BaseResponse sessionSearch(@PathVariable int sessionYear,
                                      @RequestParam(required = true) String term,
                                      @RequestParam(defaultValue = "") String sort,
                                      @RequestParam(defaultValue = "false") boolean full,
                                      @RequestParam(defaultValue = "false") boolean idOnly,
                                      WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SessionYear session = getSessionYearParam(sessionYear, "sessionYear");
        SearchResults<BaseBillId> results = billSearch.searchBills(term, session, sort, limOff);
        return getBillSearchResponse(results, full, idOnly, limOff, webRequest);
    }

    /** --- Internal --- */

    private BaseResponse getBillSearchResponse(SearchResults<BaseBillId> results,
                                               boolean full, boolean idOnly,
                                               LimitOffset limOff,
                                               WebRequest request) {
        Set<BillTextFormat> fullTextFormats = getFullTextFormats(request);
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> new SearchResultView((full)
                        ? new BillView(billData.getBill(r.getResult()), fullTextFormats)
                        : (idOnly)
                            ? new BillIdView(r.getResult())
                            : new BillInfoView(billData.getBillInfo(r.getResult())), r.getRank(), r.getHighlights()))
                .toList(), results.getTotalResults(), limOff);
    }
}