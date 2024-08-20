package gov.nysenate.openleg.api.updates.bill;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.legislation.bill.view.BaseBillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillView;
import gov.nysenate.openleg.api.legislation.bill.view.SimpleBillInfoView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.DateRangeListViewResponse;
import gov.nysenate.openleg.api.updates.view.UpdateDigestModelView;
import gov.nysenate.openleg.api.updates.view.UpdateDigestView;
import gov.nysenate.openleg.api.updates.view.UpdateTokenModelView;
import gov.nysenate.openleg.api.updates.view.UpdateTokenView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillTextFormat;
import gov.nysenate.openleg.legislation.bill.BillUpdateField;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.updates.UpdateDigest;
import gov.nysenate.openleg.updates.UpdateToken;
import gov.nysenate.openleg.updates.UpdateType;
import gov.nysenate.openleg.updates.bill.BillUpdatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Bill Updates API
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class BillUpdatesCtrl extends BaseCtrl {
    private final BillUpdatesDao billUpdatesDao;
    private final BillDataService billData;

    @Autowired
    public BillUpdatesCtrl(BillUpdatesDao billUpdatesDao, BillDataService billData) {
        this.billUpdatesDao = billUpdatesDao;
        this.billData = billData;
    }

    /**
     * Updated Bills API
     * -----------------
     *
     * Return a list of bill ids that have changed during a specified date/time range.
     * Usages:
     * (GET) /api/3/bills/updates/             (last 7 days)
     * (GET) /api/3/bills/updates/{from}       (from date to now)
     * (GET) /api/3/bills/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params: detail (boolean) - Show update digests within each token.
     *                 summary (boolean) - Return bill infos instead of just the bill id.
     *                 fullBill(boolean) - Return a full bill response instead of bill id. (overrides summary, unavailable for detailed updates)
     *                 fullTextFormat(string) - specifies which full text formats will be returned if fullBill = true.
     *                 type (string) - Update type (processed, published) Default: processed
     *                 filter (string) - Filter updates by a BillUpdateField value
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update date
     *
     * Expected Output: List of UpdateTokenView<BaseBillId> or UpdateDigestView<BaseBillId> if detail = true.
     */

    @RequestMapping(value = "/updates")
    public BaseResponse getRecentUpdates(WebRequest request) {
        return getUpdatesDuring(LocalDateTime.now().minusDays(7), LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesFrom(@PathVariable String from, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = LocalDateTime.now();
        return getUpdatesDuring(fromDateTime, toDateTime, request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesDuring(@PathVariable String from, @PathVariable String to, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        return getUpdatesDuring(fromDateTime, toDateTime, request);
    }

    /**
     * Bill Update Digests API
     * ------------------------
     *
     * Return update digests for a bill during a given date/time range
     * Usages:
     * (GET) /api/3/bills/{sessionYear}/{printNo}/updates
     * (GET) /api/3/bills/{sessionYear}/{printNo}/updates/{from}
     * (GET) /api/3/bills/{sessionYear}/{printNo}/updates/{from}/{to}
     *
     * Where 'from' and 'to' are ISO date times.
     *
     * Request Params: filter (string) - Filter updates by a BillUpdateField
     *                 type (string) - Update type (processed, published) Default: processed
     *
     * Expected Output: List of UpdateDigestView<BaseBillId>
     */

    @RequestMapping(value = "/{sessionYear:\\d{4}}/{printNo}/updates")
    public BaseResponse getUpdatesForBill(@PathVariable int sessionYear, @PathVariable String printNo, WebRequest request) {
        return getUpdatesForBillDuring(sessionYear, printNo, DateUtils.LONG_AGO,
                LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/{sessionYear:\\d{4}}/{printNo}/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesForBill(@PathVariable int sessionYear, @PathVariable String printNo,
                                          @PathVariable String from,
                                          WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        return getUpdatesForBillDuring(sessionYear, printNo, fromDateTime, LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/{sessionYear:\\d{4}}/{printNo}/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesForBillDuring(@PathVariable int sessionYear, @PathVariable String printNo,
                                                @PathVariable String from, @PathVariable String to, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        return getUpdatesForBillDuring(sessionYear, printNo, fromDateTime, toDateTime, request);
    }

    /** --- Internal --- */

    private BaseResponse getUpdatesDuring(LocalDateTime from, LocalDateTime to, WebRequest request) {
        // Fetch params
        LimitOffset limOff = getLimitOffset(request, 50);
        Range<LocalDateTime> updateRange = getClosedOpenRange(from, to, "from", "to");
        boolean detail = getBooleanParam(request, "detail", false);
        boolean summary = getBooleanParam(request, "summary", false);
        boolean fullBill = getBooleanParam(request, "fullBill", false);
        SortOrder sortOrder = getSortOrder(request, SortOrder.ASC);
        String filter = request.getParameter("filter");
        UpdateType updateType = getUpdateTypeFromParam(request);
        BillUpdateField fieldFilter = getUpdateFieldFromParam(filter);

        if (!detail) {
            PaginatedList<UpdateToken<BaseBillId>> updateTokens =
                billUpdatesDao.getUpdates(updateRange, updateType, fieldFilter, sortOrder, limOff);
            List<UpdateTokenView> updates = updateTokens.results().stream()
                    .map(token -> {
                        if (fullBill) {
                            Set<BillTextFormat> fullTextFormats = getFullTextFormats(request);
                            return new UpdateTokenModelView(token, new BaseBillIdView(token.getId()),
                                    new BillView(billData.getBill(token.getId()), fullTextFormats));
                        }
                        if (summary) {
                            return new UpdateTokenModelView(token, new BaseBillIdView(token.getId()),
                                    new SimpleBillInfoView(billData.getBillInfo(token.getId())));
                        }
                        return new UpdateTokenView(token, new BaseBillIdView(token.getId()));
                    })
                    .toList();
            return DateRangeListViewResponse.of(updates, updateRange, updateTokens.total(), limOff);
        }
        else {
            PaginatedList<UpdateDigest<BaseBillId>> updateDigests =
                billUpdatesDao.getDetailedUpdates(updateRange, updateType, fieldFilter, sortOrder, limOff);
            return DateRangeListViewResponse.of(updateDigests.results().stream()
                .map(digest ->
                        (!summary) ? new UpdateDigestView(digest, new BaseBillIdView(digest.getId()))
                                   : new UpdateDigestModelView(digest, new BaseBillIdView(digest.getId()),
                                                                       new SimpleBillInfoView(billData.getBillInfo(digest.getId())))
                )
                .toList(), updateRange, updateDigests.total(), limOff);
        }
    }

    private BaseResponse getUpdatesForBillDuring(int sessionYear, String printNo, LocalDateTime from, LocalDateTime to,
                                                 WebRequest request) {
        BillUpdateField filterField = getUpdateFieldFromParam(request.getParameter("filter"));
        SortOrder sortOrder = getSortOrder(request, SortOrder.ASC);
        LimitOffset limOff = getLimitOffset(request, 50);
        Range<LocalDateTime> updateRange = getClosedOpenRange(from, to, "from", "to");
        UpdateType updateType = getUpdateTypeFromParam(request);
        PaginatedList<UpdateDigest<BaseBillId>> digests = billUpdatesDao.getDetailedUpdatesForBill(
            getBaseBillId(printNo, sessionYear, "printNo"), updateRange, updateType, filterField, sortOrder, limOff);
        return DateRangeListViewResponse.of(digests.results().stream()
            .map(digest -> new UpdateDigestView(digest, new BaseBillIdView(digest.getId())))
            .toList(), updateRange, digests.total(), limOff);
    }

    private BillUpdateField getUpdateFieldFromParam(String filter) {
        BillUpdateField fieldFilter = null;
        if (filter != null && !filter.isEmpty()) {
            try {
                fieldFilter = BillUpdateField.valueOf(filter.toUpperCase());
            }
            catch (IllegalArgumentException ex) {
                String validFields = Stream.of(BillUpdateField.values())
                    .map(b -> b.name().toLowerCase())
                    .collect(Collectors.joining(", "));
                throw new InvalidRequestParamEx(filter, "filter", "string",
                    "Filter must be one of the following fields: " + validFields);
            }
        }
        return fieldFilter;
    }
}