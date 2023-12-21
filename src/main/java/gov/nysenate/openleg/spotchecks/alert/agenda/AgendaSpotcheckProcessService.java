package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.spotchecks.alert.agenda.dao.AgendaAlertDao;
import gov.nysenate.openleg.spotchecks.base.SpotcheckMailProcessService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaSpotcheckProcessService extends SpotcheckMailProcessService {
    private final AgendaAlertCheckMailService alertCheckMailService;
    private final CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;
    private final AgendaAlertProcessor agendaAlertProcessor;
    private final AgendaAlertDao agendaAlertDao;

    @Autowired
    public AgendaSpotcheckProcessService(AgendaAlertCheckMailService alertCheckMailService,
                                         CommAgendaAlertCheckMailService commAgendaAlertCheckMailService,
                                         AgendaAlertProcessor agendaAlertProcessor,
                                         AgendaAlertDao agendaAlertDao) {
        this.alertCheckMailService = alertCheckMailService;
        this.commAgendaAlertCheckMailService = commAgendaAlertCheckMailService;
        this.agendaAlertProcessor = agendaAlertProcessor;
        this.agendaAlertDao = agendaAlertDao;
    }

    /** --- Implemented Methods --- */

    // TODO: Can combine these Patterns.
    @Override
    protected int doCollate() {
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
