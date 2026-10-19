package gov.nysenate.openleg.api.legislation.bill;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullPolicy.Option.BILL_FULL;
import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class BillGetCtrlTest {
    private static final String LISTING = "/api/3/bills/2025";
    private static final String SINGLE_BILL = "/api/3/bills/2025/S123";

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
                List.of(new SearchResult<>(billId, BigDecimal.ONE, Map.of())), new LimitOffset(50, 1));
        when(billSearch.searchBills(any(SessionYear.class), anyString(), any(LimitOffset.class))).thenReturn(results);
        offMvc = mockMvc(policy -> new BillGetCtrl(billData, billSearch, policy), "off");
        enforceMvc = mockMvc(policy -> new BillGetCtrl(billData, billSearch, policy), "enforce");
    }

    @After
    public void tearDown() {
        unbindSubject();
    }

    /* --- Off mode --- */

    @Test
    public void omittedFullReturnsBillInfo() throws Exception {
        JsonNode response = list();
        assertEquals(1, response.get("total").asInt());
        verify(billData).getBillInfo(billId);
        verifyNoMoreInteractions(billData);
    }

    @Test
    public void offExpandsEveryTrueValue() throws Exception {
        for (String value : BOUND_TRUE_VALUES) {
            list("full", value);
            verify(billData).getBill(billId);
            verifyNoMoreInteractions(billData);
            clearInvocations(billData);
        }
    }

    @Test
    public void idsOnlyLoadsNoBill() throws Exception {
        JsonNode response = list("idsOnly", "true");
        assertEquals("S123", response.at("/result/items/0/basePrintNo").asText());
        assertEquals(list("idsOnly", "true"), list("idsOnly", "true", "full", "true"));
        verifyNoInteractions(billData);
    }

    /* --- Enforce mode --- */

    @Test
    public void enforceRejectsTrueValuesWithoutLoadingBills() throws Exception {
        for (String value : BOUND_TRUE_VALUES) {
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
        for (String value : BOUND_FALSE_VALUES) {
            assertSameInBothModes("full", value);
            assertSameInBothModes("full", value, "idsOnly", "true");
        }
    }

    @Test
    public void enforceKeepsSenateLimitZero() throws Exception {
        bindAllRolesSubject();
        assertSameInBothModes("limit", "0");
        assertSameInBothModes("full", "false", "limit", "all");
    }

    @Test
    public void invalidFullKeepsTypeMismatch() throws Exception {
        for (MockMvc mvc : List.of(offMvc, enforceMvc)) {
            MvcResult result = perform(mvc, LISTING, "full", "maybe");
            assertEquals(400, result.getResponse().getStatus());
            assertEquals("full", json(result).at("/errorData/parameterConstraint/name").asText());
            assertEquals("maybe", json(result).at("/errorData/receivedValue").asText());
        }
        verifyNoInteractions(billSearch, billData);
    }

    @Test
    public void enforceKeepsSingleBillRetrieval() throws Exception {
        for (String[] params : List.of(new String[0], new String[]{"full", "true"})) {
            MvcResult off = perform(offMvc, SINGLE_BILL, params);
            MvcResult enforce = perform(enforceMvc, SINGLE_BILL, params);
            assertEquals(200, enforce.getResponse().getStatus());
            assertEquals(json(off), json(enforce));
            verify(billData, times(2)).getBill(billId);
            verifyNoMoreInteractions(billData);
            clearInvocations(billData);
        }
        MvcResult info = perform(enforceMvc, SINGLE_BILL, "view", "info");
        assertEquals(200, info.getResponse().getStatus());
        verify(billData).getBillInfo(billId);
        verifyNoMoreInteractions(billData);
        verifyNoInteractions(billSearch);
    }

    /* --- Internal --- */

    private void assertRejected(String receivedValue, String... params) throws Exception {
        MvcResult result = perform(enforceMvc, LISTING, params);
        assertEquals(400, result.getResponse().getStatus());
        assertEquals(expectedRejection(BILL_FULL, receivedValue), json(result));
    }

    private void assertSameInBothModes(String... params) throws Exception {
        MvcResult off = perform(offMvc, LISTING, params);
        MvcResult enforce = perform(enforceMvc, LISTING, params);
        assertEquals(200, off.getResponse().getStatus());
        assertEquals(200, enforce.getResponse().getStatus());
        assertEquals(json(off), json(enforce));
    }

    private JsonNode list(String... params) throws Exception {
        MvcResult result = perform(offMvc, LISTING, params);
        assertEquals(200, result.getResponse().getStatus());
        return json(result);
    }
}
