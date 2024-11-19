package gov.nysenate.openleg.common.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class DateUtils {
    private DateUtils() {}

    public static final DateTimeFormatter LRS_LAW_FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd"),
            LRS_ACTIONS_DATE = DateTimeFormatter.ofPattern("MM/dd/yy"),
            LRS_DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            LRS_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss'Z'"),
            LRS_WEBSITE_DATETIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a"),
            PUBLIC_WEBSITE_DUMP_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            BASIC_ISO_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    public static final Pattern BASIC_ISO_DATE_TIME_REGEX = Pattern.compile("\\d{8}T\\d{6}");
    public static final int LEG_DATA_START_YEAR = 2009;

    /** --- Reference Dates --- */

    public static final LocalDateTime LONG_AGO = LocalDate.of(1970, 1, 1).atStartOfDay(),
            THE_FUTURE = LocalDate.of(2999, 12, 31).atTime(LocalTime.MAX);
    public static final Range<LocalDateTime> ALL_DATE_TIMES = Range.closed(LONG_AGO, THE_FUTURE);

    /**
     * Retrieve the year of the given date.
     */
    public static Integer getYear(java.util.Date date) {
        return Year.from(date.toInstant()).getValue();
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

    /**
     * Convert a Date to a LocalDate at the system's default time zone. Returns null on null input.
     */
    public static LocalDate getLocalDate(java.util.Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalDate();
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
                lower = LONG_AGO;
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
                upper = THE_FUTURE;
            }
            return upper;
        }
        throw new IllegalArgumentException("Supplied localDateTimeRange is null.");
    }
}