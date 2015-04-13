package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.agenda.reference.AgendaAlertProcessor;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaSpotcheckRunService extends BaseSpotcheckRunService<AgendaId> {

    @Autowired
    private AgendaAlertCheckMailService alertCheckMailService;

    @Autowired
    private CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;

    @Autowired
    private AgendaAlertProcessor agendaAlertProcessor;

    @Override
    public List<SpotCheckReport<AgendaId>> doGenerateReports() {
        return null;
    }

    @Override
    protected int doCollate() throws Exception {
        int alertsDownloaded = alertCheckMailService.checkMail() + commAgendaAlertCheckMailService.checkMail();
        agendaAlertProcessor.processAgendaAlerts();
        return alertsDownloaded;
    }

    @Override
    public String getCollateType() {
        return "agenda alert";
    }
}
