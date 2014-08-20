package gov.nysenate.openleg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class DateUtils
{
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /** --- Date Formats --- */

    public final static DateTimeFormatter LRS_ACTIONS_DATE = DateTimeFormatter.ofPattern("MM/dd/yy");
    public final static DateTimeFormatter LRS_DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter LRS_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss'Z'");

    /** --- Static Methods --- */

    /**
     * Retrieve the year of the given date.
     */
    public static Integer getYear(Date date) {
        return Year.from(date.toInstant()).getValue();
    }

    /**
     * Shorthand method to return a LocalDateTime from 'millis since epoch'.
     */
    public static LocalDateTime getLocalDateTimeFromMillis(long millis) {
        return LocalDateTime.from(Instant.ofEpochMilli(millis));
    }

    /**
     * A session year refers to that start of a 2 year legislative session period.
     * This method ensures that any given year will resolve to the correct session start year.
     */
    public static int resolveSession(int year) {
        return (year % 2 == 0) ? year - 1 : year;
    }

    /**
     * Extract the LocalDate value from the LRS formatted date string.
     * @throws java.time.format.DateTimeParseException if unable to parse the requested result.
     */
    public static LocalDate getLrsLocalDate(String lbdcDate) {
        return LocalDate.from(LRS_DATE_ONLY_FORMAT.parse(lbdcDate));
    }

    /**
     * Extract the Date (with time) from the LRS formatted date/time string.
     * @throws java.time.format.DateTimeParseException if unable to parse the requested result.
     */
    public static LocalDateTime getLrsDateTime(String lbdcDateTime) {
        return LocalDateTime.from(LRS_DATETIME_FORMAT.parse(lbdcDateTime));
    }
}