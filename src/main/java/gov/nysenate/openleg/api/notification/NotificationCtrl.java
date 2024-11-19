package gov.nysenate.openleg.api.notification;

import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.notification.view.NotificationSummaryView;
import gov.nysenate.openleg.api.notification.view.NotificationView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.DateRangeListViewResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.notifications.NotificationNotFoundException;
import gov.nysenate.openleg.search.notifications.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/notifications", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class NotificationCtrl extends BaseCtrl {
    private final NotificationService notificationService;

    @Autowired
    public NotificationCtrl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Single Notification Retrieval API
     * ---------------------------------
     *
     * Retrieve a single notification by id (GET) /api/3/admin/notifications/{id}
     *
     * <p>Request Parameters: None</p>
     *
     * Expected Output: NotificationView
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/{id:\\d+}")
    public BaseResponse getNotification(@PathVariable int id) {
        return new ViewObjectResponse<>(new NotificationView(notificationService.getNotification(id)));
    }

    /**
     * Notification Listing API
     * ------------------------
     *
     * Return notifications from the past week (GET) /api/3/admin/notifications
     * Request Params: type (string) - NotificationType. Default: ALL
     *                 full (boolean) - If true, NotificationView is returned, otherwise NotificationSummaryView.
     *                 limit, offset (int) - Paginate.
     *                 order (string) - Order by update date.
     *
     * Expected Output: List of NotificationSummaryView or NotificationView
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "")
    public BaseResponse getNotifications(WebRequest request) throws SearchException {
        LocalDateTime fromDate = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime toDate = LocalDateTime.now();
        return getNotificationsDuring(fromDate, toDate, request);
    }

    /**
     * Notification Listing API
     * ------------------------
     *
     * Return notifications from a given date to now (GET) /api/3/admin/notifications/{from}
     * @see #getNotifications(WebRequest)
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/{from:\\d{4}-.*}")
    public BaseResponse getNotifications(@PathVariable String from, WebRequest request) throws SearchException {
        LocalDateTime fromDate = parseISODateTime(from, "from");
        LocalDateTime toDate = LocalDateTime.now();
        return getNotificationsDuring(fromDate, toDate, request);
    }

    /**
     * Notification Listing API
     * ------------------------
     *
     * Return notifications for a given date/time range (GET) /api/3/admin/notifications/{from}/{to}
     * @see #getNotifications(WebRequest)
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/{from}/{to:.*\\.?.*}")
    public BaseResponse getNotifications(@PathVariable String from,
                                         @PathVariable String to,
                                         WebRequest request) throws SearchException {
        LocalDateTime fromDate = parseISODateTime(from, "from");
        LocalDateTime toDate = parseISODateTime(to, "to");
        return getNotificationsDuring(fromDate, toDate, request);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public BaseResponse handleNotificationNotFoundException(NotificationNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.NOTIFICATION_NOT_FOUND, Ints.checkedCast(ex.getId()));
    }

    /**
     * Notification Search API
     * -----------------------
     *
     * Search across all notifications:  (GET) /api/3/admin/notifications/search
     * Request Parameters:  term - The lucene query string
     *                      sort - The lucene sort string (blank by default)
     *                      full - Set to true to retrieve full notification responses (false by default)
     *                      limit - Limit the number of results (default 25)
     *                      offset - Start results from offset
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/search")
    public BaseResponse searchForNotifications(@RequestParam String term,
                                               @RequestParam(defaultValue = "") String sort,
                                               WebRequest request) throws SearchException {
        LimitOffset limitOffset = getLimitOffset(request, 25);
        boolean full = getBooleanParam(request, "full", false);
        if (StringUtils.isBlank(term)) {
            term = "*";
        }
        SearchResults<NotificationView> results = notificationService.notificationSearch(term, sort, limitOffset);
        return ListViewResponse.of(results.resultList().stream()
                .map(r -> new SearchResultView(
                        full ? r.result() : getSummary(r.result()),
                        r.rank()))
                .toList(), results.totalResults(), limitOffset);
    }


    /* --- Internal --- */

    private BaseResponse getNotificationsDuring(LocalDateTime from, LocalDateTime to, WebRequest request) throws SearchException {
        LimitOffset limOff = getLimitOffset(request, 25);
        SortOrder order = getSortOrder(request, SortOrder.DESC);
        boolean full = getBooleanParam(request, "full", false);
        PaginatedList<NotificationView> results =
                notificationService.getNotificationList(getNotificationTypes(request), from, to, order, limOff);
        Range<LocalDateTime> dateRange = getClosedOpenRange(from, to, "from", "to");
        return DateRangeListViewResponse.of(results.results().stream().map(r -> full ? r : getSummary(r)).toList(),
                dateRange, results.total(), limOff);
    }

    private static Set<NotificationType> getNotificationTypes(WebRequest request) {
        String[] types = request.getParameterValues("type");
        return types == null ? EnumSet.allOf(NotificationType.class) : getTypesFromStrings(types);
    }

    private static Set<NotificationType> getTypesFromStrings(String[] types) {
        Set<NotificationType> typeSet = new HashSet<>();
        for (String type : types) {
            typeSet.add(getEnumParameter("type", type, NotificationType.class));
        }
        return typeSet;
    }

    private static NotificationSummaryView getSummary(NotificationView view) {
        return view;
    }
}
