package gov.nysenate.openleg.processor.hearing;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PublicHearingDateParser extends BasePublicHearingParser
{
    /** Matches time string e.g. 2:00 p.m. to 4:00 p.m. */
    private static Pattern TIME = Pattern.compile("(\\d+:\\d{2} [ap].m.) to \\d+:\\d{2} [ap].m.");

    private static Pattern START_TIME = Pattern.compile("\\d+:\\d{2} [ap].m.");

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private DateTimeFormatter dayOfWeekDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a");

    /**
     * Extract a LocalDateTime from the first page of a PublicHearing.
     * @param firstPage list of Strings containg the first page of text.
     * @return LocalDateTime representing the start time of this PublicHearing.
     */
    public LocalDateTime parse(List<String> firstPage) {
        String dateTime = getDateTimeString(firstPage);
        if (dateTime == null) {
            return null;
        }
        dateTime = removeEndTime(dateTime);
        dateTime = formatAmPm(dateTime);

        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    /**
     * Finds the Strings containing date time information
     * from a List of Strings. Concatenates these Strings into
     * a "<code>MMMM d, yyyy h:mm a</code>" formatted single String.
     * @param firstPage
     * @return A String containing date time information in a specific format.
     * e.g: January 15, 2014 9:30 a.m.
     */
    private String getDateTimeString(List<String> firstPage) {
        for (int i = 0; i < firstPage.size(); i++) {
            String line = formatLine(firstPage.get(i));
            if (matchesDateFormat(line)) {
                return line + " " + getTimeString(firstPage, i + 1);
            }
            if (matchesDayDateFormat(line)) {
                // Remove the weekday.
                line = line.replaceFirst("\\w+, ", "");
                return line + " " + getTimeString(firstPage, i + 1);
            }
            if (matchesSingleLineDateFormat(line)) {
                //March 12, 2014, at 10:00 a.m.
                return line.replaceFirst(", at", "");
            }
        }
        return null;
    }

    /** Returns the String containing time information.
     * If no time exists, set it to 12:00 a.m.*/
    private String getTimeString(List<String> firstPage, int i) {
        String timeText = formatLine(firstPage.get(i));
        if (START_TIME.matcher(timeText).find()) {
            return timeText;
        }
        return "12:00 a.m.";
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: April 5, 2014.
     * @param line
     * @return
     */
    private boolean matchesDateFormat(String line) {
        try {
            dateFormatter.parse(line);
            return true;
        }
        catch (DateTimeParseException ex) {
            // Ignore
        }
        return false;
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: Tuesday, April 5, 2014.
     * @param line
     * @return
     */
    private boolean matchesDayDateFormat(String line) {
        try {
            dayOfWeekDateFormatter.parse(line);
            return true;
        }
        catch (DateTimeParseException ex) {
            // Ignore
        }
        return false;
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: March 12, 2014, at 10:00 a.m.
     * @param line
     * @return
     */
    private boolean matchesSingleLineDateFormat(String line) {
        String singleLineDate = "(\\w+ \\d+, \\d+)(, at \\d+:\\d+ [apm.]{4})";
        if (line.matches(singleLineDate)) {
            return true;
        }

        return false;
    }


    /** Remove the ending time from the date time information.<br>
     * i.e. Input of '2:00 p.m. to 4:00 p.m.'<br>
     * returns '2:00 p.m.' */
    private String removeEndTime(String dateTime) {
        return TIME.matcher(dateTime).replaceAll("$1");
    }

    /** Capitalize a.m./p.m and remove the all '.' characters. */
    private String formatAmPm(String dateTime) {
        final Pattern AM_PM = Pattern.compile("(a.m.|p.m.)");
        Matcher matcher = AM_PM.matcher(dateTime);
        matcher.find();
        String capitalized = matcher.group(1).toUpperCase();
        return matcher.replaceFirst(capitalized).replaceAll("\\.", "");
    }

    /** Removes Line numbers, excess whitespace, new line, and non text characters */
    private String formatLine(String line) {
        String formatted = stripLineNumber(line).replaceAll("\\n", "");
        formatted = formatted.replace(String.valueOf((char)150), "to");
        formatted = formatted.replace("- ", "");
        return formatted;
    }
}
