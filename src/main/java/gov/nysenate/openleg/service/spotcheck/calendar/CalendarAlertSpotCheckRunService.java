package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.dao.calendar.alert.SqlFsCalendarAlertFileDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertParser;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarAlertSpotCheckRunService extends BaseSpotcheckRunService<CalendarId> {

    @Autowired
    private ActiveListAlertCheckMailService activeListMailService;

    @Autowired
    private FloorCalAlertCheckMailService supplementalMailService;

    @Autowired
    private SqlFsCalendarAlertFileDao fileDao;

    @Autowired
    private SqlCalendarAlertDao calendarAlertDao;

    @Autowired
    private CalendarAlertParser parser;

    @Override
    protected List<SpotCheckReport<CalendarId>> doGenerateReports() throws Exception {
        return null;
    }

    @Override
    protected int doCollate() throws Exception {
        int newAlerts = activeListMailService.checkMail() + supplementalMailService.checkMail();
        archiveFiles();
        parseFiles();
        return newAlerts;
    }

    private void parseFiles() {
        List<CalendarAlertFile> files = fileDao.getPendingCalendarAlertFiles(LimitOffset.ALL);
        for (CalendarAlertFile file : files) {
            Calendar calendar = parser.parse(file.getFile());
            file.setProcessedCount(file.getProcessedCount() + 1);
            file.setProcessedDateTime(LocalDateTime.now());
            file.setPendingProcessing(false);
            fileDao.updateCalendarAlertFile(file);

            calendarAlertDao.updateCalendar(calendar, file);
        }
    }

    private void archiveFiles() throws IOException {
        List<CalendarAlertFile> incomingFiles = fileDao.getIncomingCalendarAlerts();
        for (CalendarAlertFile file : incomingFiles) {
            fileDao.updateCalendarAlertFile(file);
            file = fileDao.archiveCalendarAlertFile(file);
            fileDao.updateCalendarAlertFile(file);
        }
    }

    @Override
    public String getCollateType() {
        return "calendar alert";
    }
}
