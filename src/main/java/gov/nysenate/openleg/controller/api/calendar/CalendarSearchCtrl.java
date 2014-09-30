package gov.nysenate.openleg.controller.api.calendar;

import com.google.common.collect.*;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleErrorResponse;
import gov.nysenate.openleg.client.response.base.ViewListResponse;
import gov.nysenate.openleg.client.view.calendar.CalendarActiveListIdView;
import gov.nysenate.openleg.client.view.calendar.CalendarIdView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupIdView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.service.base.InvalidParametersSearchException;
import gov.nysenate.openleg.service.base.NoResultsSearchException;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchService;
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
public class CalendarSearchCtrl {

    @Autowired
    private CalendarSearchService calendarSearchService;

    @RequestMapping(value = "/search")
    public BaseResponse searchAllCalendars(@RequestParam MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = getSearchParameters(parameters);
        searchParams.setCalendarType(CalendarType.ALL);
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, new LimitOffset(100,1));
        try {
            return new ViewListResponse<>(
                    calendarSearchService.searchForCalendars(searchParams, sortOrder, limitOffset).stream()
                            .map(CalendarIdView::new)
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
    }

    @RequestMapping(value = "/activelists/search")
    public BaseResponse searchActiveListCalendars(@RequestParam MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = getSearchParameters(parameters);
        searchParams.setCalendarType(CalendarType.ACTIVE_LIST);
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, new LimitOffset(100,1));
        try {
            return new ViewListResponse<>(
                    calendarSearchService.searchForActiveLists(searchParams, sortOrder, limitOffset).stream()
                            .map(CalendarActiveListIdView::new)
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
    }

    @RequestMapping(value = "/floor/search")
    public BaseResponse searchFloorCalendars(@RequestParam MultiValueMap<String, String> parameters) {
        CalendarSearchParameters searchParams = getSearchParameters(parameters);
        searchParams.setCalendarType(CalendarType.FLOOR);
        SortOrder sortOrder = getSortOrder(parameters, SortOrder.ASC);
        LimitOffset limitOffset = getLimitOffset(parameters, new LimitOffset(100,1));
        try {
            return new ViewListResponse<>(
                    calendarSearchService.searchForFloorCalendars(searchParams, sortOrder, limitOffset).stream()
                            .map(CalendarSupIdView::new)
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
    }

    public CalendarSearchParameters getSearchParameters(MultiValueMap<String, String> parameters) {
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

    public SortOrder getSortOrder(MultiValueMap<String, String> parameters, SortOrder defaultSortOrder) {
        try {
            return SortOrder.valueOf(parameters.getFirst("order"));
        }
        catch (Exception ex) {
            return defaultSortOrder;
        }
    }

    public LimitOffset getLimitOffset(MultiValueMap<String, String> parameters, LimitOffset defaultLimitOffset) {
        try {
            return new LimitOffset(Integer.parseInt(parameters.getFirst("limit")),
                                   Integer.parseInt(parameters.getFirst("offset")));
        }
        catch (Exception ex) {
            return defaultLimitOffset;
        }
    }
}
