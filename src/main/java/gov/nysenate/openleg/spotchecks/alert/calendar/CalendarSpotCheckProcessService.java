package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlCalendarAlertDao;
import gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlFsCalendarAlertFileDao;
import gov.nysenate.openleg.spotchecks.base.SpotCheckNotificationService;
import gov.nysenate.openleg.spotchecks.base.SpotcheckMailProcessService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarSpotCheckProcessService extends SpotcheckMailProcessService {
    private final Logger logger = LoggerFactory.getLogger(CalendarSpotCheckProcessService.class);
    private final ActiveListAlertCheckMailService activeListMailService;
    private final FloorCalAlertCheckMailService supplementalMailService;
    private final SqlFsCalendarAlertFileDao fileDao;
    private final SqlCalendarAlertDao calendarAlertDao;
    private final CalendarAlertProcessor processor;
    private final SpotCheckNotificationService notificationService;

    @Autowired
    public CalendarSpotCheckProcessService(ActiveListAlertCheckMailService activeListMailService,
                                           FloorCalAlertCheckMailService supplementalMailService,
                                           SqlFsCalendarAlertFileDao fileDao,
                                           SqlCalendarAlertDao calendarAlertDao,
                                           CalendarAlertProcessor processor,
                                           SpotCheckNotificationService notificationService) {
        this.activeListMailService = activeListMailService;
        this.supplementalMailService = supplementalMailService;
        this.fileDao = fileDao;
        this.calendarAlertDao = calendarAlertDao;
        this.processor = processor;
        this.notificationService = notificationService;
    }

    @Override
    protected int doCollate() throws Exception {
        int newAlerts = activeListMailService.checkMail() + supplementalMailService.checkMail();
        List<CalendarAlertFile> incomingFiles = fileDao.getIncomingCalendarAlerts();
        for (CalendarAlertFile file : incomingFiles) {
            logger.info("archiving file " + file.getFile().getName());
            fileDao.updateCalendarAlertFile(file);
            file = fileDao.archiveCalendarAlertFile(file);
            fileDao.updateCalendarAlertFile(file);
        }
        return newAlerts;
    }

    @Override
    protected int doIngest() {
        int processedCount = 0;
        List<CalendarAlertFile> files = fileDao.getPendingCalendarAlertFiles(LimitOffset.THOUSAND);
        if (!files.isEmpty())
            logger.info("Processing " + files.size() + " files.");
        for (CalendarAlertFile file : files) {
            try {
                logger.info("Processing calendar from file: " + file.getFile().getName());
                Calendar calendar = processor.process(file);
                calendarAlertDao.updateCalendar(calendar, file);
                // Set as unchecked so this new calendar data gets checked in the CalendarReportService.
                calendarAlertDao.updateChecked(calendar.getId(), false);
                processedCount++;
            } catch (Exception ex) {
                notificationService.handleSpotcheckException(ex, false);
            } finally {
                updateCalendarFile(file);
            }
        }
        return processedCount;
    }

    private void updateCalendarFile(CalendarAlertFile file) {
        file.setProcessedCount(file.getProcessedCount() + 1);
        file.setProcessedDateTime(LocalDateTime.now());
        file.setPendingProcessing(false);
        fileDao.updateCalendarAlertFile(file);
    }

    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_CALENDAR_ALERT;
    }

    @Override
    protected int getUncheckedRefCount() {
        return calendarAlertDao.getUnCheckedCalendarAlerts().size() +
                calendarAlertDao.getProdUnCheckedCalendarAlerts().size();
    }

    @Override
    public String getCollateType() {
        return "calendar alert";
    }
}
