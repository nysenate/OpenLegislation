package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.controller.api.admin.SpotCheckCtrl;
import gov.nysenate.openleg.service.spotcheck.agenda.AgendaSpotcheckProcessService;
import org.springframework.beans.factory.annotation.Autowired;

public class AgendaSpotcheckTests extends BaseTests {

    @Autowired
    AgendaSpotcheckProcessService spotcheckRunService;

    @Autowired
    SpotCheckCtrl spotCheckCtrl;

//    @Test
//    public void agendaSpotcheckRunTest() {
//        spotcheckRunService.runSpotcheck();
//    }
//
//    @Test
//    public void agendaIntervalSpotcheckTest() {
//        intervalSpotcheckRunService.runSpotcheck();
//    }
//
//    @Test
//    public void oldApiSpotcheckTest() {
//        oldApiSpotcheckService.runSpotcheck();
//    }

}
