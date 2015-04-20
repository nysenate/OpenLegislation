package gov.nysenate.openleg.service.spotcheck.base;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public abstract class BaseSpotcheckRunService<ContentId> implements SpotcheckRunService<ContentId>
{

    private static final Logger logger = LoggerFactory.getLogger(BaseSpotcheckRunService.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Environment env;

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReport<ContentId>> generateReports() {
        try {
            return doGenerateReports();
        } catch (Exception ex) {
            handleSpotcheckException(ex);
            return Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int collate() {
        try {
            return doCollate();
        } catch (Exception ex) {
            handleSpotcheckException(ex);
            return 0;
        }
    }

    /** --- Internal Methods --- */

    /**
     * @see #generateReports()
     */
    protected abstract List<SpotCheckReport<ContentId>> doGenerateReports() throws Exception;

    /**
     * @see #collate()
     */
    protected abstract int doCollate() throws Exception;

    /**
     * Sends out a notification reporting the given spotcheck exception
     * @param ex Exception - an exception that was raised during a spotcheck action
     */
    private void handleSpotcheckException(Exception ex) {
        logger.error("Spotcheck Error:\n{}", ExceptionUtils.getStackTrace(ex));
        eventBus.post(new Notification(
                NotificationType.SPOTCHECK_EXCEPTION,
                LocalDateTime.now(),
                "Spotcheck Error: " + ExceptionUtils.getStackFrames(ex)[0],
                "An error occurred while running a spotcheck report at " + LocalDateTime.now() + ":\n" +
                        ExceptionUtils.getStackTrace(ex)
        ));
    }

    /**
     * Generates and sends a notification for a new daybreak spotcheck report
     * @param daybreakReport SpotCheckReport<BaseBillId>
     */
    protected void spotcheckCompleteNotification(SpotCheckReport<ContentId> daybreakReport) {
        String summary = "New " + daybreakReport.getReportId().getReferenceType() +
                " spotcheck report: " + daybreakReport.getReportDateTime();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(summary)
                .append("\n\n");

        messageBuilder.append(env.getUrl())
                .append("/admin/report/spotcheck?type=")
                .append(daybreakReport.getReferenceType().getRefName())
                .append("&runTime")
                .append(daybreakReport.getReportDateTime())
                .append("\n\n");

        messageBuilder.append("Total open errors: ").append(daybreakReport.getOpenMismatchCount()).append("\n");

        daybreakReport.getMismatchStatusTypeCounts().forEach((status, typeCounts) -> {
            long totalTypeCounts = typeCounts.values().stream().reduce(0L, (a, b) -> a + b);
            messageBuilder.append(status).append(": ").append(totalTypeCounts).append("\n");
            typeCounts.forEach((type, count) ->
                    messageBuilder.append("\t").append(type).append(": ").append(count).append("\n"));
        });

        Notification notification = new Notification(NotificationType.SPOTCHECK, daybreakReport.getReportDateTime(),
                summary, messageBuilder.toString());

        eventBus.post(notification);
    }
}
