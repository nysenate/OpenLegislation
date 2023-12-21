package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.spotchecks.alert.agenda.dao.AgendaAlertDao;
import gov.nysenate.openleg.spotchecks.base.SpotCheckNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AgendaAlertProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AgendaAlertProcessor.class);
    private final AgendaAlertDao agendaAlertDao;
    private final SpotCheckNotificationService notificationService;

    public AgendaAlertProcessor(AgendaAlertDao agendaAlertDao, SpotCheckNotificationService notificationService) {
        this.agendaAlertDao = agendaAlertDao;
        this.notificationService = notificationService;
    }

    public int processAgendaAlerts() throws ParseError, IOException {
        int processedCount = 0;
        for (File alertFile : agendaAlertDao.getIncomingAgendaAlerts()) {
            logger.info("processing agenda alert {}", alertFile.getName());
            try {
                List<AgendaAlertInfoCommittee> references = AgendaAlertParser.parseAgendaAlert(alertFile);
                references.forEach(agendaAlertDao::updateAgendaAlertInfoCommittee);
                processedCount++;

                // Archive file
                logger.info("archiving agenda alert {}", alertFile.getName());
                agendaAlertDao.archiveAgendaAlert(alertFile);
            } catch (Exception ex) {
                notificationService.handleSpotcheckException(ex, false);
            }
        }
        return processedCount;
    }
}
