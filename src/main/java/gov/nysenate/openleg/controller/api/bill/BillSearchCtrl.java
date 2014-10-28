package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.search.BillSearchDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET)
public class BillSearchCtrl extends BillBaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillSearchCtrl.class);

    /** --- Request Handlers --- */

    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam(required = true) String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.TWENTY_FIVE);
        SearchResults<BaseBillId> results = billSearch.searchBills(term, sort, limOff);
        return getBillSearchResponse(results, full, limOff);
    }

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/search")
    public BaseResponse sessionSearch(@PathVariable int sessionYear,
                                      @RequestParam(required = true) String term,
                                      @RequestParam(defaultValue = "") String sort,
                                      @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.TWENTY_FIVE);
        SearchResults<BaseBillId> results = billSearch.searchBills(term, sessionYear, sort, limOff);
        return getBillSearchResponse(results, full, limOff);
    }

    protected BaseResponse getBillSearchResponse(SearchResults<BaseBillId> results, boolean full, LimitOffset limOff) {
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> new SearchResultView((full)
                    ? new BillView(billData.getBill(r.getResult()))
                    : new BillInfoView(billData.getBillInfo(r.getResult())), r.getRank()))
                .collect(toList()), results.getTotalResults(), limOff);
    }
}