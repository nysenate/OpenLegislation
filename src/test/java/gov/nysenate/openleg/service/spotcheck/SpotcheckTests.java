package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.controller.api.admin.SpotCheckCtrl;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.service.spotcheck.agenda.AgendaSpotcheckProcessService;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SpotcheckTests extends BaseTests {

    @Autowired
    SpotcheckRunService spotcheckRunService;

    @Test
    public void runWeeklyReports() {
        spotcheckRunService.runWeeklyReports();
    }

    @Test
    public void runReports() {
        spotcheckRunService.runReports(SpotCheckRefType.LBDC_DAYBREAK);
    }

}
