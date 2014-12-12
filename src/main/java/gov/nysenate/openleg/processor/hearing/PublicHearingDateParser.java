package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PublicHearingDateParser
{
    private static Pattern START_TIME = Pattern.compile("\\d+:\\d{2} [ap].m.");

    private static Pattern DATE_TIME = Pattern.compile("(?<date>\\w+ \\d{1,2}, \\d{4})(( at)? " +
                                                       "(?<startTime>\\d{1,2}:\\d{2} [ap].m.)" +
                                                       "( to (?<endTime>\\d{1,2}:\\d{2} [ap].m.))?)?");

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private DateTimeFormatter dayOfWeekDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    /**
     * Extract a LocalDate from the first page of a PublicHearing.
     * @param firstPage list of Strings containing the first page of text.
     * @return LocalDate the date of this public hearing.
     */
    public LocalDate parseDate(List<String> firstPage) {
        Matcher matcher = getDateTimeMatcher(firstPage);
        matcher.find();
        return LocalDate.parse(matcher.group("date"), dateFormatter);
    }

    public LocalTime parseStartTime(List<String> firstPage) {
        Matcher matcher = getDateTimeMatcher(firstPage);
        matcher.find();

        String startTime = matcher.group("startTime");
        if (startTime == null) {
            return null;
        }
        startTime = formatAmPm(startTime);
        return LocalTime.parse(startTime, timeFormatter);
    }

    public LocalTime parseEndTime(List<String> firstPage) {
        Matcher matcher = getDateTimeMatcher(firstPage);
        matcher.find();

        String endTime = matcher.group("endTime");
        if (endTime == null) {
            return null;
        }
        endTime = formatAmPm(endTime);
        return LocalTime.parse(endTime, timeFormatter);
    }

    private Matcher getDateTimeMatcher(List<String> firstPage) {
        String dateTime = getDateTimeString(firstPage);
        return DATE_TIME.matcher(dateTime);
    }

    /**
     * Finds the Strings containing date and time information.
     * Concatenates these Strings into
     * a "<code>MMMM d, yyyy h:mm a to h:mm a</code>" formatted single String.
     * @param firstPage
     * @return A String containing date time information.
     */
    private String getDateTimeString(List<String> firstPage) {
        firstPage = formatLines(firstPage);
        for (int i = 0; i < firstPage.size(); i++) {
            String line = firstPage.get(i);
            if (containsDate(line)) {
                return line + " " + getTimeString(firstPage.get(i + 1));
            }
            if (containsDayOfWeekAndDate(line)) {
                // Remove the weekday.
                line = line.replaceFirst("\\w+, ", "");
                return line + " " + getTimeString(firstPage.get(i + 1));
            }
            if (containsDateAndTime(line)) {
                //March 12, 2014, at 10:00 a.m.
                return line.replaceFirst(", at", "");
            }
        }
        return null;
    }

    /** Returns the String containing time information.
     * If no time exists return null.*/
    private String getTimeString(String line) {
        if (START_TIME.matcher(line).find()) {
            return line;
        }
        return null;
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: April 5, 2014.
     * @param line
     * @return
     */
    private boolean containsDate(String line) {
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
     */
    private boolean containsDayOfWeekAndDate(String line) {
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
     */
    private boolean containsDateAndTime(String line) {
        String singleLineDate = "(\\w+ \\d+, \\d+)(, at \\d+:\\d+ [apm.]{4})";
        if (line.matches(singleLineDate)) {
            return true;
        }
        return false;
    }

    /** Removes Line numbers, excess whitespace, new line, and non text characters */
    private List<String> formatLines(List<String> lines) {
        List<String> formattedLines = new ArrayList<>();
        for (String line : lines) {
            line = removeLineNumbers(line);
            line = removeNewLineCharacters(line);
            line = removeBadCharacters(line);
            formattedLines.add(line);
        }
        return formattedLines;
    }

    private String removeLineNumbers(String line) {
        return PublicHearingTextUtils.stripLineNumber(line);
    }

    private String removeNewLineCharacters(String line) {
        return line.replaceAll("\\n", "");
    }

    private String removeBadCharacters(String line) {
        line = line.replace(String.valueOf((char) 65533), "to");
        line = line.replace("- ", "");
        return line;
    }

    /** Capitalize a.m./p.m and remove the all '.' characters. */
    private String formatAmPm(String dateTime) {
        final Pattern AM_PM = Pattern.compile("(a.m.|p.m.)");
        Matcher matcher = AM_PM.matcher(dateTime);
        matcher.find();
        String capitalized = matcher.group(1).toUpperCase();
        return matcher.replaceFirst(capitalized).replaceAll("\\.", "");
    }
}
