package gov.nysenate.openleg.service.sobi;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.sobi.LegDataProcessService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ManagedLegDataProcessServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedLegDataProcessServiceTest.class);

    @Autowired
    private LegDataProcessService legDataProcessService;

    @Test
    public void randomTest() throws Exception {

    }

    @Test
    public void testCollateSobiFiles() throws Exception {
        int collated = legDataProcessService.collateSourceFiles();
        logger.info("Collated {} sobis", collated);
    }

    @Test
    public void testGetPendingFragments() throws Exception {

    }

    @Test
    public void testProcessFragments() throws Exception {

    }

    @Test
    public void testProcessPendingFragments() throws Exception {
        SobiProcessOptions options = SobiProcessOptions.builder().build();
        legDataProcessService.processPendingFragments(options);
    }

    @Test
    public void testUpdatePendingProcessing() throws Exception {

    }
}
