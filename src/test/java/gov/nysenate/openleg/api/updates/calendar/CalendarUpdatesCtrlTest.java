package gov.nysenate.openleg.api.updates.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.updates.UpdateContentType;
import gov.nysenate.openleg.updates.UpdateDigest;
import gov.nysenate.openleg.updates.UpdateType;
import gov.nysenate.openleg.updates.calendar.CalendarUpdatesDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CalendarUpdatesCtrlTest {
    private static final int TOTAL = 18;
    private static final String CALENDAR_DIGESTS = "/api/3/calendars/2025/67/updates";

    private final CalendarId calendarId = new CalendarId(67, 2025);
    private CalendarUpdatesDao calendarUpdatesDao;
    private MockMvc mvc;

    @Before
    public void setUp() {
        calendarUpdatesDao = mock(CalendarUpdatesDao.class);
        // Pages through TOTAL digests the way the DAO does. Its total comes from the returned rows, so a
        // page past the end reports 0.
        when(calendarUpdatesDao.getDetailedUpdatesForCalendar(any(), any(), any(), any(), any()))
                .thenAnswer(inv -> {
                    LimitOffset limOff = inv.getArgument(4);
                    List<UpdateDigest<CalendarId>> digests = IntStream
                            .rangeClosed(limOff.offsetStart(), Math.min(limOff.getOffsetEnd(), TOTAL))
                            .mapToObj(this::digest)
                            .toList();
                    return new PaginatedList<>(digests.isEmpty() ? 0 : TOTAL, limOff, digests);
                });
        mvc = mockMvc(new CalendarUpdatesCtrl(calendarUpdatesDao));
    }

    @Test
    public void calendarDigestsReportTheRequestedPage() throws Exception {
        JsonNode body = ok(mvc, CALENDAR_DIGESTS, "limit", "2", "offset", "3");
        assertPage(body, TOTAL, 3, 4);
        assertEquals(2, body.get("limit").asInt());
        assertEquals(2, body.at("/result/size").asInt());
        verify(calendarUpdatesDao).getDetailedUpdatesForCalendar(eq(UpdateType.PROCESSED_DATE), eq(calendarId),
                any(), eq(SortOrder.ASC), eq(new LimitOffset(2, 3)));
    }

    @Test
    public void calendarDigestsDefaultToTheFirstHundred() throws Exception {
        JsonNode body = ok(mvc, CALENDAR_DIGESTS);
        assertPage(body, TOTAL, 1, TOTAL);
        assertEquals(100, body.get("limit").asInt());
    }

    @Test
    public void pagingByOffsetEndReturnsEveryDigest() throws Exception {
        int offset = 1, seen = 0;
        while (true) {
            JsonNode body = ok(mvc, CALENDAR_DIGESTS, "limit", "5", "offset", String.valueOf(offset));
            seen += body.at("/result/size").asInt();
            if (body.get("offsetEnd").asInt() >= body.get("total").asInt()) {
                break;
            }
            offset = body.get("offsetEnd").asInt() + 1;
        }
        assertEquals(TOTAL, seen);
    }

    @Test
    public void calendarDigestsReportTheDateRange() throws Exception {
        JsonNode body = ok(mvc, CALENDAR_DIGESTS + "/2026-10-01T00:00:00/2026-10-08T00:00:00");
        assertEquals("2026-10-01T00:00:00", body.get("fromDateTime").asText());
        assertEquals("2026-10-07T23:59:59.999999", body.get("toDateTime").asText());
    }

    @Test
    public void calendarDigestsWithoutDatesRunFrom1970UntilNow() throws Exception {
        assertRangeEndsNow(CALENDAR_DIGESTS, "1970-01-01T00:00:00");
    }

    @Test
    public void calendarDigestsWithoutToRunUntilNow() throws Exception {
        assertRangeEndsNow(CALENDAR_DIGESTS + "/2026-10-01T00:00:00", "2026-10-01T00:00:00");
    }

    /* --- Internal --- */

    /** Asserts that the request reports the given start and a range that ends at the time of the request. */
    private void assertRangeEndsNow(String path, String expectedFrom) throws Exception {
        LocalDateTime before = LocalDateTime.now();
        JsonNode body = ok(mvc, path);
        LocalDateTime after = LocalDateTime.now();
        assertEquals(path, expectedFrom, body.get("fromDateTime").asText());
        LocalDateTime to = LocalDateTime.parse(body.get("toDateTime").asText());
        assertTrue(path + " toDateTime " + to, !to.isBefore(before.minusNanos(1000)) && to.isBefore(after));
    }

    private UpdateDigest<CalendarId> digest(int n) {
        LocalDateTime dateTime = LocalDateTime.of(2026, 10, 2, 12, 0).plusMinutes(n);
        UpdateDigest<CalendarId> digest = new UpdateDigest<>(calendarId, UpdateContentType.CALENDAR,
                "2026-10-02-12.00.00.000000_SENFLVOTE_" + n + ".XML", dateTime, dateTime);
        digest.setAction("update");
        digest.setTable("calendar");
        return digest;
    }
}
