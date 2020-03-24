package gov.nysenate.openleg.controller.api;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.WebRequest;

import java.util.EnumSet;

public abstract class ApiTest extends BaseTests {
    protected static EnumSet<SearchIndex> indicesToTest = EnumSet.noneOf(SearchIndex.class);
    private static boolean dataLoaded;
    protected WebRequest testRequest = Mockito.mock(WebRequest.class);
    @Autowired
    private EventBus eventBus;

    @Before
    public void setup() throws InterruptedException {
        if (!dataLoaded) {
            eventBus.post(new RebuildIndexEvent(indicesToTest));
            Thread.sleep(1000);
            dataLoaded = true;
        }
        testRequest = Mockito.mock(WebRequest.class);
        Mockito.when(testRequest.getParameter(Mockito.anyString())).thenReturn(null);
    }

    protected void addParam(String key, String value) {
        Mockito.when(testRequest.getParameter(key)).thenReturn(value);
    }
}
 
