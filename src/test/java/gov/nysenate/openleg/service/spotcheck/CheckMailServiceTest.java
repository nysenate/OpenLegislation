package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.service.spotcheck.base.CheckMailService;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakCheckMailService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Category(SillyTest.class)
public class CheckMailServiceTest extends BaseTests{

    @Autowired
    private DaybreakCheckMailService daybreakCheckMailService;

    @Autowired private List<CheckMailService> checkMailServices;

    @Test
    public void checkMailTest() {
        daybreakCheckMailService.checkMail();
    }

    @Test
    public void allCheckMailTest() {
        checkMailServices.forEach(CheckMailService::checkMail);
    }
}
