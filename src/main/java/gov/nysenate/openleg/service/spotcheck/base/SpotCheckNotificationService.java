package gov.nysenate.openleg.service.spotcheck.base;

import com.google.common.collect.MapMaker;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckAbortException;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class SpotCheckNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckNotificationService.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Environment env;

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
     * Generates and sends a notification for a new daybreak spotcheck report
     *
     * @param daybreakReport SpotCheckReport<
     */
    public void spotcheckCompleteNotification(SpotCheckReport<?> daybreakReport) {
        String summary = "New " + daybreakReport.getReportId().getReferenceType() +
                " spotcheck report: " + daybreakReport.getReportDateTime();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(summary)
                .append("\n")
                .append(env.getUrl())
                .append("/admin/report/spotcheck/")
                .append(daybreakReport.getReferenceType().getRefName())
                .append("/")
                .append(daybreakReport.getReportDateTime())
                .append("\n")
                .append(StringUtils.isNotBlank(daybreakReport.getNotes()) ? "Notes: " + daybreakReport.getNotes() : "")
                .append("\n\n")
                .append("Total open errors: ")
                .append(daybreakReport.getOpenMismatchCount(false))
                .append("\n");

        daybreakReport.getMismatchStatusTypeCounts(false).forEach((status, typeCounts) -> {
            long totalTypeCounts = typeCounts.values().stream().reduce(0L, (a, b) -> a + b);
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

        long ignoredCount = daybreakReport.getOpenMismatchCount(true);
        if (ignoredCount > 0) {
            messageBuilder.append("IGNORED: ")
                    .append(ignoredCount)
                    .append("\n");
        }

        NotificationType type = daybreakReport.getOpenMismatchCount(false) > 0
                ? daybreakReport.getReferenceType().getNotificationType()
                : NotificationType.SPOTCHECK_ALL_CLEAR;

        Notification notification =
                new Notification(type, daybreakReport.getReportDateTime(), summary, messageBuilder.toString());

        eventBus.post(notification);
    }
}
