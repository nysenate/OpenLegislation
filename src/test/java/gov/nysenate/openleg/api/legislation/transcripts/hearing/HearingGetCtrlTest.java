package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.hearing.HearingSearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class HearingGetCtrlTest {
    private static final List<String> LISTINGS = List.of("/api/3/hearings", "/api/3/hearings/2025");
    private static final List<String> SINGLE_HEARINGS = List.of("/api/3/hearings/1", "/api/3/hearings/hearing-file");

    private final HearingId hearingId = new HearingId(1);
    private HearingDataService hearingData;
    private HearingSearchService hearingSearch;
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        hearingData = mock(HearingDataService.class);
        hearingSearch = mock(HearingSearchService.class);
        Hearing hearing = new Hearing("hearing-file", "Hearing text", "Hearing title", "Albany",
                LocalDate.of(2025, 3, 4), LocalTime.of(10, 0), LocalTime.of(12, 0));
        hearing.setId(hearingId);
        hearing.setHosts(Set.of());
        stubSearch(1, new LimitOffset(25, 1), hearingId);
        when(hearingData.getHearing(hearingId)).thenReturn(hearing);
        when(hearingData.getHearing("hearing-file")).thenReturn(hearing);
        when(hearingData.getFilename(hearingId)).thenReturn("hearing-file");
        offMvc = mockMvc(policy -> new HearingGetCtrl(hearingData, hearingSearch, policy), "off");
        enforceMvc = mockMvc(policy -> new HearingGetCtrl(hearingData, hearingSearch, policy), "enforce");
    }

    @Test
    public void offListsIdsSummariesOrFullHearings() throws Exception {
        for (String listing : LISTINGS) {
            assertEquals("hearing-id list", ok(offMvc, listing).get("responseType").asText());
            assertEquals("hearing-info list", ok(offMvc, listing, "summary", "true").get("responseType").asText());
            JsonNode full = ok(offMvc, listing, "full", "true");
            assertEquals("hearing list", full.get("responseType").asText());
            assertEquals("Hearing text", full.at("/result/items/0/text").asText());
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
    public void enforceKeepsSingleHearingRetrieval() throws Exception {
        for (String path : SINGLE_HEARINGS) {
            for (String[] params : List.of(new String[0], new String[]{"full", "true"}, new String[]{"full", "false"})) {
                JsonNode body = assertSameInBothModes(offMvc, enforceMvc, path, params);
                assertEquals("hearing", body.get("responseType").asText());
                assertEquals("Hearing text", body.at("/result/text").asText());
            }
        }
        verifyNoInteractions(hearingSearch);
    }

    @Test
    public void listingsReportSearchTotal() throws Exception {
        stubSearch(120, new LimitOffset(25, 26), hearingId);
        for (String listing : LISTINGS) {
            for (String[] params : List.of(new String[0], new String[]{"summary", "true"}, new String[]{"full", "true"})) {
                JsonNode body = ok(offMvc, listing, concat(new String[]{"limit", "25", "offset", "26"}, params));
                assertPage(listing + query(params), body, 120, 26, 50);
            }
        }
        verify(hearingSearch, times(LISTINGS.size() * 3))
                .searchHearings(nullable(Integer.class), anyString(), eq(new LimitOffset(25, 26)));
    }

    @Test
    public void idListingLoadsOnlyFilenames() throws Exception {
        HearingId secondId = new HearingId(2);
        stubSearch(2, new LimitOffset(25, 1), hearingId, secondId);
        when(hearingData.getFilename(secondId)).thenReturn("second-file");
        JsonNode expectedItems = json("[{\"id\":1,\"filename\":\"hearing-file\"},{\"id\":2,\"filename\":\"second-file\"}]");
        for (String listing : LISTINGS) {
            assertEquals(listing, expectedItems, ok(offMvc, listing).at("/result/items"));
        }
        verify(hearingData, times(LISTINGS.size())).getFilename(hearingId);
        verify(hearingData, times(LISTINGS.size())).getFilename(secondId);
        verify(hearingData, never()).getHearing(any(HearingId.class));
    }

    @Test
    public void idListingOfMissingHearingIsNotFound() throws Exception {
        when(hearingData.getFilename(hearingId)).thenThrow(new HearingNotFoundEx(hearingId, null));
        for (String listing : LISTINGS) {
            MvcResult result = perform(offMvc, listing);
            assertEquals(listing, 404, result.getResponse().getStatus());
            assertEquals(listing, ErrorCode.HEARING_NOT_FOUND.getCode(), json(result).get("errorCode").asInt());
        }
    }

    @Test
    public void pdfIsServedForEveryAcceptedType() throws Exception {
        String text = Files.readString(Path.of("src/test/resources/hearingTranscripts/1-30-20 Human Services Test.txt"));
        Hearing hearing = new Hearing("hearing-file", text, "Hearing title", "Albany",
                LocalDate.of(2020, 1, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        hearing.setId(hearingId);
        hearing.setHosts(Set.of());
        when(hearingData.getHearing(hearingId)).thenReturn(hearing);
        when(hearingData.getHearing("hearing-file")).thenReturn(hearing);
        for (String path : SINGLE_HEARINGS) {
            assertServesPdf(offMvc, path + ".pdf");
        }
    }

    @Test
    public void pdfErrorsStayJsonWhenPdfIsAccepted() throws Exception {
        HearingId missing = new HearingId(9);
        when(hearingData.getHearing(missing)).thenThrow(new HearingNotFoundEx(missing, null));
        when(hearingData.getHearing("missing-file")).thenThrow(new HearingNotFoundEx("missing-file", null));
        assertJsonErrorWhenPdfAccepted(offMvc, "/api/3/hearings/9.pdf", 404);
        assertJsonErrorWhenPdfAccepted(offMvc, "/api/3/hearings/missing-file.pdf", 404);
    }

    @Test
    public void jsonEndpointsStillRejectPdfAndHtml() throws Exception {
        for (String path : List.of(LISTINGS.get(0), LISTINGS.get(1), SINGLE_HEARINGS.get(0), SINGLE_HEARINGS.get(1))) {
            assertNotAcceptable(offMvc, path, "application/pdf");
            assertNotAcceptable(offMvc, path, "text/html");
        }
    }

    private void stubSearch(int total, LimitOffset limitOffset, HearingId... ids) throws Exception {
        List<SearchResult<HearingId>> results = Arrays.stream(ids)
                .map(id -> new SearchResult<>(id, BigDecimal.ONE, Map.<String, List<String>>of())).toList();
        when(hearingSearch.searchHearings(nullable(Integer.class), anyString(), any(LimitOffset.class)))
                .thenReturn(new SearchResults<>(total, results, limitOffset));
    }
}
