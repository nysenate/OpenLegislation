package gov.nysenate.openleg.service.spotcheck.agenda;

import com.google.common.collect.Range;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

public abstract class BaseAgendaIntervalSpotcheckRunService extends BaseAgendaSpotcheckRunService {

    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotcheckRunService.class);

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
    protected Range<LocalDateTime> getCheckRange() {
        LocalDateTime now = LocalDateTime.now();
        return Range.closed(now.minus(checkRange), now);
    }
}
