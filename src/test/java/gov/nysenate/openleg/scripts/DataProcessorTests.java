package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.sobi.SobiFileDao;
import gov.nysenate.openleg.processors.DataProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataProcessorTests extends BaseTests
{
    @Autowired
    protected DataProcessor dataProcessor;

    @Autowired
    protected SobiFileDao sobiFileDao;

    @Test
    public void completeTest() throws Exception {
        /** TODO update for Spring */
        //sobiFileDao.deleteAll();
        dataProcessor.stage(null, null);
        dataProcessor.collate();
        dataProcessor.ingest();
    }

    @Test
    public void testSomething() throws Exception {
        String s = "";
        System.out.print(s.compareTo("A"));
    }
}
