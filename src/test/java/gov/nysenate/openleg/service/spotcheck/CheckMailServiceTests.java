package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.service.spotcheck.base.CheckMailService;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakCheckMailService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CheckMailServiceTests extends BaseTests{

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
