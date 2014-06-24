package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.sobi.SqlSOBIFileDao;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.util.Application;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Test;

public class DataProcessorTests extends BaseTests
{
    @Test
    public void completeTest() throws Exception {
        DataProcessor dataProcessor = new DataProcessor(Application.getEnvironment());
        SqlSOBIFileDao sobiFileDao = new SqlSOBIFileDao(Application.getEnvironment());
        //sobiFileDao.deleteAll();
        dataProcessor.stage(null, null);
        dataProcessor.collate(null);
        dataProcessor.ingest(null, null);
    }

    @Test
    public void testSomething() throws Exception {
        String s = "";
        System.out.print(s.compareTo("A"));
    }
}
