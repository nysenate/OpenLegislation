package gov.nysenate.openleg.api.legislation.transcripts.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.session.dao.SqlTranscriptDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDao;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.session.TranscriptSearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TranscriptGetCtrlTest {
    private static final List<String> LISTINGS = List.of("/api/3/transcripts", "/api/3/transcripts/2025");
    private static final List<String> SINGLE_TRANSCRIPTS = List.of(
            "/api/3/transcripts/2025-01-08T15:00", "/api/3/transcripts/2025-01-08T15:00/Regular%20Session");

    private final TranscriptId transcriptId = TranscriptId.from(LocalDateTime.of(2025, 1, 8, 15, 0), "Regular Session");
    private TranscriptDataService transcriptData;
    private TranscriptSearchService transcriptSearch;
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        transcriptData = mock(TranscriptDataService.class);
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

    @Test
    public void missingTranscriptReturnsNotFound() throws Exception {
        // The real data service, so the test covers the id it reports for a lookup by date time alone
        TranscriptDao transcriptDao = mock(TranscriptDao.class);
        when(transcriptDao.getTranscript(any(TranscriptId.class))).thenThrow(new EmptyResultDataAccessException(1));
        MockMvc mvc = mockMvc(policy -> new TranscriptGetCtrl(
                new SqlTranscriptDataService(new EventBus(), transcriptDao), transcriptSearch, policy), "off");
        for (String path : List.of("/api/3/transcripts/2025-01-09T11:00", "/api/3/transcripts/2025-01-09T11:00.pdf")) {
            JsonNode body = notFound(mvc, path);
            assertEquals("2025-01-09T11:00", body.at("/errorData/dateTime").asText());
            assertTrue(path, body.at("/errorData/sessionType").isNull());
        }
        for (String path : List.of("/api/3/transcripts/2025-01-09T11:00/Regular%20Session",
                "/api/3/transcripts/2025-01-09T11:00/Regular%20Session.pdf")) {
            assertEquals("Regular Session", notFound(mvc, path).at("/errorData/sessionType").asText());
        }
    }

    @Test
    public void pdfIsServedForEveryAcceptedType() throws Exception {
        String text = Files.readString(Path.of("src/test/resources/transcriptFiles/forPdfParser/2020-01-01T1100"));
        Transcript transcript = new Transcript(transcriptId, DayType.SESSION, "010825.txt", "Albany", text);
        when(transcriptData.getTranscript(transcriptId)).thenReturn(transcript);
        when(transcriptData.getTranscriptByDateTime(transcriptId.dateTime())).thenReturn(transcript);
        for (String path : SINGLE_TRANSCRIPTS) {
            assertServesPdf(offMvc, path + ".pdf");
        }
    }

    @Test
    public void pdfErrorsStayJsonWhenPdfIsAccepted() throws Exception {
        TranscriptId missing = TranscriptId.from(LocalDateTime.of(2030, 1, 1, 1, 0), "Regular Session");
        when(transcriptData.getTranscriptByDateTime(missing.dateTime()))
                .thenThrow(new TranscriptNotFoundEx(missing, null));
        when(transcriptData.getTranscript(missing)).thenThrow(new TranscriptNotFoundEx(missing, null));
        assertJsonErrorWhenPdfAccepted(offMvc, "/api/3/transcripts/2030-01-01T01:00.pdf", 404);
        assertJsonErrorWhenPdfAccepted(offMvc, "/api/3/transcripts/2030-01-01T01:00/Regular%20Session.pdf", 404);
        assertJsonErrorWhenPdfAccepted(offMvc, "/api/3/transcripts/notadate.pdf", 400);
    }

    @Test
    public void jsonEndpointsStillRejectPdfAndHtml() throws Exception {
        for (String path : List.of(LISTINGS.get(0), LISTINGS.get(1), SINGLE_TRANSCRIPTS.get(0), SINGLE_TRANSCRIPTS.get(1))) {
            assertNotAcceptable(offMvc, path, "application/pdf");
            assertNotAcceptable(offMvc, path, "text/html");
        }
    }

    /* --- Internal --- */

    /** Asserts that the request returns a transcript-not-found error, and returns its body. */
    private static JsonNode notFound(MockMvc mvc, String path) throws Exception {
        MvcResult result = perform(mvc, path);
        assertEquals(path, 404, result.getResponse().getStatus());
        JsonNode body = json(result);
        assertEquals(path, 141, body.get("errorCode").asInt());
        return body;
    }
}
