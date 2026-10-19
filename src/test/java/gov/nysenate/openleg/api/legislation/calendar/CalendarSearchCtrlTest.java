package gov.nysenate.openleg.api.legislation.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.CollectionFullTestSupport;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplemental;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.calendar.CalendarSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class CalendarSearchCtrlTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[] endpoints() {
        return new Object[]{"/api/3/calendars/search", "/api/3/calendars/2025/search"};
    }

    @Parameterized.Parameter
    public String endpoint;

    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        CalendarSearchService calendarSearch = mock(CalendarSearchService.class);
        CalendarDataService calendarData = mock(CalendarDataService.class);
        CalendarId calendarId = new CalendarId(1, 2025);
        LocalDate calDate = LocalDate.of(2025, 1, 8);
        Calendar calendar = new Calendar(calendarId);
        calendar.putActiveList(new CalendarActiveList(calendarId, 0, "notes", calDate, calDate.atTime(10, 0)));
        calendar.putSupplemental(new CalendarSupplemental(calendarId, Version.ORIGINAL, calDate, calDate.atTime(10, 0)));
        SearchResults<CalendarId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(calendarId, BigDecimal.ONE, Map.of())), new LimitOffset(100, 1));
        when(calendarSearch.searchForCalendars(anyString(), anyString(), any(LimitOffset.class))).thenReturn(results);
        when(calendarSearch.searchForCalendarsByYear(anyInt(), anyString(), anyString(), any(LimitOffset.class)))
                .thenReturn(results);
        when(calendarData.getCalendar(calendarId)).thenReturn(calendar);
        CalendarViewFactory viewFactory = new CalendarViewFactory(mock(BillDataService.class));
        offMvc = mockMvc(policy -> new CalendarSearchCtrl(calendarSearch, calendarData, viewFactory, policy), "off");
        enforceMvc = mockMvc(policy -> new CalendarSearchCtrl(calendarSearch, calendarData, viewFactory, policy),
                "enforce");
    }

    @Test
    public void offReturnsSummariesOrFullCalendars() throws Exception {
        assertFalse(isFull(search()));
        assertTrue(isFull(search("detail", "true")));
    }

    @Test
    public void fullIsIgnored() throws Exception {
        assertEquals(search(), assertSameInBothModes("full", "true"));
        assertEquals(search("detail", "true"), search("detail", "true", "full", "false"));
    }

    @Test
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        assertSameInBothModes();
        assertSameInBothModes("detail", "false");
    }

    /** Full calendars carry the entries of their floor calendar; summaries only count them. */
    private static boolean isFull(JsonNode response) {
        return response.at("/result/items/0/result/floorCalendar").has("entriesBySection");
    }

    private JsonNode assertSameInBothModes(String... params) throws Exception {
        return CollectionFullTestSupport.assertSameInBothModes(offMvc, enforceMvc, endpoint, withTerm(params));
    }

    private JsonNode search(String... params) throws Exception {
        return ok(offMvc, endpoint, withTerm(params));
    }

    private static String[] withTerm(String... params) {
        return concat(new String[]{"term", "*"}, params);
    }
}
