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
    private final static String AM_PM =  "(a.m.|p.m.)";

    private final static Pattern TIME = Pattern.compile("\\d+:\\d{2} " + AM_PM);

    private final static Pattern DATE_TIME = Pattern.compile("(?<date>\\w+ \\d{1,2}, \\d{4})(( at)? " +
                                                       "(?<startTime>" + TIME + ")" +
                                                       "( to (?<endTime>" + TIME + "))?)?");

    private final static Pattern END_TIME = Pattern.compile("Whereupon[^\\d]+(" + TIME +")");

    private final static String DATE_FORMATTER = "MMMM d, yyyy";

    private final static DateTimeFormatter DAY_OF_WEEK_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, " + DATE_FORMATTER);

    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private final static String SINGLE_LINE_DATE = "(\\w+ \\d+, \\d+)(, at \\d+:\\d+ " + AM_PM + ")";

    /**
     * Extract a LocalDate from the first page of a PublicHearing.
     * @param firstPage list of Strings containing the first page of text.
     * @return LocalDate the date of this public hearing.
     */
    public LocalDate parseDate(List<String> firstPage) {
        Matcher matcher = getDateTimeMatcher(firstPage);
        if (!matcher.find())
            return null;
        return LocalDate.parse(matcher.group("date"), DateTimeFormatter.ofPattern(DATE_FORMATTER));
    }

    public LocalTime parseStartTime(List<String> firstPage) {
        return parseTime(true, firstPage);
    }

    public LocalTime parseEndTime(List<String> firstPage, List<String> lastPage) {
        LocalTime t = parseTime(false, firstPage);
        return t == null ? alternateParseEndTime(lastPage):t;
    }

    private LocalTime parseTime(boolean isStartTime, List<String> firstPage) {
        Matcher matcher = getDateTimeMatcher(firstPage);
        if(!matcher.find())
            return null;
        String time = formatAmPm(matcher.group(isStartTime ? "startTime" : "endTime"));
        if (time == null)
            return null;
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    private LocalTime alternateParseEndTime(List<String> lastPage) {
        String wholePage = String.join("", lastPage);
        Matcher matcher = END_TIME.matcher(wholePage);
        if (!matcher.find())
            return null;
        String endTime = formatAmPm(matcher.group(1));
        if (endTime == null)
            return null;
        return LocalTime.parse(endTime, TIME_FORMATTER);
    }

    private Matcher getDateTimeMatcher(List<String> firstPage) {
        String dateTime = getDateTimeString(firstPage);
        return DATE_TIME.matcher(dateTime);
    }

    /**
     * Finds the Strings containing date and time information.
     * Concatenates these Strings into
     * a "<code>MMMM d, yyyy h:mm a to h:mm a</code>" formatted single String.
     * @param firstPage of the hearing.
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
        throw new IllegalArgumentException("Could not find date/time match in first page of hearing.");
    }

    /** Returns the String containing time information.
     * If no time exists return null.*/
    private String getTimeString(String line) {
        if (TIME.matcher(line).find()) {
            return line;
        }
        return null;
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: April 5, 2014.
     * @param line to check.
     * @return if it contians a date.
     */
    private boolean containsDate(String line) {
        try {
            DateTimeFormatter.ofPattern(DATE_FORMATTER).parse(line);
            return true;
        }
        catch (DateTimeParseException ex) {
            return false;
        }
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: Tuesday, April 5, 2014.
     */
    private boolean containsDayOfWeekAndDate(String line) {
        try {
            DAY_OF_WEEK_DATE_FORMATTER.parse(line);
            return true;
        }
        catch (DateTimeParseException ex) {
            return false;
        }
    }

    /**
     * Determines if the given String contains date time information.
     * Matches date Strings like: March 12, 2014, at 10:00 a.m.
     */
    private boolean containsDateAndTime(String line) {
        return line.matches(SINGLE_LINE_DATE);
    }

    /** Removes Line numbers, excess whitespace, new line, and non text characters */
    private List<String> formatLines(List<String> lines) {
        List<String> formattedLines = new ArrayList<>();
        for (String line : lines) {
            line = removeLineNumbers(line);
            line = removeNewLineCharacters(line);
            line = removeBadCharacters(line);
            line = removeDateTimeLabels(line);
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

    /**
     * Strip labels for date and/or time
     */
    private String removeDateTimeLabels(String line) {
        return line.replaceAll("\\s*(?:Date|Time):?\\s*", "");
    }

    /** Capitalize a.m./p.m and remove the all '.' characters. */
    private String formatAmPm(String dateTime) {
        if (dateTime == null)
            return null;
        Matcher matcher = Pattern.compile(AM_PM).matcher(dateTime);
        if (!matcher.find())
            return null;
        String capitalized = matcher.group(1).toUpperCase();
        return matcher.replaceFirst(capitalized).replaceAll("\\.", "");
    }
}
