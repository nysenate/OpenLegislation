package gov.nysenate.openleg.service.spotcheck.base;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckAbortException;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.scraping.LrsOutageScrapingEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static gov.nysenate.openleg.model.notification.NotificationType.LRS_OUTAGE;

@Service
public class SpotCheckNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckNotificationService.class);

    private static final String spotcheckReportPath = "/admin/report/spotcheck";

    private final EventBus eventBus;
    private final Environment env;

    @Autowired
    public SpotCheckNotificationService(EventBus eventBus, Environment env) {
        this.eventBus = eventBus;
        this.env = env;
    }

    /**
     * Sends out a notification reporting the given spotcheck exception
     *
     * @param ex Exception - an exception that was raised during a spotcheck action
     * @param duringReport - true if the exception occurred during a report run
     */
    public void handleSpotcheckException(Exception ex, boolean duringReport) {
        if (!(ex instanceof SpotCheckAbortException)) {
            logger.error("Spotcheck Error:\n{}", ExceptionUtils.getStackTrace(ex));
            eventBus.post(new Notification(
                    NotificationType.SPOTCHECK_EXCEPTION,
                    LocalDateTime.now(),
                    "Spotcheck Error: " + ExceptionUtils.getStackFrames(ex)[0],
                    "An error occurred while " +
                    (duringReport ? "running a spotcheck report" : "processing spotcheck data") +
                    " at " + LocalDateTime.now() + ":\n" + ExceptionUtils.getStackTrace(ex)
            ));
        }
    }

    /**
     * Generates and sends a notification for a new spotcheck report
     *
     * @param report SpotCheckReport<
     */
    public void spotcheckCompleteNotification(SpotCheckReport<?> report) {
        String summary = "New " + report.getReportId().getReferenceType() +
                " spotcheck report: " + report.getReportDateTime();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(summary)
                .append("\n")
                .append(getReportUrl(report))
                .append("\n")
                .append(StringUtils.isNotBlank(report.getNotes()) ? "Notes: " + report.getNotes() : "")
                .append("\n\n")
                .append("Total open errors: ")
                .append(report.getOpenMismatchCount(false))
                .append("\n");

        report.getMismatchStatusTypeCounts(false).forEach((status, typeCounts) -> {
            long totalTypeCounts = typeCounts.values().stream().reduce(0L, Long::sum);
            messageBuilder.append(status)
                    .append(": ")
                    .append(totalTypeCounts)
                    .append("\n");
            typeCounts.forEach((type, count) ->
                    messageBuilder.append("\t")
                            .append(type)
                            .append(": ")
                            .append(count)
                            .append("\n"));
        });

        long ignoredCount = report.getOpenMismatchCount(true);
        if (ignoredCount > 0) {
            messageBuilder.append("IGNORED: ")
                    .append(ignoredCount)
                    .append("\n");
        }

        NotificationType type = report.getReferenceType().getNotificationType();

        Notification notification =
                new Notification(type, report.getReportDateTime(), summary, messageBuilder.toString());

        eventBus.post(notification);
    }

    /* --- Scraping Exceptions --- */

    public void handleLrsOutageScrapingEx(LrsOutageScrapingEx ex) {
        Notification notification = new Notification(
                LRS_OUTAGE,
                LocalDateTime.now(),
                "LRS appears to be down",
                ex.getMessage()
        );
        eventBus.post(notification);
    }

    /* --- Internal Methods --- */

    private String getReportUrl(SpotCheckReport<?> report) {
        return UriComponentsBuilder.fromHttpUrl(env.getUrl() + spotcheckReportPath)
                .queryParam("date", report.getReportDateTime().toLocalDate())
                .queryParam("source", report.getReferenceType().getDataSource())
                .queryParam("content", report.getReferenceType().getContentType())
                .toUriString();
    }
}
