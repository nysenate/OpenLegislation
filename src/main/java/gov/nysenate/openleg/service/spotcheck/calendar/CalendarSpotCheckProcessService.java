package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.dao.calendar.alert.SqlFsCalendarAlertFileDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertProcessor;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarSpotCheckProcessService extends BaseSpotcheckProcessService<CalendarId> {

    private Logger logger = LoggerFactory.getLogger(CalendarSpotCheckProcessService.class);

    @Autowired
    private ActiveListAlertCheckMailService activeListMailService;

    @Autowired
    private FloorCalAlertCheckMailService supplementalMailService;

    @Autowired
    private SqlFsCalendarAlertFileDao fileDao;

    @Autowired
    private SqlCalendarAlertDao calendarAlertDao;

    @Autowired
    private CalendarAlertProcessor processor;

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
    protected int doIngest() throws Exception {
        int processedCount = 0;
        List<CalendarAlertFile> files = fileDao.getPendingCalendarAlertFiles(LimitOffset.THOUSAND);
        logger.info("Processing " + files.size() + " files.");
        for (CalendarAlertFile file : files) {
            logger.info("Processing calendar from file: " + file.getFile().getName());
            Calendar calendar = processor.process(file.getFile());
            updateCalendarFile(file);
            calendarAlertDao.updateCalendar(calendar, file);
            processedCount++;
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
    public String getCollateType() {
        return "calendar alert";
    }
}
