package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.CollectionFullTestSupport;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.hearing.HearingSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class HearingSearchCtrlTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[] endpoints() {
        return new Object[]{"/api/3/hearings/search", "/api/3/hearings/2025/search"};
    }

    @Parameterized.Parameter
    public String endpoint;

    private final HearingId hearingId = new HearingId(1);
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        HearingDataService hearingData = mock(HearingDataService.class);
        HearingSearchService hearingSearch = mock(HearingSearchService.class);
        Hearing hearing = new Hearing("hearing-file", "Hearing text", "Hearing title", "Albany",
                LocalDate.of(2025, 3, 4), LocalTime.of(10, 0), LocalTime.of(12, 0));
        hearing.setId(hearingId);
        hearing.setHosts(Set.of());
        SearchResults<HearingId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(hearingId, BigDecimal.ONE, Map.of("text", List.of("budget")))),
                new LimitOffset(25, 1));
        when(hearingSearch.searchHearings(anyString(), anyString(), any(LimitOffset.class))).thenReturn(results);
        when(hearingSearch.searchHearings(anyString(), anyInt(), anyString(), any(LimitOffset.class)))
                .thenReturn(results);
        when(hearingData.getHearing(hearingId)).thenReturn(hearing);
        when(hearingData.getFilename(hearingId)).thenReturn("hearing-file");
        offMvc = mockMvc(policy -> new HearingSearchCtrl(hearingData, hearingSearch, policy), "off");
        enforceMvc = mockMvc(policy -> new HearingSearchCtrl(hearingData, hearingSearch, policy), "enforce");
    }

    @Test
    public void offReturnsSummariesIdsOrFullHearings() throws Exception {
        JsonNode summary = search().at("/result/items/0/result");
        assertEquals("Hearing title", summary.get("title").asText());
        assertFalse(summary.has("text"));
        JsonNode id = search("summary", "false").at("/result/items/0/result");
        assertEquals("hearing-file", id.get("filename").asText());
        assertFalse(id.has("title"));
        JsonNode full = search("full", "true");
        assertEquals("Hearing text", full.at("/result/items/0/result/text").asText());
        assertEquals("budget", full.at("/result/items/0/highlights/text/0").asText());
        assertEquals(full, search("full", "true", "summary", "true"));
        assertEquals(full, search("full", "true", "summary", "false"));
    }

    @Test
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        assertSameInBothModes();
        assertSameInBothModes("summary", "false");
        assertSameInBothModes("full", "false");
        assertSameInBothModes("full", "false", "summary", "false");
    }

    private void assertSameInBothModes(String... params) throws Exception {
        CollectionFullTestSupport.assertSameInBothModes(offMvc, enforceMvc, endpoint, withTerm(params));
    }

    private JsonNode search(String... params) throws Exception {
        return ok(offMvc, endpoint, withTerm(params));
    }

    private static String[] withTerm(String... params) {
        return concat(new String[]{"term", "budget"}, params);
    }
}
