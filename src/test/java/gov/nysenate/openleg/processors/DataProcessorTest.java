package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.processors.DataProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataProcessorTest extends BaseTests
{
    @Autowired
    protected DataProcessor dataProcessor;

//    @Autowired
//    protected SobiFileDao sobiFileDao;

    @Test
    public void completeTest() throws Exception {
        /** TODO update for Spring */
        dataProcessor.collate();
        dataProcessor.ingest();
    }

    @Test
    public void testSomething() throws Exception {
        String s = "";
        System.out.print(s.compareTo("A"));
    }
}
