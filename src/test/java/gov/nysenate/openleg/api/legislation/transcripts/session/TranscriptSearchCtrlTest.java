package gov.nysenate.openleg.api.legislation.transcripts.session;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.CollectionFullTestSupport;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.session.TranscriptSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class TranscriptSearchCtrlTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[] endpoints() {
        return new Object[]{"/api/3/transcripts/search", "/api/3/transcripts/2025/search"};
    }

    @Parameterized.Parameter
    public String endpoint;

    private final TranscriptId transcriptId = TranscriptId.from(LocalDateTime.of(2025, 1, 8, 15, 0), "Regular Session");
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        TranscriptDataService transcriptData = mock(TranscriptDataService.class);
        TranscriptSearchService transcriptSearch = mock(TranscriptSearchService.class);
        Transcript transcript = new Transcript(transcriptId, DayType.SESSION, "010825.txt", "Albany", "Transcript text");
        SearchResults<TranscriptId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(transcriptId, BigDecimal.ONE, Map.of("text", List.of("budget")))),
                new LimitOffset(25, 1));
        when(transcriptSearch.searchTranscripts(anyString(), anyString(), any(LimitOffset.class), anyBoolean()))
                .thenReturn(results);
        when(transcriptSearch.searchTranscripts(anyString(), anyInt(), anyString(), any(LimitOffset.class), anyBoolean()))
                .thenReturn(results);
        when(transcriptData.getTranscript(transcriptId)).thenReturn(transcript);
        offMvc = mockMvc(policy -> new TranscriptSearchCtrl(transcriptData, transcriptSearch, policy), "off");
        enforceMvc = mockMvc(policy -> new TranscriptSearchCtrl(transcriptData, transcriptSearch, policy), "enforce");
    }

    @Test
    public void offReturnsSummariesIdsOrFullTranscripts() throws Exception {
        JsonNode summary = search().at("/result/items/0/result");
        assertEquals("Albany", summary.get("location").asText());
        assertFalse(summary.has("text"));
        JsonNode id = search("summary", "false").at("/result/items/0/result");
        assertEquals("Regular Session", id.get("sessionType").asText());
        assertFalse(id.has("location"));
        JsonNode full = search("full", "true");
        assertEquals("Transcript text", full.at("/result/items/0/result/text").asText());
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
