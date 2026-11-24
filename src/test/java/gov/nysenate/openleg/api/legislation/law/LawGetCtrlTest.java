package gov.nysenate.openleg.api.legislation.law;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Request parameter handling of the repealed laws endpoint. */
public class LawGetCtrlTest {
    private static final String REPEALED = "/api/3/laws/repealed";

    private LawDataService lawData;
    private MockMvc mvc;

    @Before
    public void setUp() {
        lawData = mock(LawDataService.class);
        when(lawData.getRepealedLawDocs(any())).thenReturn(List.of());
        mvc = mockMvc(new LawGetCtrl(lawData));
    }

    /** The controller declares no media type, so a text/html client used to get a 500 for this 404. */
    @Test
    public void missingLawIsErrorResponseForEveryAccept() throws Exception {
        when(lawData.getLawTree("ZZZ", null)).thenThrow(new LawTreeNotFoundEx("ZZZ", null, "not found"));
        assertEquals(22, assertJsonForEveryAccept(mvc, "/api/3/laws/ZZZ", 404).get("errorCode").asInt());
    }

    @Test
    public void acceptsDates() throws Exception {
        ok(mvc, REPEALED, "fromDate", "2026-05-22", "toDate", "2026-05-23");
        ok(mvc, REPEALED, "fromDate", "2026-05-22");
        ok(mvc, REPEALED, "toDate", "2026-05-23");
        ok(mvc, REPEALED);
    }

    @Test
    public void sameFromAndToDateIsAccepted() throws Exception {
        ok(mvc, REPEALED, "fromDate", "2026-05-22", "toDate", "2026-05-22");
    }

    @Test
    public void emptyDateCountsAsOmitted() throws Exception {
        ok(mvc, REPEALED, "fromDate", "", "toDate", "");
    }

    @Test
    public void rejectsValuesThatAreNotDates() throws Exception {
        assertInvalid("fromDate", "fromDate", "2026-05-22T00:00:00");
        assertInvalid("toDate", "toDate", "2026-05-22T00:00:00");
        assertInvalid("fromDate", "fromDate", "garbage");
        assertInvalid("toDate", "toDate", "2026-13-01");
    }

    @Test
    public void rejectsFromDateAfterToDate() throws Exception {
        MvcResult result = perform(mvc, REPEALED, "fromDate", "2026-05-23", "toDate", "2026-05-22");
        assertEquals(400, result.getResponse().getStatus());
        assertEquals(1, json(result).get("errorCode").asInt());
    }

    @Test
    public void rejectsRetiredDateTimeParams() throws Exception {
        assertRetired("fromDateTime", "fromDateTime", "2015-01-01");
        assertRetired("toDateTime", "toDateTime", "2015-12-31");
        assertRetired("fromDateTime", "fromDateTime", "");
        assertRetired("toDateTime", "toDateTime", "2026-01-01T00:00:00");
    }

    @Test
    public void retiredParamWinsOverOtherProblems() throws Exception {
        assertRetired("fromDateTime", "fromDateTime", "2015-01-01", "fromDate", "garbage");
        assertRetired("toDateTime", "fromDate", "2026-05-23", "toDate", "2026-05-22", "toDateTime", "x");
    }

    @Test
    public void retiredParamDoesNotQueryLaws() throws Exception {
        perform(mvc, REPEALED, "fromDateTime", "2015-01-01");
        verifyNoInteractions(lawData);
    }

    private void assertInvalid(String expectedParam, String... params) throws Exception {
        JsonNode body = error(params);
        assertEquals(expectedParam, body.at("/errorData/parameterConstraint/name").asText());
    }

    private void assertRetired(String expectedParam, String... params) throws Exception {
        JsonNode body = error(params);
        assertEquals(expectedParam, body.at("/errorData/parameterConstraint/name").asText());
        String message = body.get("message").asText();
        assertTrue(message, message.contains("fromDate") && message.contains("toDate"));
        assertTrue(message, message.contains("exclusive"));
        assertFalse(message, message.contains("migration"));
    }

    private JsonNode error(String... params) throws Exception {
        MvcResult result = perform(mvc, REPEALED, params);
        String request = REPEALED + query(params);
        assertEquals(request, 400, result.getResponse().getStatus());
        JsonNode body = json(result);
        assertEquals(request, 1, body.get("errorCode").asInt());
        assertEquals(request, "invalid-parameter", body.get("errorDataType").asText());
        return body;
    }
}
