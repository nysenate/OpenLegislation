package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.service.law.data.LawDataService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class LawProcessorTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(LawProcessorTests.class);

    @Autowired
    private LawDataService lawDataService;

    @Test
    public void testProcess() throws Exception {
        LawTree lawTree = lawDataService.getLawTree("ABC", LocalDate.now());
        logger.info("{}", lawTree);
    }
}
