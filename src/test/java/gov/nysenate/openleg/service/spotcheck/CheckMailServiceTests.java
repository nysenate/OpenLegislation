package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckMailServiceTests extends BaseTests{

    @Autowired
    private CheckMailService checkMailService;

    @Test
    public void checkMailTest() {
        checkMailService.checkMail();
    }
}
