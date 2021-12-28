package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.spotchecks.alert.agenda.dao.AgendaAlertDao;
import gov.nysenate.openleg.spotchecks.base.SpotcheckMailProcessService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaSpotcheckProcessService extends SpotcheckMailProcessService {
    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotcheckProcessService.class);

    @Autowired
    private AgendaAlertCheckMailService alertCheckMailService;

    @Autowired
    private CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;

    @Autowired
    private AgendaAlertProcessor agendaAlertProcessor;

    @Autowired
    private AgendaAlertDao agendaAlertDao;

    /** --- Implemented Methods --- */

    // TODO: This method takes minutes to run.
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
