package gov.nysenate.openleg.service.spotcheck.base;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckAbortException;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.base.ProcessService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public abstract class BaseSpotcheckProcessService<ContentId> implements ProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(BaseSpotcheckProcessService.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Environment env;

    /**
     * {@inheritDoc}
     */
    @Override
    public int collate() {
        try {
            return doCollate();
        } catch (Exception ex) {
            handleSpotcheckException(ex);
            return 0;
        }
    }

    /**
     * @see #collate()
     */
    protected abstract int doCollate() throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public int ingest() {
        try {
            return doIngest();
        } catch (Exception ex) {
            handleSpotcheckException(ex);
            return 0;
        }
    }

    /**
     * @see #ingest()
     */
    protected abstract int doIngest() throws Exception;


    /** --- Internal Methods --- */

    /**
     * Sends out a notification reporting the given spotcheck exception
     *
     * @param ex Exception - an exception that was raised during a spotcheck action
     */
    private void handleSpotcheckException(Exception ex) {
        if (!(ex instanceof SpotCheckAbortException)) {
            logger.error("Spotcheck Error:\n{}", ExceptionUtils.getStackTrace(ex));
            eventBus.post(new Notification(
                    NotificationType.SPOTCHECK_EXCEPTION,
                    LocalDateTime.now(),
                    "Spotcheck Error: " + ExceptionUtils.getStackFrames(ex)[0],
                    "An error occurred while running a spotcheck report at " + LocalDateTime.now() + ":\n" +
                    ExceptionUtils.getStackTrace(ex)
            ));
        }
    }

    /**
     * Generates and sends a notification for a new daybreak spotcheck report
     *
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
