package gov.nysenate.openleg.api.updates.aggregate;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BaseBillIdView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarIdView;
import gov.nysenate.openleg.api.legislation.law.view.LawDocIdView;
import gov.nysenate.openleg.api.legislation.law.view.LawVersionIdView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.DateRangeListViewResponse;
import gov.nysenate.openleg.api.updates.view.UpdateDigestView;
import gov.nysenate.openleg.api.updates.view.UpdateTokenView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawVersionId;
import gov.nysenate.openleg.updates.UpdateContentType;
import gov.nysenate.openleg.updates.UpdateDigest;
import gov.nysenate.openleg.updates.UpdateToken;
import gov.nysenate.openleg.updates.UpdateType;
import gov.nysenate.openleg.updates.aggregate.AggregateUpdatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/updates", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class AggregateUpdatesCtrl extends BaseCtrl {
    private final AggregateUpdatesDao updatesDao;

    @Autowired
    public AggregateUpdatesCtrl(AggregateUpdatesDao updatesDao) {
        this.updatesDao = updatesDao;
    }

    /**
     * Aggregate Updates API
     * ---------------------
     *
     * Usages:
     * (GET) /api/3/updates/{from date-time}
     * (GET) /api/3/updates/{from date-time}/{to date-time}
     *
     * Request Params: detail (boolean) - Show update digests within each token. Default: false
     *                 fields (boolean) - Show updated fields for each update digest (if detail is true) Default: false
     *                 content-type (string[]) - Only get updates for the specified types
     *                                           Default: all types (AGENDA, BILL, CALENDAR, LAW)
     *                 type (string) - Update type (processed, published) Default: processed
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update
     *
     * Expected Response: List of UpdateTokenView or UpdateDigestTokenView if detail = true
     */
    @RequestMapping(value = "/{from:.*\\.?.*}")
    public BaseResponse getAggregateUpdates(@PathVariable String from, WebRequest webRequest) {
        return getAggregateUpdatesResponse(parseISODateTime(from, "from"), LocalDateTime.now(), webRequest);
    }

    @RequestMapping(value = "/{from}/{to:.*\\.?.*}")
    public BaseResponse getAggregateUpdates(@PathVariable String from, @PathVariable String to, WebRequest webRequest) {
        return getAggregateUpdatesResponse(parseISODateTime(from, "from"), parseISODateTime(to, "to"), webRequest);
    }

    /**
     * Parses request parameters and fetches a response for the getAggregateUpdates functions
     * @see #getAggregateUpdates
     */
    private BaseResponse getAggregateUpdatesResponse(LocalDateTime from, LocalDateTime to, WebRequest webRequest) {
        Range<LocalDateTime> dateTimeRange = getOpenRange(from, to, "from", "to");
        UpdateType updateType = getUpdateTypeFromParam(webRequest);
        boolean detail = getBooleanParam(webRequest, "detail", false);
        boolean fields = getBooleanParam(webRequest, "fields", false);
        Set<UpdateContentType> contentTypes = getContentTypes(webRequest);
        LimitOffset limitOffset = getLimitOffset(webRequest, 50);
        SortOrder order = getSortOrder(webRequest, SortOrder.DESC);

        return detail
                ? getDigestResponse(dateTimeRange, updateType, contentTypes, fields, limitOffset, order)
                : getTokenResponse(dateTimeRange, updateType, contentTypes, limitOffset, order);
    }

    private BaseResponse getTokenResponse(Range<LocalDateTime> dateTimeRange, UpdateType updateType,
                                          Set<UpdateContentType> contentTypes,
                                          LimitOffset limitOffset, SortOrder order) {
        PaginatedList<UpdateToken<Map<String, String>>> result =
                updatesDao.getUpdateTokens(dateTimeRange, contentTypes, updateType, order, limitOffset);
        return DateRangeListViewResponse.of(
                result.results().stream()
                        .map(this::getTokenView)
                        .toList(),
                dateTimeRange, result.total(), limitOffset);
    }

    private BaseResponse getDigestResponse(Range<LocalDateTime> dateTimeRange, UpdateType updateType,
                                           Set<UpdateContentType> contentTypes, boolean fields,
                                           LimitOffset limitOffset, SortOrder order) {
        PaginatedList<UpdateDigest<Map<String, String>>> result =
                updatesDao.getUpdateDigests(dateTimeRange, contentTypes, updateType, order, limitOffset, fields);
        return DateRangeListViewResponse.of(
                result.results().stream()
                        .map(this::getDigestView)
                        .toList(),
                dateTimeRange, result.total(), limitOffset);
    }

    private Set<UpdateContentType> getContentTypes(WebRequest webRequest) {
        String[] contentTypeStrings = webRequest.getParameterValues("content-type");
        Set<UpdateContentType> types = new HashSet<>();
        if (contentTypeStrings != null) {
            for (String cTypeString : contentTypeStrings) {
                try {
                    types.add(UpdateContentType.getValue(cTypeString));
                } catch (IllegalArgumentException | NullPointerException ignored) {}
            }
        }
        if (types.isEmpty()) {
            return UpdateContentType.getAllTypes();
        }
        return types;
    }

    private UpdateTokenView getTokenView(UpdateToken<Map<String, String>> token) {
        return new UpdateTokenView(token, getIdView(token));
    }

    private UpdateDigestView getDigestView(UpdateDigest<Map<String, String>> digest) {
        return new UpdateDigestView(digest, getIdView(digest));
    }

    /**
     * Determines the content type of the given genericized update token and returns a ViewObject id for that content
     * @param token UpdateToken<Map<String, String>>
     * @return ViewObject
     */
    private ViewObject getIdView(UpdateToken<Map<String, String>> token) {
        Map<String, String> id = token.getId();
        switch (token.getContentType()) {
            case AGENDA:
                return new AgendaIdView(new AgendaId(
                        Integer.parseInt(id.get("agendaNumber")),
                        Integer.parseInt(id.get("year"))
                ));
            case BILL:
                return new BaseBillIdView(new BaseBillId(id.get("printNo"), Integer.parseInt(id.get("session"))));
            case CALENDAR:
                return new CalendarIdView(new CalendarId(
                        Integer.parseInt(id.get("calNo")),
                        Integer.parseInt(id.get("year"))
                ));
            case LAW:
                if (id.containsKey("lawDocId")) {
                    return new LawDocIdView(new LawDocId(id.get("lawDocId"), LocalDate.parse(id.get("publishedDate"))));
                }
                return new LawVersionIdView(new LawVersionId(id.get("lawId"), LocalDate.parse(id.get("publishedDate"))));
        }
        return () -> "null";
    }
}
