package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.*;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET)
public class BillGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillGetCtrl.class);

    @Autowired
    private BillDataService billDataService;

    @Autowired
    private BillSearchService billSearchService;

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}")
    public BaseResponse getBills(@PathVariable int sessionYear,
                                 @RequestParam(defaultValue = "printNo:asc") String sort,
                                 @RequestParam(defaultValue = "false") boolean full, WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.FIFTY);
        SearchResults<BaseBillId> results =
            billSearchService.searchBills("session:" + SessionYear.of(sessionYear).toString(), sort, limOff);
        return ListViewResponse.of(
            results.getResults().parallelStream()
                .map(r -> (full) ? new BillView(billDataService.getBill(r.getResult()))
                                 : new BillInfoView(billDataService.getBillInfo(r.getResult())))
                .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public BaseResponse getBill(@PathVariable int sessionYear, @PathVariable String printNo) {
        return new ViewObjectResponse<>(new BillView(
            billDataService.getBill(new BaseBillId(printNo, sessionYear))));
    }
}