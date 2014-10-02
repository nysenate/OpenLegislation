package gov.nysenate.openleg.controller.api.calendar;

import com.google.common.collect.*;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleErrorResponse;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @RequestMapping(value = "/search")
    public BaseResponse searchAllCalendars(@RequestParam MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = getSearchParameters(parameters);
        searchParams.setCalendarType(CalendarType.ALL);
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, LimitOffset.HUNDRED);
        try {
            return ListViewResponse.of(
                    calendarSearchService.searchForCalendars(searchParams, sortOrder, limitOffset).stream()
                            .map(calId -> new SimpleCalendarView(calendarDataService.getCalendar(calId)))
                            .collect(Collectors.toList()),
                    calendarSearchService.getCalenderSearchResultCount(searchParams),
                    limitOffset
            );
        }
        catch (InvalidParametersSearchException ex) {
            return new SimpleErrorResponse("Conflicting search parameters :\n" + searchParams);
        }
        catch (NoResultsSearchException ex) {
            return new SimpleErrorResponse("Received no results for search query");
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "calendar search");
        }
    }

    @RequestMapping(value = "/activelists/search")
    public BaseResponse searchActiveListCalendars(@RequestParam MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = getSearchParameters(parameters);
        searchParams.setCalendarType(CalendarType.ACTIVE_LIST);
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, LimitOffset.HUNDRED);
        try {
            return ListViewResponse.of(
                    calendarSearchService.searchForActiveLists(searchParams, sortOrder, limitOffset).stream()
                            .map(alId -> new SimpleActiveListView(calendarDataService.getActiveList(alId)))
                            .collect(Collectors.toList()),
                    calendarSearchService.getCalenderSearchResultCount(searchParams),
                    limitOffset
            );
        }
        catch (InvalidParametersSearchException ex) {
            return new SimpleErrorResponse("Conflicting search parameters :\n" + searchParams);
        }
        catch (NoResultsSearchException ex) {
            return new SimpleErrorResponse("Received no results for search query");
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "calendar active list search");
        }
    }

    @RequestMapping(value = "/floor/search")
    public BaseResponse searchFloorCalendars(@RequestParam MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = getSearchParameters(parameters);
        searchParams.setCalendarType(CalendarType.FLOOR);
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, LimitOffset.HUNDRED);
        try {
            return ListViewResponse.of(
                    calendarSearchService.searchForFloorCalendars(searchParams, sortOrder, limitOffset).stream()
                            .map(supId -> new SimpleCalendarSupView(calendarDataService.getFloorCalendar(supId)))
                            .collect(Collectors.toList()),
                    calendarSearchService.getCalenderSearchResultCount(searchParams),
                    limitOffset
            );
        }
        catch (InvalidParametersSearchException ex) {
            return new SimpleErrorResponse("Conflicting search parameters :\n" + searchParams);
        }
        catch (NoResultsSearchException ex) {
            return new SimpleErrorResponse("Received no results for search query");
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "floor calendar search");
        }
    }

    private CalendarSearchParameters getSearchParameters(MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = new CalendarSearchParameters();
        if (parameters.containsKey("year")) {
            searchParams.setYear(Integer.parseInt(parameters.getFirst("year")));
        }
        if (parameters.containsKey("startDate") && parameters.containsKey("endDate")) {
            searchParams.setDateRange(Range.closed(
                    LocalDate.parse(parameters.getFirst("startDate"), DateTimeFormatter.BASIC_ISO_DATE),
                    LocalDate.parse(parameters.getFirst("endDate"), DateTimeFormatter.BASIC_ISO_DATE)
            ));
        }
        if (parameters.containsKey("sessionYear")) {
            int sessionYear = Integer.parseInt(parameters.getFirst("sessionYear"));
            SetMultimap<Integer, BillId> printNoMap = HashMultimap.create();
            for (int n=1; parameters.containsKey("printNoSet" + n); n++) {
                Integer setNum = n;
                parameters.get("printNoSet" + setNum).stream()
                        .map(printNo -> new BillId(printNo, sessionYear))
                        .forEach(billId -> printNoMap.put(setNum, billId));
            }
            searchParams.setBillPrintNo(printNoMap);
        }
        if (parameters.containsKey("calNoSet1")) {
            SetMultimap<Integer, Integer> calNoMap = HashMultimap.create();
            for (int n=1; parameters.containsKey("calNoSet" + n); n++) {
                Integer setNum = n;
                parameters.get("calNoSet" + setNum).stream()
                        .map(Integer::parseInt)
                        .forEach(calNo -> calNoMap.put(setNum, calNo));
            }
            searchParams.setBillCalendarNo(calNoMap);
        }
        if (parameters.containsKey("sectionCodeSet1")) {
            SetMultimap<Integer, Integer> sectionCodeMap = HashMultimap.create();
            for (int n=1; parameters.containsKey("sectionCodeSet" + n); n++) {
                Integer setNum = n;
                parameters.get("sectionCodeSet" + setNum).stream()
                        .map(Integer::parseInt)
                        .forEach(sectionCode -> sectionCodeMap.put(setNum, sectionCode));
            }
            searchParams.setSectionCode(sectionCodeMap);
        }
        return searchParams;
    }
}
