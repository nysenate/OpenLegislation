package gov.nysenate.openleg.controller.api.bill;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.bill.BillUpdateDigestView;
import gov.nysenate.openleg.client.view.bill.BillUpdateTokenView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillUpdatesDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateToken;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Bill Updates API
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class BillUpdatesCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillUpdatesCtrl.class);

    @Autowired protected BillDataService billData;
    @Autowired protected BillUpdatesDao billUpdatesDao;

    /**
     * Updated Bills API
     * -----------------
     *
     * Return a list of bill ids that have changed on or after the specified date.
     * Usage: (GET) /api/3/bills/updates/{from datetime}
     *
     * Expected Output: List of BillUpdateTokenView
     *
     * @see #getUpdatesDuring(java.time.LocalDateTime, java.time.LocalDateTime)
     */
    @RequestMapping(value = "/updates/{from}")
    public BaseResponse getUpdatesDuring(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                         WebRequest request) {
        return getUpdatesDuring(from, DateUtils.THE_FUTURE.atStartOfDay(), request);
    }

    /**
     * Updated Bills API
     * -----------------
     *
     * Return a list of bill ids that have changed during a specified date/time range.
     * Usage: (GET) /api/3/bills/updates/{from datetime}/{to datetime}
     *
     * Expected Output: List of BillUpdateTokenView
     */
    @RequestMapping(value = "/updates/{from}/{to}")
    public BaseResponse getUpdatesDuring(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                         @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                         WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 100);
        PaginatedList<BillUpdateToken> updateTokens = billUpdatesDao.billsUpdatedDuring(Range.closedOpen(from, to), SortOrder.ASC, limOff);
        return ListViewResponse.of(
            updateTokens.getResults().stream()
                .map(BillUpdateTokenView::new)
                .collect(Collectors.toList()), updateTokens.getTotal(), limOff);

    }

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}/updates")
    public Object getUpdatesForBill(@PathVariable int sessionYear, @PathVariable String printNo) {
        return getUpdatesForBillDuring(sessionYear, printNo, DateUtils.LONG_AGO.atStartOfDay(), DateUtils.THE_FUTURE.atStartOfDay());
    }

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}/updates/{from}/{to}")
    public Object getUpdatesForBillDuring(@PathVariable int sessionYear, @PathVariable String printNo,
                                          @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                          @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<BillUpdateDigestView> digests =
           billUpdatesDao.getUpdateDigest(new BaseBillId(printNo, sessionYear), Range.openClosed(from, to), SortOrder.ASC)
                .stream().map(BillUpdateDigestView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(digests, digests.size(), LimitOffset.ALL);
    }
}
