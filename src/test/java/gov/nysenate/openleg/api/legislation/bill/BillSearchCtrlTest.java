package gov.nysenate.openleg.api.legislation.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.bill.BillSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class BillSearchCtrlTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[] endpoints() {
        return new Object[]{"/api/3/bills/search", "/api/3/bills/2025/search"};
    }

    @Parameterized.Parameter
    public String endpoint;

    private final BaseBillId billId = new BaseBillId("S123", 2025);
    private BillDataService billData;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        billData = mock(BillDataService.class);
        BillSearchService billSearch = mock(BillSearchService.class);
        SearchResults<BaseBillId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(billId, BigDecimal.ONE, Map.of("title", List.of("education")))),
                new LimitOffset(25, 1));
        when(billSearch.searchBills(anyString(), anyString(), any(LimitOffset.class))).thenReturn(results);
        when(billSearch.searchBills(anyString(), any(SessionYear.class), anyString(), any(LimitOffset.class)))
                .thenReturn(results);
        mockMvc = standaloneSetup(new BillSearchCtrl(billData, billSearch)).build();
    }

    @Test
    public void idsOnlyReturnsIdsAndSearchMetadataWithoutFetchingBills() throws Exception {
        JsonNode response = search("idsOnly", "true");
        JsonNode item = response.at("/result/items/0");
        assertEquals("S123", item.at("/result/printNo").asText());
        assertEquals(2025, item.at("/result/session").asInt());
        assertFalse(item.get("result").has("summary"));
        assertEquals(1, item.get("rank").asInt());
        assertEquals("education", item.at("/highlights/title/0").asText());
        assertEquals(1, response.get("total").asInt());
        verifyNoInteractions(billData);
    }

    @Test
    public void omittedIdsOnlyReturnsBillInfo() throws Exception {
        search();
        verify(billData).getBillInfo(billId);
        verifyNoMoreInteractions(billData);
    }

    @Test
    public void falseIdsOnlyReturnsBillInfo() throws Exception {
        search("idsOnly", "false");
        verify(billData).getBillInfo(billId);
        verifyNoMoreInteractions(billData);
    }

    @Test
    public void idsOnlyTakesPrecedenceOverFull() throws Exception {
        assertEquals(search("idsOnly", "true"), search("idsOnly", "true", "full", "true"));
        verifyNoInteractions(billData);
    }

    @Test
    public void fullReturnsBillsWhenIdsOnlyIsFalse() throws Exception {
        search("idsOnly", "false", "full", "true");
        verify(billData).getBill(billId);
        verifyNoMoreInteractions(billData);
    }

    @Test
    public void invalidIdsOnlyIsRejected() throws Exception {
        mockMvc.perform(get(endpoint).param("term", "education").param("idsOnly", "invalid"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(billData);
    }

    private JsonNode search(String... params) throws Exception {
        MockHttpServletRequestBuilder request = get(endpoint).param("term", "education");
        for (int i = 0; i < params.length; i += 2) {
            request.param(params[i], params[i + 1]);
        }
        String response = mockMvc.perform(request).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readTree(response);
    }
}
