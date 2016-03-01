package gov.nysenate.openleg.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.postgresql.util.PGInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.regex.Pattern;

public abstract class DateUtils
{
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /** --- Date Formats --- */

    public final static DateTimeFormatter LRS_LAW_FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    public final static DateTimeFormatter LRS_ACTIONS_DATE = DateTimeFormatter.ofPattern("MM/dd/yy");
    public final static DateTimeFormatter LRS_DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter LRS_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss'Z'");
    public final static DateTimeFormatter LRS_WEBSITE_DATETIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a");

    public final static DateTimeFormatter PUBLIC_WEBSITE_DUMP_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public final static DateTimeFormatter BASIC_ISO_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    public final static Pattern BASIC_ISO_DATE_TIME_REGEX = Pattern.compile("\\d{8}T\\d{6}");

    /** --- Reference Dates --- */

    public static final LocalDate LONG_AGO = LocalDate.of(1970, 1, 1);
    public static final LocalDate THE_FUTURE = LocalDate.of(2999, 12, 31);
    public static final Range<LocalDate> ALL_DATES = Range.closed(LONG_AGO, THE_FUTURE);
    public static final Range<LocalDateTime> ALL_DATE_TIMES = Range.closed(LONG_AGO.atStartOfDay(), atEndOfDay(THE_FUTURE));

    /** --- Static Methods --- */

    /**
     * Retrieve the year of the given date.
     */
    public static Integer getYear(java.util.Date date) {
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
     * Extract the Date (with time) from the LRS formatted date/time string.
     * @throws java.time.format.DateTimeParseException if unable to parse the requested result.
     */
    public static LocalDateTime getLrsWebsiteDateTime(String lbdcDateTime) {
        return LocalDateTime.from(LRS_WEBSITE_DATETIME_FORMAT.parse(lbdcDateTime));
    }

    /**
     * Convert a Date to a LocalDate at the system's default time zone. Returns null on null input.
     */
    public static LocalDate getLocalDate(java.util.Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalDate();
    }

    /**
     * Convert a Date to a LocalTime at the system's default time zone.  Returns null on null input.
     *
     * @param date
     * @return
     */
    public static LocalTime getLocalTime(Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalTime();
    }

    /**
     * Convert a Date to a LocalDateTime at the system's default time zone. Returns null on null input.
     */
    public static LocalDateTime getLocalDateTime(java.util.Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Convert a LocalDate to a Date. Returns null on null input.
     */
    public static java.sql.Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * Convert a LocalDateTime to a Date. Returns null on null input.
     */
    public static Timestamp toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Timestamp.valueOf(localDateTime);
    }

    public static Time toTime(LocalTime localTime) {
        if (localTime == null) return null;
        return Time.valueOf(localTime);
    }

    public static PGInterval toInterval(Period period, Duration duration) {
        if (duration == null || period == null) return null;
        return new PGInterval(period.getYears(), period.getMonths(), period.getDays(),
                (int) duration.toHours(), (int) duration.toMinutes() % 60,
                (double) (duration.toMillis() % (1000 * 60)) / 1000);
    }

    public static Period getPeriod(PGInterval interval) {
        if (interval == null) return null;
        return Period.of(interval.getYears(), interval.getMonths(), interval.getDays());
    }

    public static Duration getDuration(PGInterval interval) {
        if (interval == null) return null;
        return Duration.ofMillis(
                (long) (interval.getSeconds() * 1000) + interval.getMinutes() * 60000 + interval.getHours() * 3600000);
    }

    /** --- Date Range methods --- */

    /**
     * Converts a LocalDateTime range to a closed LocalDate range
     * The resulting LocalDate range includes all Dates that contain times that occurred within the given range
     *
     * @param dateTimeRange
     * @return
     */
    public static Range<LocalDate> toDateRange(Range<LocalDateTime> dateTimeRange) {
        return Range.closed(
                startOfDateTimeRange(dateTimeRange).toLocalDate(),
                endOfDateTimeRange(dateTimeRange).toLocalDate()
        );
    }

    /**
     * Converts a LocalDate range to a closed LocalDateTime range
     * The LocalDateTimeRange includes all times that occur within the included LocalDates
     *
     * @param dateRange
     * @return
     */
    public static Range<LocalDateTime> toDateTimeRange(Range<LocalDate> dateRange) {
        return Range.closed(
                startOfDateRange(dateRange).atStartOfDay(),
                endOfDateRange(dateRange).plusDays(1).atStartOfDay()
        );
    }

    /**
     * Given the LocalDate range, extract the lower bound LocalDate. If the lower bound is not set,
     * a really early date will be returned. If the bound is open, a single day will be added to the
     * LocalDate. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Lower bound in the date range
     */
    public static LocalDate startOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            LocalDate lower;
            if (localDateRange.hasLowerBound()) {
                lower = (localDateRange.lowerBoundType().equals(BoundType.CLOSED))
                        ? localDateRange.lowerEndpoint() : localDateRange.lowerEndpoint().plusDays(1);
            }
            else {
                lower = LONG_AGO;
            }
            return lower;
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
    }

    /**
     * Given the LocalDateTime range, extract the upper bound LocalDateTime. If the upper bound is not set, a
     * date far in the future will be returned. If the bound is open, a single day will be subtracted
     * from the LocalDateTime. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Upper bound in the date range
     */
    public static LocalDate endOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            LocalDate upper;
            if (localDateRange.hasUpperBound()) {
                upper = (localDateRange.upperBoundType().equals(BoundType.CLOSED))
                        ? localDateRange.upperEndpoint() : localDateRange.upperEndpoint().minusDays(1);
            }
            else {
                upper = THE_FUTURE;
            }
            return upper;
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
    }

    /**
     * Given the LocalDateTime range, extract the lower bound LocalDateTime. If the lower bound is not set,
     * a really early date will be returned. If the bound is open, a single microsecond will be added to the
     * LocalDateTime. If its closed, the dateTime will remain as is.
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @return LocalDateTime - Lower bound in the dateTime range
     */
    public static LocalDateTime startOfDateTimeRange(Range<LocalDateTime> dateTimeRange) {
        if (dateTimeRange != null) {
            LocalDateTime lower;
            if (dateTimeRange.hasLowerBound()) {
                lower = (dateTimeRange.lowerBoundType().equals(BoundType.CLOSED))
                        ? dateTimeRange.lowerEndpoint() : dateTimeRange.lowerEndpoint().plusNanos(1000);
            }
            else {
                lower = LONG_AGO.atStartOfDay();
            }
            return lower;
        }
        throw new IllegalArgumentException("Supplied localDateTimeRange is null.");
    }

    /**
     * Given the LocalDateTime range, extract the upper bound LocalDateTime. If the upper bound is not set, a
     * date far in the future will be returned. If the bound is open, a single microsecond will be subtracted
     * from the LocalDateTime. If its closed, the date will remain as is.
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @return LocalDateTime - Upper bound in the dateTime range
     */
    public static LocalDateTime endOfDateTimeRange(Range<LocalDateTime> dateTimeRange) {
        if (dateTimeRange != null) {
            LocalDateTime upper;
            if (dateTimeRange.hasUpperBound()) {
                upper = (dateTimeRange.upperBoundType().equals(BoundType.CLOSED))
                        ? dateTimeRange.upperEndpoint() : dateTimeRange.upperEndpoint().minusNanos(1000);
            }
            else {
                upper = atEndOfDay(THE_FUTURE);
            }
            return upper;
        }
        throw new IllegalArgumentException("Supplied localDateTimeRange is null.");
    }
}