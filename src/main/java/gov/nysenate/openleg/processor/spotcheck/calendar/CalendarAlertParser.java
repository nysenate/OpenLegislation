package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.model.calendar.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CalendarAlertParser extends BaseCalendarAlertParser {

    private static final Logger logger = LoggerFactory.getLogger(CalendarAlertParser.class);

    @Autowired
    private CalendarAlertSupplementalParser supplementalParser;

    @Autowired
    private CalendarAlertActiveListParser activeListParser;

    /**
     * Parses a Calendar object from an LRS alert email file.
     *
     * @param file
     * @return
     */
    public Calendar parse(File file) {
        Calendar calendar = new Calendar(parseCalendarId(file));

        if (isSupplemental(file)) {
            try {
                calendar.putSupplemental(supplementalParser.parseSupplemental(file));
            } catch (IOException e) {
                logger.error("Error parsing calendar alert supplemental file: " + file.getName(), e);
            }
        } else if (isActiveList(file)) {
            try {
                calendar.putActiveList(activeListParser.parseActiveList(file));
            } catch (IOException e) {
                logger.error("Error parsing calendar alert active list file: " + file.getName(), e);
            }
        }
        return calendar;
    }

    private boolean isSupplemental(File file) {
        return file.getName().contains("floor_cal");
    }

    private boolean isActiveList(File file) {
        return file.getName().contains("active_list");
    }
}
