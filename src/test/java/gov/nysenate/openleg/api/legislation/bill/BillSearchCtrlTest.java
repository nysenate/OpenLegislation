package gov.nysenate.openleg.api.legislation.bill;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.CollectionFullTestSupport;
import gov.nysenate.openleg.api.CtrlTestSupport;
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
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    public void enforceKeepsFalseAndOmittedResponses() throws Exception {
        assertSameInBothModes();
        assertSameInBothModes("idsOnly", "true");
        assertSameInBothModes("full", "false");
        assertSameInBothModes("full", "false", "idsOnly", "true");
    }

    @Test
    public void enforceKeepsSenateLimitZero() throws Exception {
        bindAllRolesSubject();
        assertSameInBothModes("limit", "0");
        assertSameInBothModes("full", "false", "limit", "0");
    }

    /* --- Internal --- */

    private void assertSameInBothModes(String... params) throws Exception {
        CollectionFullTestSupport.assertSameInBothModes(offMvc, enforceMvc, endpoint, withTerm(params));
    }

    private JsonNode search(String... params) throws Exception {
        return ok(offMvc, endpoint, withTerm(params));
    }

    private MvcResult perform(MockMvc mvc, String... params) throws Exception {
        return CtrlTestSupport.perform(mvc, endpoint, withTerm(params));
    }

    private static String[] withTerm(String... params) {
        return concat(new String[]{"term", "education"}, params);
    }
}
