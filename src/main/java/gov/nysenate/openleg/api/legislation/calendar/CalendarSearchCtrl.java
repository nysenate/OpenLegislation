package gov.nysenate.openleg.api.legislation.calendar;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.api.legislation.calendar.view.SimpleCalendarView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.calendar.CalendarSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarSearchCtrl extends BaseCtrl {
    private final CalendarSearchService calendarSearchService;
    private final CalendarDataService calendarDataService;
    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public CalendarSearchCtrl(CalendarSearchService calendarSearchService,
                              CalendarDataService calendarDataService,
                              CalendarViewFactory calendarViewFactory) {
        this.calendarSearchService = calendarSearchService;
        this.calendarDataService = calendarDataService;
        this.calendarViewFactory = calendarViewFactory;
    }

    /** --- Request Handlers --- */

    /**
     * Calendar Search API
     *
     * Performs a search across all calendars: (GET) /api/3/calendars/search
     * Request Parameters:      term - The lucene query string
     *                          sort - The lucene sort string (blank by default)
     *                          detail - If true, full calendars will be returned including
     *                                  active list and supplemental entries (default false)
     *                          limit - Limit the number of results (default 100)
     *                          offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/search")
    public BaseResponse searchCalendars(@RequestParam String term,
                                        @RequestParam(defaultValue = "") String sort,
                                        @RequestParam(defaultValue = "false") boolean detail,
                                        WebRequest webRequest) throws SearchException, InvalidRequestParamEx {
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return getCalendarSearchResponse(term, sort, limitOffset, null, detail);
    }

    /**
     * Calendar Search API
     *
     * Performs a search across all calendars: (GET) /api/3/calendars/{year}/search
     * Request Parameters:      term - The lucene query string
     *                          sort - The lucene sort string (blank by default)
     *                          full - If true, full calendars will be returned including
     *                                  active list and supplemental entries (default false)
     *                          limit - Limit the number of results (default 100)
     *                          offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/{year:\\d{4}}/search")
    public BaseResponse searchCalendarsOfYear(@PathVariable Integer year,
                                              @RequestParam String term,
                                              @RequestParam(defaultValue = "") String sort,
                                              @RequestParam(defaultValue = "false") boolean detail,
                                      WebRequest webRequest) throws SearchException, InvalidRequestParamEx {
        LimitOffset limitOffset = getLimitOffset(webRequest, 100);
        return getCalendarSearchResponse(term, sort, limitOffset, year, detail);
    }

    /**
     * --- Internal Methods ---
     */

    /**
     * Performs a calendar search based on the input parameters and returns a search response
     */
    private BaseResponse getCalendarSearchResponse(String term, String sort, LimitOffset limitOffset, Integer year, boolean detail)
            throws SearchException, InvalidRequestParamEx {
        SearchResults<CalendarId> calResults = (year == null)
            ? calendarSearchService.searchForCalendars(term, sort, limitOffset)
            : calendarSearchService.searchForCalendarsByYear(year, term, sort, limitOffset);
        return getCalendarSearchResultResponse(calResults, detail);
    }

    /**
     * Generates a calendar list response from calendar search results
     * @param results
     * @return
     */
    private BaseResponse getCalendarSearchResultResponse(SearchResults<CalendarId> results, boolean detail) {
        return ListViewResponse.of(
                results.resultList().stream()
                        .map(result -> new SearchResultView(detail ? calendarViewFactory.getCalendarView(calendarDataService.getCalendar(result.result()))
                                : new SimpleCalendarView(calendarDataService.getCalendar(result.result())),
                                result.rank())).toList(),
                results.totalResults(), results.limitOffset() );
    }
}
