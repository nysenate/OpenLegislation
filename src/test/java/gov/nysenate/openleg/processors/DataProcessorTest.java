package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataProcessorTest extends BaseTests {
    @Autowired
    protected DataProcessor dataProcessor;

    @Test
    public void completeTest() throws Exception {
        /** TODO update for Spring */
        dataProcessor.collateAll();
        dataProcessor.ingestAll();
    }
}
