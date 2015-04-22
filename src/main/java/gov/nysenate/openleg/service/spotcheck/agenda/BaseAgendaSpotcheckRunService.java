package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.agenda.reference.AgendaAlertProcessor;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckRunService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

public abstract class BaseAgendaSpotcheckRunService extends BaseSpotcheckRunService<CommitteeAgendaAddendumId> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgendaSpotcheckRunService.class);

    @Autowired
    private AgendaAlertCheckMailService alertCheckMailService;

    @Autowired
    private CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;

    @Autowired
    private AgendaAlertProcessor agendaAlertProcessor;

    /** --- Implemented Methods --- */

    @Override
    public List<SpotCheckReport<CommitteeAgendaAddendumId>> doGenerateReports() {
        try {
            return Collections.singletonList(generateReport());
        } catch (ReferenceDataNotFoundEx ex) {
            logger.info("No reports generated: {}", ex.getMessage());
        }
        return Collections.emptyList();
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

    /** --- Internal Methods --- */

    protected abstract SpotCheckReport<CommitteeAgendaAddendumId> generateReport() throws ReferenceDataNotFoundEx;
}
