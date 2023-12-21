package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.spotchecks.alert.calendar.dao.CalendarAlertDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CalendarAlertProcessor extends BaseCalendarAlertParser {
    private final CalendarAlertSupplementalParser supplementalParser;
    private final CalendarAlertActiveListParser activeListParser;
    private final CalendarAlertDao calendarAlertDao;

    @Autowired
    public CalendarAlertProcessor(CalendarAlertSupplementalParser supplementalParser,
                                  CalendarAlertActiveListParser activeListParser,
                                  CalendarAlertDao calendarAlertDao) {
        this.supplementalParser = supplementalParser;
        this.activeListParser = activeListParser;
        this.calendarAlertDao = calendarAlertDao;
    }

    /**
     * Parses a Calendar object from an LRS alert email file.
     * @param calFile to pull data from.
     * @return the aforementioned calendar.
     */
    public Calendar process(CalendarAlertFile calFile) throws ParseError {
        CalendarId calendarId = calFile.getCalendarId();
        Calendar calendar;
        try {
            calendar = calendarAlertDao.getCalendar(calendarId);
        } catch (EmptyResultDataAccessException e) {
            calendar = new Calendar(calendarId);
        }
        calendar.setPublishedDateTime(calFile.getPublishedDateTime());
        calendar.setModifiedDateTime(LocalDateTime.now());

        try {
            if (calFile.isFloorSupplemental()) {
                calendar.putSupplemental(supplementalParser.parseSupplemental(calFile));
            } else if (calFile.isActiveList()) {
                calendar.putActiveList(activeListParser.parseActiveList(calFile));
            }
            return calendar;
        } catch (Exception ex) {
            throw new ParseError("Error while parsing calendar alert file " + calFile.getFile().getName(), ex);
        }
    }
}
