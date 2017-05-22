package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.WebAppBaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.controller.api.admin.SpotCheckCtrl;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class SpotcheckTest extends WebAppBaseTests {
    private static final Logger logger = LoggerFactory.getLogger(SpotcheckTest.class);

    @Autowired
    SpotcheckRunService spotcheckRunService;

    @Autowired SpotCheckCtrl spotCheckCtrl;

    @Test
    public void runReports()
    {
        spotcheckRunService.runReports(SpotCheckRefType.SENATE_SITE_CALENDAR);
    }

    @Test
    public void openObsGetTest() {
//        LocalTime start = LocalTime.now();
//        logger.info("start {}", start);
//        BaseResponse response =
//                spotCheckCtrl.getOpenMismatches("scraped-bill", null, "CONTENT_KEY", null, false, false, false, false, true,
//                        new ServletWebRequest(new MockHttpServletRequest()));
//        LocalTime end = LocalTime.now();
//        logger.info("done {}", end);
//        logger.info("took {}", Duration.between(start, end));
    }
}
