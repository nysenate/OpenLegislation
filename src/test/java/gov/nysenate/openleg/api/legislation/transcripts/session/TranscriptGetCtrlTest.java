package gov.nysenate.openleg.api.legislation.transcripts.session;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class TranscriptGetCtrlTest {
    private static final List<String> LISTINGS = List.of("/api/3/transcripts", "/api/3/transcripts/2025");
    private static final List<String> SINGLE_TRANSCRIPTS = List.of(
            "/api/3/transcripts/2025-01-08T15:00", "/api/3/transcripts/2025-01-08T15:00/Regular%20Session");

    private final TranscriptId transcriptId = TranscriptId.from(LocalDateTime.of(2025, 1, 8, 15, 0), "Regular Session");
    private TranscriptSearchService transcriptSearch;
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        TranscriptDataService transcriptData = mock(TranscriptDataService.class);
        transcriptSearch = mock(TranscriptSearchService.class);
        Transcript transcript = new Transcript(transcriptId, DayType.SESSION, "010825.txt", "Albany", "Transcript text");
        SearchResults<TranscriptId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(transcriptId, BigDecimal.ONE, Map.of())), new LimitOffset(25, 1));
        when(transcriptSearch.searchTranscripts(anyString(), any(LimitOffset.class), anyBoolean())).thenReturn(results);
        when(transcriptSearch.searchTranscripts(anyInt(), anyString(), any(LimitOffset.class), anyBoolean()))
                .thenReturn(results);
        when(transcriptData.getTranscript(any(TranscriptId.class))).thenReturn(transcript);
        when(transcriptData.getTranscriptByDateTime(transcriptId.dateTime())).thenReturn(transcript);
        offMvc = mockMvc(policy -> new TranscriptGetCtrl(transcriptData, transcriptSearch, policy), "off");
        enforceMvc = mockMvc(policy -> new TranscriptGetCtrl(transcriptData, transcriptSearch, policy), "enforce");
    }

    @Test
    public void offListsIdsSummariesOrFullTranscripts() throws Exception {
        for (String listing : LISTINGS) {
            assertEquals("transcript-id list", ok(offMvc, listing).get("responseType").asText());
            assertEquals("transcript-info list", ok(offMvc, listing, "summary", "true").get("responseType").asText());
            JsonNode full = ok(offMvc, listing, "full", "true");
            assertEquals("transcript list", full.get("responseType").asText());
            assertEquals("Transcript text", full.at("/result/items/0/text").asText());
            assertEquals(full, ok(offMvc, listing, "full", "true", "summary", "true"));
        }
    }

    @Test
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        for (String listing : LISTINGS) {
            assertSameInBothModes(offMvc, enforceMvc, listing);
            assertSameInBothModes(offMvc, enforceMvc, listing, "summary", "true");
            assertSameInBothModes(offMvc, enforceMvc, listing, "full", "false");
            assertSameInBothModes(offMvc, enforceMvc, listing, "full", "false", "summary", "true");
        }
    }

    @Test
    public void enforceKeepsSingleTranscriptRetrieval() throws Exception {
        for (String path : SINGLE_TRANSCRIPTS) {
            for (String[] params : List.of(new String[0], new String[]{"full", "true"}, new String[]{"full", "false"})) {
                JsonNode body = assertSameInBothModes(offMvc, enforceMvc, path, params);
                assertEquals("transcript", body.get("responseType").asText());
                assertEquals("Transcript text", body.at("/result/text").asText());
            }
        }
        verifyNoInteractions(transcriptSearch);
    }
}
