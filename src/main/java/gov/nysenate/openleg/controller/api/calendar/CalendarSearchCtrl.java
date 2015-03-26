package gov.nysenate.openleg.controller.api.calendar;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarSearchCtrl extends BaseCtrl{

    private static final Logger logger = LoggerFactory.getLogger(CalendarSearchCtrl.class);

    @Autowired
    private CalendarSearchService calendarSearchService;

    @Autowired
    private CalendarDataService calendarDataService;

    @Autowired
    private CalendarViewFactory calendarViewFactory;

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
    public BaseResponse searchCalendars(@RequestParam(required = true) String term,
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
                                              @RequestParam(required = true) String term,
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
     *
     * @param term
     * @param sort
     * @param limitOffset
     * @param year
     * @return
     * @throws SearchException
     * @throws gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx
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
                results.getResults().stream()
                        .map(result -> new SearchResultView((detail)
                                ? calendarViewFactory.getCalendarView(
                                calendarDataService.getCalendar(result.getResult()))
                                : new SimpleCalendarView(calendarDataService.getCalendar(result.getResult())),
                                result.getRank()))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }
}
