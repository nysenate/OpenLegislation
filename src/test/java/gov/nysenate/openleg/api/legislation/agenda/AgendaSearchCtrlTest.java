package gov.nysenate.openleg.api.legislation.agenda;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.CollectionFullTestSupport;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.agenda.AgendaSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class AgendaSearchCtrlTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[] endpoints() {
        return new Object[]{"/api/3/agendas/search", "/api/3/agendas/2025/search"};
    }

    @Parameterized.Parameter
    public String endpoint;

    private final AgendaId agendaId = new AgendaId(1, 2025);
    private final CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Finance");
    private AgendaBillUtils agendaBillUtils;
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        AgendaDataService agendaData = mock(AgendaDataService.class);
        AgendaSearchService agendaSearch = mock(AgendaSearchService.class);
        agendaBillUtils = mock(AgendaBillUtils.class);
        SearchResults<CommitteeAgendaId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(new CommitteeAgendaId(agendaId, committeeId), BigDecimal.ONE, Map.of())),
                new LimitOffset(25, 1));
        when(agendaSearch.searchCommitteeAgendas(anyString(), anyString(), any(LimitOffset.class))).thenReturn(results);
        when(agendaSearch.searchCommitteeAgendas(anyString(), anyInt(), anyString(), any(LimitOffset.class)))
                .thenReturn(results);
        when(agendaData.getAgenda(agendaId)).thenReturn(new Agenda(agendaId));
        offMvc = mockMvc(policy -> new AgendaSearchCtrl(agendaData, agendaSearch, agendaBillUtils, policy), "off");
        enforceMvc = mockMvc(policy -> new AgendaSearchCtrl(agendaData, agendaSearch, agendaBillUtils, policy),
                "enforce");
    }

    @Test
    public void offReturnsSummariesOrFullCommitteeAgendas() throws Exception {
        JsonNode summary = search().at("/result/items/0/result");
        assertEquals("Finance", summary.at("/committeeId/name").asText());
        assertTrue(summary.has("totalBillsConsidered"));
        assertFalse(summary.has("committee"));
        verifyNoInteractions(agendaBillUtils);
        JsonNode full = search("full", "true").at("/result/items/0/result");
        assertEquals("Finance", full.at("/committee/committeeId/name").asText());
        assertTrue(full.has("agenda"));
        verify(agendaBillUtils).getBillInfoMap(any(Agenda.class), eq(committeeId));
    }

    @Test
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        assertSameInBothModes();
        assertSameInBothModes("full", "false");
        verifyNoInteractions(agendaBillUtils);
    }

    private void assertSameInBothModes(String... params) throws Exception {
        CollectionFullTestSupport.assertSameInBothModes(offMvc, enforceMvc, endpoint, withTerm(params));
    }

    private JsonNode search(String... params) throws Exception {
        return ok(offMvc, endpoint, withTerm(params));
    }

    private static String[] withTerm(String... params) {
        return concat(new String[]{"term", "*"}, params);
    }
}
