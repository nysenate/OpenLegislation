package gov.nysenate.openleg.api.legislation.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CalendarGetCtrlTest {
    private record Listing(String path, String fullType, String simpleType) {}

    private static final List<Listing> LISTINGS = List.of(
            new Listing("/api/3/calendars/2025", "calendar list", "calendar-simple list"),
            new Listing("/api/3/calendars/2025/activelist",
                    "calendar-activelist list", "calendar-activelist-simple list"),
            new Listing("/api/3/calendars/2025/supplemental",
                    "calendar-supplemental list", "calendar-supplemental-simple list"));
    private static final List<String> SINGLE_CALENDARS = List.of("/api/3/calendars/2025/1",
            "/api/3/calendars/2025/1/0", "/api/3/calendars/2025/1/floor", "/api/3/calendars/2025/1/A");

    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() {
        CalendarDataService calendarData = mock(CalendarDataService.class);
        CalendarId calendarId = new CalendarId(1, 2025);
        LocalDate calDate = LocalDate.of(2025, 1, 8);
        LocalDateTime releaseDateTime = calDate.atTime(10, 0);
        CalendarActiveList activeList = new CalendarActiveList(calendarId, 0, "notes", calDate, releaseDateTime);
        CalendarSupplemental supplemental = new CalendarSupplemental(calendarId, Version.A, calDate, releaseDateTime);
        Calendar calendar = new Calendar(calendarId);
        calendar.putActiveList(activeList);
        calendar.putSupplemental(new CalendarSupplemental(calendarId, Version.ORIGINAL, calDate, releaseDateTime));
        calendar.putSupplemental(supplemental);
        when(calendarData.getCalendars(anyInt(), any(SortOrder.class), any(LimitOffset.class)))
                .thenReturn(List.of(calendar));
        when(calendarData.getActiveLists(anyInt(), any(SortOrder.class), any(LimitOffset.class)))
                .thenReturn(List.of(activeList));
        when(calendarData.getCalendarSupplementals(anyInt(), any(SortOrder.class), any(LimitOffset.class)))
                .thenReturn(List.of(supplemental));
        when(calendarData.getCalendarCount(anyInt())).thenReturn(1);
        when(calendarData.getActiveListCount(anyInt())).thenReturn(1);
        when(calendarData.getSupplementalCount(anyInt())).thenReturn(1);
        when(calendarData.getCalendar(any())).thenReturn(calendar);
        when(calendarData.getActiveList(any())).thenReturn(activeList);
        when(calendarData.getCalendarSupplemental(any())).thenAnswer(inv ->
                calendar.getSupplemental(inv.<CalendarSupplementalId>getArgument(0).getVersion()));
        CalendarViewFactory viewFactory = new CalendarViewFactory(mock(BillDataService.class));
        offMvc = mockMvc(policy -> new CalendarGetCtrl(calendarData, viewFactory, policy), "off");
        enforceMvc = mockMvc(policy -> new CalendarGetCtrl(calendarData, viewFactory, policy), "enforce");
    }

    @Test
    public void offListsSummariesOrFullCalendars() throws Exception {
        for (Listing listing : LISTINGS) {
            assertEquals(listing.simpleType(), ok(offMvc, listing.path()).get("responseType").asText());
            assertEquals(listing.fullType(), ok(offMvc, listing.path(), "full", "true").get("responseType").asText());
        }
    }

    @Test
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        for (Listing listing : LISTINGS) {
            assertSameInBothModes(offMvc, enforceMvc, listing.path());
            assertSameInBothModes(offMvc, enforceMvc, listing.path(), "full", "false");
        }
    }

    @Test
    public void enforceKeepsSingleCalendarRetrieval() throws Exception {
        for (String path : SINGLE_CALENDARS) {
            JsonNode full = assertSameInBothModes(offMvc, enforceMvc, path);
            assertEquals(full, assertSameInBothModes(offMvc, enforceMvc, path, "full", "true"));
            JsonNode simple = assertSameInBothModes(offMvc, enforceMvc, path, "full", "false");
            assertTrue(simple.get("responseType").asText().endsWith("-simple"));
            assertNotEquals(full.get("responseType"), simple.get("responseType"));
        }
    }
}
