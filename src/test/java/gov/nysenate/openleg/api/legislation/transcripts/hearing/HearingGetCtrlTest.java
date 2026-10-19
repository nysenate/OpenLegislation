package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
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
        when(hearingSearch.searchHearings(nullable(Integer.class), anyString(), any(LimitOffset.class)))
                .thenReturn(new SearchResults<>(1, List.of(new SearchResult<>(hearingId, BigDecimal.ONE, Map.of())),
                        new LimitOffset(25, 1)));
        when(hearingData.getHearing(hearingId)).thenReturn(hearing);
        when(hearingData.getHearing("hearing-file")).thenReturn(hearing);
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
}
