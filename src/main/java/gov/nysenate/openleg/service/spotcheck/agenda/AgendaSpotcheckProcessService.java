package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.processor.agenda.reference.AgendaAlertProcessor;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaSpotcheckProcessService extends BaseSpotcheckProcessService<CommitteeAgendaAddendumId>
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotcheckProcessService.class);

    @Autowired
    private AgendaAlertCheckMailService alertCheckMailService;

    @Autowired
    private CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;

    @Autowired
    private AgendaAlertProcessor agendaAlertProcessor;

    /** --- Implemented Methods --- */

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
    public String getCollateType() {
        return "agenda alert";
    }
}
