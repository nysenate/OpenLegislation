package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LegDataProcessServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(LegDataProcessServiceTest.class);

    @Autowired
    private LegDataProcessService legDataProcessService;

    @Test
    public void ingestTest() {
            legDataProcessService.ingest();
    }

    @Test
    public void fullTest() {
            legDataProcessService.collate();
            legDataProcessService.ingest();
    }


}
