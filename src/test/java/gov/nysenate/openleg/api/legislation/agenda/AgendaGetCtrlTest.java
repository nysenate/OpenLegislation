package gov.nysenate.openleg.api.legislation.agenda;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AgendaGetCtrlTest {
    private static final String LISTING = "/api/3/agendas/2025";
    private static final List<AgendaId> AGENDA_IDS =
            IntStream.rangeClosed(1, 30).mapToObj(number -> new AgendaId(number, 2025)).toList();

    private AgendaDataService agendaData;
    private MockMvc mvc;

    @Before
    public void setUp() {
        agendaData = mock(AgendaDataService.class);
        when(agendaData.getAgendaIds(2025, SortOrder.ASC)).thenReturn(AGENDA_IDS);
        when(agendaData.getAgenda(any(AgendaId.class))).thenAnswer(invocation -> new Agenda(invocation.getArgument(0)));
        mvc = mockMvc(new AgendaGetCtrl(agendaData, mock(AgendaBillUtils.class)));
    }

    @After
    public void tearDown() {
        unbindSubject();
    }

    @Test
    public void laterPageReportsYearTotal() throws Exception {
        JsonNode body = ok(mvc, LISTING, "limit", "25", "offset", "26");
        assertPage(body, 30, 26, 30);
        assertEquals(List.of(26, 27, 28, 29, 30), agendaNumbers(body));
        verify(agendaData, times(5)).getAgenda(any(AgendaId.class));
        AGENDA_IDS.subList(25, 30).forEach(id -> verify(agendaData).getAgenda(id));
    }

    @Test
    public void defaultPageReportsYearTotal() throws Exception {
        JsonNode body = ok(mvc, LISTING);
        assertPage(body, 30, 1, 25);
        assertEquals(IntStream.rangeClosed(1, 25).boxed().toList(), agendaNumbers(body));
    }

    @Test
    public void offsetPastEndReportsYearTotal() throws Exception {
        JsonNode body = ok(mvc, LISTING, "offset", "31");
        assertEquals(30, body.get("total").asInt());
        assertEquals(List.of(), agendaNumbers(body));
        verify(agendaData, never()).getAgenda(any(AgendaId.class));
    }

    @Test
    public void unlimitedListingReturnsWholeYear() throws Exception {
        bindAllRolesSubject();
        JsonNode body = ok(mvc, LISTING, "limit", "0");
        assertPage(body, 30, 1, 30);
        assertEquals(IntStream.rangeClosed(1, 30).boxed().toList(), agendaNumbers(body));
    }

    private static List<Integer> agendaNumbers(JsonNode body) {
        return StreamSupport.stream(body.at("/result/items").spliterator(), false)
                .map(item -> item.at("/id/number").asInt()).toList();
    }
}
