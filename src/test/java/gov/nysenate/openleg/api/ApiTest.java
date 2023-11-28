package gov.nysenate.openleg.api;

import gov.nysenate.openleg.BaseTests;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.web.context.request.WebRequest;

public abstract class ApiTest extends BaseTests {
    protected WebRequest testRequest = Mockito.mock(WebRequest.class);

    @Before
    public void setup() {
        testRequest = Mockito.mock(WebRequest.class);
        Mockito.when(testRequest.getParameter("limit")).thenReturn("500");
    }

    protected void addParam(String key, String value) {
        Mockito.when(testRequest.getParameter(key)).thenReturn(value);
    }
}
