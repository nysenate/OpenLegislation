package gov.nysenate.openleg.controller.api;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import gov.nysenate.openleg.model.search.SearchException;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.WebRequest;

import java.util.EnumSet;

public abstract class ApiTest extends BaseTests {
    private static EnumSet<SearchIndex> rebuiltIndices = EnumSet.noneOf(SearchIndex.class);
    protected WebRequest testRequest = Mockito.mock(WebRequest.class);
    @Autowired
    private EventBus eventBus;

    /**
     * Forces subclasses to have an associated SearchIndex.
     * @return The relevent SearchIndex.
     */
    protected abstract SearchIndex getIndex();

    protected abstract int allItemsInIndex() throws SearchException;

    @Before
    public void setup() throws InterruptedException, SearchException {
        if (!rebuiltIndices.contains(getIndex())) {
            // Data should only be loaded once for all relevant indices.
            eventBus.post(new RebuildIndexEvent(EnumSet.of(getIndex())));
            int currItemsInIndex = 0;
            // While an index is rebuilding, the number of things in it will be changing.
            while (currItemsInIndex == 0 || currItemsInIndex != allItemsInIndex()) {
                currItemsInIndex = allItemsInIndex();
                Thread.sleep(100);
            }
            rebuiltIndices.add(getIndex());
        }
        testRequest = Mockito.mock(WebRequest.class);
        Mockito.when(testRequest.getParameter(Mockito.anyString())).thenReturn(null);
    }

    protected void addParam(String key, String value) {
        Mockito.when(testRequest.getParameter(key)).thenReturn(value);
    }
}
 
