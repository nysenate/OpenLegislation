package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.service.law.data.LawDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Category(SillyTest.class)
public class LawProcessorTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(LawProcessorTest.class);

    @Autowired
    private LawDataService lawDataService;

    @Test
    public void testProcess() throws Exception {
        LawTree lawTree = lawDataService.getLawTree("ABC", LocalDate.now());
        logger.info("{}", lawTree);
    }
}
