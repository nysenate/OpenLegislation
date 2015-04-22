package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.controller.api.admin.SpotCheckCtrl;
import gov.nysenate.openleg.service.spotcheck.agenda.AgendaIntervalSpotcheckRunService;
import gov.nysenate.openleg.service.spotcheck.agenda.AgendaSpotcheckRunService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AgendaSpotcheckTests extends BaseTests {

    @Autowired
    AgendaSpotcheckRunService spotcheckRunService;

    @Autowired
    AgendaIntervalSpotcheckRunService intervalSpotcheckRunService;

    @Autowired
    SpotCheckCtrl spotCheckCtrl;

    @Test
    public void agendaSpotcheckRunTest() {
        spotcheckRunService.runSpotcheck();
    }

    @Test
    public void agendaIntervalSpotcheckTest() {
        intervalSpotcheckRunService.runSpotcheck();
    }

}
