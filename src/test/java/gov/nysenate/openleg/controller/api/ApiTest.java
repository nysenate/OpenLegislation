package gov.nysenate.openleg.controller.api;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.web.context.request.WebRequest;

public abstract class ApiTest extends BaseTests {
    protected WebRequest testRequest = Mockito.mock(WebRequest.class);
    private static boolean dataLoaded = false;

    protected abstract IndexedSearchService<?> getIndex();

    @Before
    public void setup() throws InterruptedException {
        if (!dataLoaded) {
            getIndex().rebuildIndex();
            Thread.sleep(500);
            dataLoaded = true;
        }
        testRequest = Mockito.mock(WebRequest.class);
        Mockito.when(testRequest.getParameter(Mockito.anyString())).thenReturn(null);
    }

    protected void addParam(String key, String value) {
        Mockito.when(testRequest.getParameter(key)).thenReturn(value);
    }
}
 
