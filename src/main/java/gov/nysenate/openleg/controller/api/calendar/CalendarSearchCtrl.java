package gov.nysenate.openleg.controller.api.calendar;

import com.google.common.collect.*;
import gov.nysenate.openleg.client.response.base.*;
import gov.nysenate.openleg.client.response.error.*;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.service.base.InvalidParametersSearchException;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedList;
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

    /** --- Request Handlers --- */

    /**
     * Performs a search on all types of calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/search")
    public BaseResponse searchAllCalendars(@RequestParam(required = true) String term,
                                           @RequestParam(defaultValue = "") String sort,
                                           @RequestParam(defaultValue = "false") boolean full,
                                           WebRequest webRequest) throws SearchException {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        SearchResults<CalendarId> results = calendarSearchService.searchForCalendars(term, sort, limitOffset);
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> (full) ? new CalendarView(calendarDataService.getCalendar(result.getResult()))
                                              : new SimpleCalendarView(calendarDataService.getCalendar(result.getResult())))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }

    /**
     * Performs a search on all active list calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/activelists/search")
    public BaseResponse searchActiveListCalendars(@RequestParam(required = true) String term,
                                                  @RequestParam(defaultValue = "") String sort,
                                                  @RequestParam(defaultValue = "false") boolean full,
                                                  WebRequest webRequest) throws SearchException {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        SearchResults<CalendarActiveListId> results = calendarSearchService.searchForActiveLists(term, sort, limitOffset);
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> (full) ? new ActiveListView(calendarDataService.getActiveList(result.getResult()))
                                              : new SimpleActiveListView(calendarDataService.getActiveList(result.getResult())))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }

    /**
     * Performs a search on all floor calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/floor/search")
    public BaseResponse searchFloorCalendars(@RequestParam(required = true) String term,
                                             @RequestParam(defaultValue = "") String sort,
                                             @RequestParam(defaultValue = "false") boolean full,
                                             WebRequest webRequest) throws SearchException {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        SearchResults<CalendarSupplementalId> results = calendarSearchService.searchForFloorCalendars(term, sort, limitOffset);
        return ListViewResponse.of(
                results.getResults().stream()
                        .map(result -> (full) ? new CalendarSupView(calendarDataService.getFloorCalendar(result.getResult()))
                                              : new SimpleCalendarSupView(calendarDataService.getFloorCalendar(result.getResult())))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset() );
    }

    /** --- Exception Handlers --- */
}
