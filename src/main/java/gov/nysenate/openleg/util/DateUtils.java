package gov.nysenate.openleg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class DateUtils
{
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /** --- Date Formats --- */

    public final static DateTimeFormatter LRS_LAW_FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
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
     * Shorthand method to return a LocalDateTime from 'millis since longAgo'.
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
     * Returns a LocalDateTime that represents the time just before the start of the next day.
     */
    public static LocalDateTime atEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
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

    /**
     * Return a LocalDateTime that represents the epoch date.
     * @return LocalDateTime
     */
    public static LocalDateTime longAgo() {
        return LocalDate.ofEpochDay(0).atStartOfDay();
    }

    /**
     * Convert a Date to a LocalDate at the system's default time zone. Returns null on null input.
     */
    public static LocalDate getLocalDate(Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalDate();
    }

    /**
     * Convert a Date to a LocalDateTime at the system's default time zone. Returns null on null input.
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Convert a LocalDate to a Date. Returns null on null input.
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return toDate(localDate.atStartOfDay());
    }

    /**
     * Convert a LocalDateTime to a Date. Returns null on null input.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}