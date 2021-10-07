package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.spotchecks.alert.calendar.dao.CalendarAlertDao;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.processors.ParseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

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
     * @param calFile
     * @return
     */
    public Calendar process(CalendarAlertFile calFile) throws ParseError {
        try {
            CalendarId calendarId = calFile.getCalendarId();
            Calendar calendar;
            try {
                calendar = calendarAlertDao.getCalendar(calendarId);
            } catch (EmptyResultDataAccessException e) {
                calendar = new Calendar(calendarId);
            }
            calendar.setPublishedDateTime(calFile.getPublishedDateTime());
            calendar.setModifiedDateTime(LocalDateTime.now());

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
