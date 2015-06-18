package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.dao.calendar.alert.CalendarAlertDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.processor.base.ParseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CalendarAlertProcessor extends BaseCalendarAlertParser {

    private static final Logger logger = LoggerFactory.getLogger(CalendarAlertProcessor.class);

    @Autowired
    private CalendarAlertSupplementalParser supplementalParser;

    @Autowired
    private CalendarAlertActiveListParser activeListParser;

    @Autowired
    private CalendarAlertDao calendarAlertDao;

    /**
     * Parses a Calendar object from an LRS alert email file.
     *
     * @param file
     * @return
     */
    public Calendar process(File file) throws ParseError {
        try {
            CalendarId calendarId = parseCalendarId(file);
            Calendar calendar;
            try {
                calendar = calendarAlertDao.getCalendar(calendarId);
            } catch (EmptyResultDataAccessException e) {
                calendar = new Calendar(calendarId);
            }
            calendar.setPublishedDateTime(parseReleaseDateTime(file));
            calendar.setModifiedDateTime(LocalDateTime.now());

            if (isSupplemental(file)) {
                calendar.putSupplemental(supplementalParser.parseSupplemental(calendarId, file));
            } else if (isActiveList(file)) {
                calendar.putActiveList(activeListParser.parseActiveList(calendar, file));
            }
            return calendar;
        } catch (Exception ex) {
            throw new ParseError("Error while parsing calendar alert file " + file.getName(), ex);
        }
    }

    private CalendarId parseCalendarId(File file) {
        int year = Integer.valueOf(splitFileName(file)[1]);
        String calNo = parseCalNo(file);
        return new CalendarId(Integer.valueOf(calNo), year);
    }

    private boolean isSupplemental(File file) {
        return file.getName().contains("floor_cal");
    }

    private boolean isActiveList(File file) {
        return file.getName().contains("active_list");
    }
}
