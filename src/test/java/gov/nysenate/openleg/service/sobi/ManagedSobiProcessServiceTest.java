package gov.nysenate.openleg.service.sobi;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.base.SobiProcessService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TreeSet;

public class ManagedSobiProcessServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedSobiProcessServiceTest.class);

    @Autowired
    private SobiProcessService sobiProcessService;

    @Test
    public void randomTest() throws Exception {

    }

    @Test
    public void testCollateSobiFiles() throws Exception {
        sobiProcessService.collateSobiFiles();
    }

    @Test
    public void testGetPendingFragments() throws Exception {

    }

    @Test
    public void testProcessFragments() throws Exception {

    }

    @Test
    public void testProcessPendingFragments() throws Exception {
        SobiProcessOptions options = SobiProcessOptions.builder()
            .setAllowedFragmentTypes(ImmutableSet.of(
                    SobiFragmentType.BILL
            ))
            .build();
        sobiProcessService.processPendingFragments(options);
    }

    @Test
    public void testUpdatePendingProcessing() throws Exception {

    }
}
