package gov.nysenate.openleg.service.spotcheck.daybreak;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class DaybreakCheckMailServiceTest extends BaseTests {

    @Autowired private DaybreakCheckMailService daybreakCheckMailService;

    @Test
    public void checkMail() throws Exception {
        daybreakCheckMailService.checkMail();
    }

}