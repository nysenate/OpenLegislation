package gov.nysenate.openleg.api.updates.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillUpdateField;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.updates.UpdateContentType;
import gov.nysenate.openleg.updates.UpdateDigest;
import gov.nysenate.openleg.updates.UpdateToken;
import gov.nysenate.openleg.updates.UpdateType;
import gov.nysenate.openleg.updates.bill.BillUpdatesDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullPolicy.Option.BILL_UPDATES_FULL_BILL;
import static gov.nysenate.openleg.api.CollectionFullTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class BillUpdatesCtrlTest {
    private static final String FROM = "2026-10-01T00:00:00";
    private static final String TO = "2026-10-08T00:00:00";
    private static final List<String> FEEDS = List.of(
            "/api/3/bills/updates",
            "/api/3/bills/updates/" + FROM,
            "/api/3/bills/updates/" + FROM + "/" + TO);
    private static final String BILL_DIGESTS = "/api/3/bills/2025/S123/updates";

    private final BaseBillId billId = new BaseBillId("S123", 2025);
    private BillUpdatesDao billUpdatesDao;
    private BillDataService billData;
    private MockMvc offMvc;
    private MockMvc enforceMvc;

    @Before
    public void setUp() {
        billUpdatesDao = mock(BillUpdatesDao.class);
        billData = mock(BillDataService.class);
        LocalDateTime dateTime = LocalDateTime.of(2026, 10, 2, 12, 0);
        UpdateToken<BaseBillId> token = new UpdateToken<>(billId, UpdateContentType.BILL, "SOBI.D261002.T120000.TXT",
                dateTime, dateTime);
        UpdateDigest<BaseBillId> digest = new UpdateDigest<>(token);
        digest.setAction("update");
        digest.setTable("bill_amendment");
        digest.setFields(Map.of("full_text", "AN ACT to amend the law"));
        when(billUpdatesDao.getUpdates(any(), any(), any(), any(), any()))
                .thenAnswer(inv -> new PaginatedList<>(1, inv.getArgument(4), List.of(token)));
        when(billUpdatesDao.getDetailedUpdates(any(), any(), any(), any(), any()))
                .thenAnswer(inv -> new PaginatedList<>(1, inv.getArgument(4), List.of(digest)));
        when(billUpdatesDao.getDetailedUpdatesForBill(any(), any(), any(), any(), any(), any()))
                .thenAnswer(inv -> new PaginatedList<>(1, inv.getArgument(5), List.of(digest)));
        offMvc = mockMvc(policy -> new BillUpdatesCtrl(billUpdatesDao, billData, policy), "off");
        enforceMvc = mockMvc(policy -> new BillUpdatesCtrl(billUpdatesDao, billData, policy), "enforce");
    }

    @After
    public void tearDown() {
        unbindSubject();
    }

    /* --- Off mode --- */

    @Test
    public void offExpandsEveryTrueValue() throws Exception {
        for (String feed : FEEDS) {
            for (String value : PARSED_TRUE_VALUES) {
                ok(offMvc, feed, "fullBill", value);
                verify(billData).getBill(billId);
                verifyNoMoreInteractions(billData);
                clearInvocations(billData);
            }
        }
    }

    @Test
    public void offKeepsFullBillPrecedence() throws Exception {
        String feed = FEEDS.get(2);
        // fullBill overrides summary
        ok(offMvc, feed, "fullBill", "true", "summary", "true");
        verify(billData).getBill(billId);
        verifyNoMoreInteractions(billData);
        clearInvocations(billData);
        // fullBill is ignored for detailed updates
        assertEquals(ok(offMvc, feed, "detail", "true"), ok(offMvc, feed, "detail", "true", "fullBill", "true"));
        assertEquals(ok(offMvc, feed, "detail", "true", "summary", "true"),
                ok(offMvc, feed, "detail", "true", "summary", "true", "fullBill", "true"));
        verify(billData, never()).getBill(any(BaseBillId.class));
    }

    @Test
    public void offIgnoresFalseValues() throws Exception {
        for (String value : PARSED_FALSE_VALUES) {
            assertEquals(ok(offMvc, FEEDS.get(2)), ok(offMvc, FEEDS.get(2), "fullBill", value));
        }
        verifyNoInteractions(billData);
    }

    /* --- Enforce mode --- */

    @Test
    public void enforceRejectsTrueValuesWithoutWork() throws Exception {
        for (String feed : FEEDS) {
            for (String value : PARSED_TRUE_VALUES) {
                assertRejected(feed, decoded(value), "fullBill", value);
                assertRejected(feed, decoded(value), "fullBill", value, "summary", "true");
                assertRejected(feed, decoded(value), "fullBill", value, "detail", "true");
                assertRejected(feed, decoded(value), "fullBill", value, "detail", "true", "summary", "true");
                assertRejected(feed, decoded(value), "fullBill", value, "fullTextFormat", "html");
            }
        }
        verifyNoInteractions(billUpdatesDao, billData);
    }

    @Test
    public void enforceRejectsBeforeOtherValidation() throws Exception {
        assertRejected("/api/3/bills/updates/not-a-date", "true", "fullBill", "true");
        assertRejected("/api/3/bills/updates/" + FROM + "/not-a-date", "true", "fullBill", "true");
        assertRejected("/api/3/bills/updates/" + TO + "/" + FROM, "true", "fullBill", "true");
        for (String feed : FEEDS) {
            assertRejected(feed, "true", "fullBill", "true", "limit", "-1");
            assertRejected(feed, "true", "fullBill", "true", "filter", "bogus");
            assertRejected(feed, "true", "fullBill", "true", "fullTextFormat", "bogus");
        }
        verifyNoInteractions(billUpdatesDao, billData);
    }

    @Test
    public void enforceRejectsCallerWithEveryRole() throws Exception {
        bindAllRolesSubject();
        for (String feed : FEEDS) {
            assertRejected(feed, "true", "fullBill", "true");
        }
        verifyNoInteractions(billUpdatesDao, billData);
    }

    @Test
    public void enforceKeepsRequestsWithoutTrueFullBill() throws Exception {
        for (String feed : FEEDS) {
            assertSameInBothModes(feed);
            assertSameInBothModes(feed, "summary", "true");
            assertSameInBothModes(feed, "fullTextFormat", "html");
            assertSameInBothModes(feed, "detail", "true");
            assertSameInBothModes(feed, "detail", "true", "summary", "true");
            for (BillUpdateField field : BillUpdateField.values()) {
                assertSameInBothModes(feed, "detail", "true", "filter", field.name().toLowerCase());
            }
            for (String value : PARSED_FALSE_VALUES) {
                assertSameInBothModes(feed, "fullBill", value);
                assertSameInBothModes(feed, "fullBill", value, "summary", "true");
            }
        }
        verify(billData, never()).getBill(any(BaseBillId.class));
    }

    @Test
    public void enforceKeepsDigestText() throws Exception {
        JsonNode response = ok(enforceMvc, FEEDS.get(2), "detail", "true", "filter", "fulltext");
        assertEquals("AN ACT to amend the law", response.at("/result/items/0/fields/Full Text").asText());
        verify(billUpdatesDao).getDetailedUpdates(any(Range.class), eq(UpdateType.PROCESSED_DATE),
                eq(BillUpdateField.FULLTEXT), eq(SortOrder.ASC), any(LimitOffset.class));
    }

    @Test
    public void enforceKeepsBillDigestRoutes() throws Exception {
        for (String path : List.of(BILL_DIGESTS, BILL_DIGESTS + "/" + FROM, BILL_DIGESTS + "/" + FROM + "/" + TO)) {
            assertSameInBothModes(path, "fullBill", "true");
            assertEquals(ok(enforceMvc, path), ok(enforceMvc, path, "fullBill", "true"));
        }
        verify(billUpdatesDao, atLeastOnce()).getDetailedUpdatesForBill(eq(billId), any(), any(), isNull(), any(), any());
        verifyNoInteractions(billData);
    }

    /* --- Internal --- */

    private void assertRejected(String path, String receivedValue, String... params) throws Exception {
        MvcResult result = perform(enforceMvc, path, params);
        assertEquals(path + query(params), 400, result.getResponse().getStatus());
        assertEquals(path + query(params), expectedRejection(BILL_UPDATES_FULL_BILL, receivedValue), json(result));
    }

    private void assertSameInBothModes(String path, String... params) throws Exception {
        assertEquals(path + query(params), ok(offMvc, path, params), ok(enforceMvc, path, params));
    }

    /** @return the response body, without its date range, which can follow the current time */
    private static JsonNode ok(MockMvc mvc, String path, String... params) throws Exception {
        MvcResult result = perform(mvc, path, params);
        assertEquals(path + query(params), 200, result.getResponse().getStatus());
        ObjectNode body = (ObjectNode) json(result);
        body.remove(List.of("fromDateTime", "toDateTime"));
        return body;
    }
}
