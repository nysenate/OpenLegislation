package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.legislation.agenda.AgendaBillUtils;
import gov.nysenate.openleg.api.legislation.agenda.AgendaSearchCtrl;
import gov.nysenate.openleg.api.legislation.bill.BillGetCtrl;
import gov.nysenate.openleg.api.legislation.bill.BillSearchCtrl;
import gov.nysenate.openleg.api.legislation.calendar.CalendarGetCtrl;
import gov.nysenate.openleg.api.legislation.calendar.CalendarSearchCtrl;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.HearingGetCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.HearingSearchCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.TranscriptGetCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.TranscriptSearchCtrl;
import gov.nysenate.openleg.api.updates.bill.BillUpdatesCtrl;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.agenda.AgendaSearchService;
import gov.nysenate.openleg.search.bill.BillSearchService;
import gov.nysenate.openleg.search.calendar.CalendarSearchService;
import gov.nysenate.openleg.search.transcripts.hearing.HearingSearchService;
import gov.nysenate.openleg.search.transcripts.session.TranscriptSearchService;
import gov.nysenate.openleg.updates.bill.BillUpdatesDao;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static gov.nysenate.openleg.api.CollectionFullPolicy.Option.*;
import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Every retired route, with its option, against the real controllers and mocked services. How a
 * value binds is covered once, by the bill listing and bill update feed tests.
 */
@Category(UnitTest.class)
public class CollectionFullRoutesTest {
    private record Route(String path, CollectionFullPolicy.Option option, String... params) {}

    private static final String FROM = "2026-10-01T00:00:00";
    private static final String TO = "2026-10-08T00:00:00";
    private static final List<Route> ROUTES = List.of(
            new Route("/api/3/bills/2025", BILL_FULL),
            new Route("/api/3/bills/search", BILL_FULL, "term", "x"),
            new Route("/api/3/bills/2025/search", BILL_FULL, "term", "x"),
            new Route("/api/3/bills/updates", BILL_UPDATES_FULL_BILL),
            new Route("/api/3/bills/updates/" + FROM, BILL_UPDATES_FULL_BILL),
            new Route("/api/3/bills/updates/" + FROM + "/" + TO, BILL_UPDATES_FULL_BILL),
            new Route("/api/3/hearings", HEARING_FULL),
            new Route("/api/3/hearings/2025", HEARING_FULL),
            new Route("/api/3/hearings/search", HEARING_FULL, "term", "x"),
            new Route("/api/3/hearings/2025/search", HEARING_FULL, "term", "x"),
            new Route("/api/3/transcripts", TRANSCRIPT_FULL),
            new Route("/api/3/transcripts/2025", TRANSCRIPT_FULL),
            new Route("/api/3/transcripts/search", TRANSCRIPT_FULL, "term", "x"),
            new Route("/api/3/transcripts/2025/search", TRANSCRIPT_FULL, "term", "x"),
            new Route("/api/3/calendars/2025", CALENDAR_FULL),
            new Route("/api/3/calendars/2025/activelist", ACTIVE_LIST_FULL),
            new Route("/api/3/calendars/2025/supplemental", SUPPLEMENTAL_FULL),
            new Route("/api/3/calendars/search", CALENDAR_SEARCH_DETAIL, "term", "x"),
            new Route("/api/3/calendars/2025/search", CALENDAR_SEARCH_DETAIL, "term", "x"),
            new Route("/api/3/agendas/search", AGENDA_SEARCH_FULL, "term", "x"),
            new Route("/api/3/agendas/2025/search", AGENDA_SEARCH_FULL, "term", "x"));

    private final BillDataService billData = mock(BillDataService.class);
    private final BillSearchService billSearch = mock(BillSearchService.class);
    private final BillUpdatesDao billUpdatesDao = mock(BillUpdatesDao.class);
    private final HearingDataService hearingData = mock(HearingDataService.class);
    private final HearingSearchService hearingSearch = mock(HearingSearchService.class);
    private final TranscriptDataService transcriptData = mock(TranscriptDataService.class);
    private final TranscriptSearchService transcriptSearch = mock(TranscriptSearchService.class);
    private final CalendarDataService calendarData = mock(CalendarDataService.class);
    private final CalendarSearchService calendarSearch = mock(CalendarSearchService.class);
    private final CalendarViewFactory calendarViewFactory = mock(CalendarViewFactory.class);
    private final AgendaDataService agendaData = mock(AgendaDataService.class);
    private final AgendaSearchService agendaSearch = mock(AgendaSearchService.class);
    private final AgendaBillUtils agendaBillUtils = mock(AgendaBillUtils.class);

    private final CollectionFullPolicy policy = policy("enforce");
    private final MockMvc enforceMvc = mockMvc(
            new BillGetCtrl(billData, billSearch, policy), new BillSearchCtrl(billData, billSearch, policy),
            new BillUpdatesCtrl(billUpdatesDao, billData, policy),
            new HearingGetCtrl(hearingData, hearingSearch, policy),
            new HearingSearchCtrl(hearingData, hearingSearch, policy),
            new TranscriptGetCtrl(transcriptData, transcriptSearch, policy),
            new TranscriptSearchCtrl(transcriptData, transcriptSearch, policy),
            new CalendarGetCtrl(calendarData, calendarViewFactory, policy),
            new CalendarSearchCtrl(calendarSearch, calendarData, calendarViewFactory, policy),
            new AgendaSearchCtrl(agendaData, agendaSearch, agendaBillUtils, policy));

    @After
    public void tearDown() {
        unbindSubject();
    }

    @Test
    public void enforceRejectsEveryRetiredRouteWithoutWork() throws Exception {
        for (Route route : ROUTES) {
            String name = route.option().getParameterName();
            assertRejected(enforceMvc, route.path(), route.option(), "true", concat(route.params(), name, "true"));
            assertRejected(enforceMvc, route.path(), route.option(), "true",
                    concat(route.params(), name, "true", "summary", "true", "idsOnly", "true"));
        }
        bindAllRolesSubject();
        for (Route route : ROUTES) {
            assertRejected(enforceMvc, route.path(), route.option(), "true",
                    concat(route.params(), route.option().getParameterName(), "true", "limit", "0"));
        }
        verifyNoInteractions(billData, billSearch, billUpdatesDao, hearingData, hearingSearch, transcriptData,
                transcriptSearch, calendarData, calendarSearch, calendarViewFactory, agendaData, agendaSearch,
                agendaBillUtils);
    }
}
