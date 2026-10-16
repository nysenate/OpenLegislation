package gov.nysenate.openleg.api.legislation.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.bill.BillSearchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.legislation.bill.BillCollectionFullTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
    private BillSearchService billSearch;
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() throws Exception {
        billData = mock(BillDataService.class);
        billSearch = mock(BillSearchService.class);
        SearchResults<BaseBillId> results = new SearchResults<>(1,
                List.of(new SearchResult<>(billId, BigDecimal.ONE, Map.of("title", List.of("education")))),
                new LimitOffset(25, 1));
        when(billSearch.searchBills(anyString(), anyString(), any(LimitOffset.class))).thenReturn(results);
        when(billSearch.searchBills(anyString(), any(SessionYear.class), anyString(), any(LimitOffset.class)))
                .thenReturn(results);
        offMvc = mockMvc(policy -> new BillSearchCtrl(billData, billSearch, policy), "off");
        enforceMvc = mockMvc(policy -> new BillSearchCtrl(billData, billSearch, policy), "enforce");
    }

    @After
    public void tearDown() {
        unbindSubject();
    }

    /* --- Off mode --- */

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
    public void offExpandsEveryTrueValue() throws Exception {
        for (String value : TRUE_VALUES) {
            search("full", value);
            verify(billData).getBill(billId);
            verifyNoMoreInteractions(billData);
            clearInvocations(billData);
        }
    }

    @Test
    public void invalidIdsOnlyIsRejected() throws Exception {
        MvcResult result = perform(offMvc, "idsOnly", "invalid");
        assertEquals(400, result.getResponse().getStatus());
        JsonNode body = json(result);
        assertEquals(ErrorCode.INVALID_ARGUMENTS.getMessage(), body.get("message").asText());
        assertEquals("idsOnly", body.at("/errorData/parameterConstraint/name").asText());
        assertEquals("boolean", body.at("/errorData/parameterConstraint/type").asText());
        assertEquals("invalid", body.at("/errorData/receivedValue").asText());
        verifyNoInteractions(billData);
    }

    /* --- Enforce mode --- */

    @Test
    public void enforceRejectsTrueValuesWithoutLoadingBills() throws Exception {
        for (String value : TRUE_VALUES) {
            assertRejected(decoded(value), "full", value);
            assertRejected(decoded(value), "full", value, "idsOnly", "true");
        }
        verifyNoInteractions(billData);
    }

    @Test
    public void enforceRejectsCallerWithEveryRole() throws Exception {
        bindAllRolesSubject();
        assertRejected("true", "full", "true");
        assertRejected("true", "full", "true", "limit", "0");
        verifyNoInteractions(billData);
    }

    @Test
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        assertSameInBothModes();
        assertSameInBothModes("idsOnly", "true");
        for (String value : FALSE_VALUES) {
            assertSameInBothModes("full", value);
            assertSameInBothModes("full", value, "idsOnly", "true");
        }
    }

    @Test
    public void enforceKeepsSenateLimitZero() throws Exception {
        bindAllRolesSubject();
        assertSameInBothModes("limit", "0");
        assertSameInBothModes("full", "false", "limit", "0");
    }

    @Test
    public void invalidFullKeepsTypeMismatch() throws Exception {
        for (MockMvc mvc : List.of(offMvc, enforceMvc)) {
            MvcResult result = perform(mvc, "full", "maybe");
            assertEquals(400, result.getResponse().getStatus());
            JsonNode body = json(result);
            assertEquals(ErrorCode.INVALID_ARGUMENTS.getMessage(), body.get("message").asText());
            assertEquals("full", body.at("/errorData/parameterConstraint/name").asText());
            assertEquals("must be convertible to type: boolean",
                    body.at("/errorData/parameterConstraint/constraint").asText());
            assertEquals("maybe", body.at("/errorData/receivedValue").asText());
        }
        verifyNoInteractions(billSearch, billData);
    }

    /* --- Internal --- */

    private void assertRejected(String receivedValue, String... params) throws Exception {
        MvcResult result = perform(enforceMvc, params);
        assertEquals(400, result.getResponse().getStatus());
        assertEquals(expectedRejection(receivedValue), json(result));
    }

    private void assertSameInBothModes(String... params) throws Exception {
        MvcResult off = perform(offMvc, params);
        MvcResult enforce = perform(enforceMvc, params);
        assertEquals(200, off.getResponse().getStatus());
        assertEquals(200, enforce.getResponse().getStatus());
        assertEquals(json(off), json(enforce));
    }

    private JsonNode search(String... params) throws Exception {
        MvcResult result = perform(offMvc, params);
        assertEquals(200, result.getResponse().getStatus());
        return json(result);
    }

    private MvcResult perform(MockMvc mvc, String... params) throws Exception {
        return performAt(mvc, endpoint, params);
    }

    private static MvcResult performAt(MockMvc mvc, String path, String... params) throws Exception {
        String uri = path + query(concat(new String[]{"term", "education"}, params));
        return mvc.perform(get(URI.create(uri))).andReturn();
    }

    private static JsonNode json(MvcResult result) throws Exception {
        return new ObjectMapper().readTree(result.getResponse().getContentAsString());
    }
}
