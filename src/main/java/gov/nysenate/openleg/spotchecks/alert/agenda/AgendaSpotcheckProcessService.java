package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.spotchecks.alert.agenda.dao.AgendaAlertDao;
import gov.nysenate.openleg.spotchecks.base.SpotcheckMailProcessService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaSpotcheckProcessService extends SpotcheckMailProcessService {

    @Autowired
    private AgendaAlertCheckMailService alertCheckMailService;

    @Autowired
    private CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;

    @Autowired
    private AgendaAlertProcessor agendaAlertProcessor;

    @Autowired
    private AgendaAlertDao agendaAlertDao;

    /** --- Implemented Methods --- */

    // TODO: Can combine these Patterns.
    @Override
    protected int doCollate() throws Exception {
        return alertCheckMailService.checkMail() + commAgendaAlertCheckMailService.checkMail();
    }

    @Override
    protected int doIngest() throws Exception {
        return agendaAlertProcessor.processAgendaAlerts();
    }

    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_AGENDA_ALERT;
    }

    @Override
    protected int getUncheckedRefCount() {
        return agendaAlertDao.getProdUncheckedAgendaAlertReferences().size() +
                agendaAlertDao.getUncheckedAgendaAlertReferences().size();
    }

    @Override
    public String getCollateType() {
        return "agenda alert";
    }
}
