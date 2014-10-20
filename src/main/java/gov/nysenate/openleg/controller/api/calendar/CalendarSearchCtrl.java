package gov.nysenate.openleg.controller.api.calendar;

import com.google.common.collect.*;
import gov.nysenate.openleg.client.response.base.*;
import gov.nysenate.openleg.client.response.error.*;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.calendar.SimpleActiveListView;
import gov.nysenate.openleg.client.view.calendar.SimpleCalendarSupView;
import gov.nysenate.openleg.client.view.calendar.SimpleCalendarView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.service.base.InvalidParametersSearchException;
import gov.nysenate.openleg.service.base.NoResultsSearchException;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public BaseResponse searchAllCalendars(WebRequest webRequest) {
        CalendarSearchParameters searchParams = getSearchParameters(webRequest);
        searchParams.setCalendarType(CalendarType.ALL);
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        return ListViewResponse.of(
                calendarSearchService.searchForCalendars(searchParams, sortOrder, limitOffset).stream()
                        .map(calId -> new SimpleCalendarView(calendarDataService.getCalendar(calId)))
                        .collect(Collectors.toList()),
                calendarSearchService.getCalenderSearchResultCount(searchParams),
                limitOffset
        );
    }

    /**
     * Performs a search on all active list calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/activelists/search")
    public BaseResponse searchActiveListCalendars(WebRequest webRequest) {
        CalendarSearchParameters searchParams = getSearchParameters(webRequest);
        searchParams.setCalendarType(CalendarType.ACTIVE_LIST);
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        return ListViewResponse.of(
                calendarSearchService.searchForActiveLists(searchParams, sortOrder, limitOffset).stream()
                        .map(alId -> new SimpleActiveListView(calendarDataService.getActiveList(alId)))
                        .collect(Collectors.toList()),
                calendarSearchService.getCalenderSearchResultCount(searchParams),
                limitOffset
        );
    }

    /**
     * Performs a search on all floor calendars based on parameters in the given web request
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/floor/search")
    public BaseResponse searchFloorCalendars(WebRequest webRequest) {
        CalendarSearchParameters searchParams = getSearchParameters(webRequest);
        searchParams.setCalendarType(CalendarType.FLOOR);
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.HUNDRED);
        return ListViewResponse.of(
                calendarSearchService.searchForFloorCalendars(searchParams, sortOrder, limitOffset).stream()
                        .map(supId -> new SimpleCalendarSupView(calendarDataService.getFloorCalendar(supId)))
                        .collect(Collectors.toList()),
                calendarSearchService.getCalenderSearchResultCount(searchParams),
                limitOffset
        );
    }

    /** --- Exception Handlers --- */

    /**
     * Handles an invalid search parameters exception by returning an error response
     * @param ex
     * @return
     */
    @ExceptionHandler(InvalidParametersSearchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidParamsSearchEx(InvalidParametersSearchException ex) {
        return new ViewObjectErrorResponse(ErrorCode.INVALID_CAL_SEARCH_PARAMS,
                                           ListView.ofStringList(new LinkedList<>(ex.getInvalidParams())));
    }

    /** --- Internal Methods --- */

    /**
     * Extracts calendar search parameters from the web request params
     * @param webRequest
     * @return
     */
    private CalendarSearchParameters getSearchParameters(WebRequest webRequest) {
        CalendarSearchParameters searchParams = new CalendarSearchParameters();
        searchParams.setDateRange(getDateRange(webRequest, null));
        if (webRequest.getParameter("year") != null) {
            searchParams.setYear(Integer.parseInt(webRequest.getParameter("year")));
        }
        if (webRequest.getParameter("sessionYear") != null && webRequest.getParameter("printNoSet1") != null) {
            int sessionYear = Integer.parseInt(webRequest.getParameter("sessionYear"));
            SetMultimap<Integer, BillId> printNoMap = HashMultimap.create();
            for (int setNum=1; webRequest.getParameter("printNoSet" + setNum) != null; setNum++) {
                for (String printNo : webRequest.getParameterValues("printNoSet" + setNum)) {
                    printNoMap.put(setNum, new BillId(printNo, sessionYear));
                }
            }
            searchParams.setBillPrintNo(printNoMap);
        }
        if (webRequest.getParameter("calNoSet1") != null) {
            SetMultimap<Integer, Integer> calNoMap = HashMultimap.create();
            for (int setNum=1; webRequest.getParameter("calNoSet" + setNum) != null; setNum++) {
                for (String calNo : webRequest.getParameterValues("calNoSet" + setNum)) {
                    calNoMap.put(setNum, Integer.parseInt(calNo));
                }
            }
            searchParams.setBillCalendarNo(calNoMap);
        }
        if (webRequest.getParameter("sectionCodeSet1") != null) {
            SetMultimap<Integer, Integer> sectionCodeMap = HashMultimap.create();
            for (int setNum=1; webRequest.getParameter("sectionCodeSet" + setNum) != null; setNum++) {
                for (String sectionCode : webRequest.getParameterValues("sectionCodeSet" + setNum)) {
                    sectionCodeMap.put(setNum, Integer.parseInt(sectionCode));
                }
            }
            searchParams.setSectionCode(sectionCodeMap);
        }
        return searchParams;
    }
}
