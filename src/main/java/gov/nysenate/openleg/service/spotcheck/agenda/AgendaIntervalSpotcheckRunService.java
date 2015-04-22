package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

@Service
public class AgendaIntervalSpotcheckRunService extends BaseAgendaSpotcheckRunService {

    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotcheckRunService.class);

    @Autowired
    AgendaIntervalCheckReportService reportService;

    @Autowired
    Environment environment;

    private Duration checkRange = Duration.ofDays(7);

    @Scheduled(cron = "${scheduler.spotcheck.agenda.cron}")
    public void runScheduledAgendaSpotcheck() {
        if (environment.isSpotcheckScheduled()) {
            runSpotcheck();
        }
    }

    @Override
    protected SpotCheckReport<CommitteeAgendaAddendumId> generateReport() throws ReferenceDataNotFoundEx {
        logger.info("attempting to run an interval based agenda spotcheck report");
        // Find unchecked references from the past week
        LocalDateTime endDateTime = LocalDateTime.now();
        SpotCheckReport<CommitteeAgendaAddendumId> report =
                reportService.generateReport(endDateTime.minus(checkRange), endDateTime);
        logger.info("saving agenda reports..");
        reportService.saveReport(report);
        return report;
    }
}
