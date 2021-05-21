package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.processors.ParseError;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PublicHearingDateParser {
    private final static String TIME_STR =  "\\d{1,2}:\\d{2} (a.m.|p.m.)",
            DAYS_OF_THE_WEEK = toOptionString(DayOfWeek.values()),
            MONTHS = toOptionString(Month.values());
    private final static Pattern DATE_TIME = Pattern.compile("(?i)(" + DAYS_OF_THE_WEEK + ")?(, )?" +
                "(?<date>(" + MONTHS + ") \\d{1,2}, \\d{4})" + " ?(?<startTime>" + TIME_STR + ")?" +
                "( (to|-) (?<endTime>" + TIME_STR + "))?"),
            ALT_END_TIME = Pattern.compile("Whereupon(, at)?( approximately)? (?<altTime>" + TIME_STR +")");
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy"),
            TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    /**
     * Utility method to match enums.
     * @param enums from a certain class.
     * @return Pattern string.
     */
    private static String toOptionString(Enum<?>... enums) {
        return Arrays.stream(enums).map(Enum::name).collect(Collectors.joining("|"));
    }

    private final LocalDate date;
    private final LocalTime startTime, endTime;

    // TODO: use newlines to split date and time?
    public PublicHearingDateParser(String dateTimeText, List<String> lastPage) {
        dateTimeText = dateTimeText.replaceAll("(\\n|\\s*(?:Date|Time):?\\s*)", " ")
                .replaceAll(", at", "").replaceAll("\\s+", " ");
        Matcher matcher = DATE_TIME.matcher(dateTimeText);
        if (!matcher.find())
            throw new ParseError("No date found in public hearing!");
        this.date = LocalDate.parse(matcher.group("date"), DATE_FORMATTER);
        this.startTime = formatAmPm(matcher.group("startTime"));
        String endTimeStr = Optional.ofNullable(matcher.group("endTime"))
                .orElse(altEndTime(lastPage));
        this.endTime = formatAmPm(endTimeStr);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    private static LocalTime formatAmPm(String time) {
        if (time == null)
            return null;
        time = time.toUpperCase().replaceAll("\\.", "");
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    private static String altEndTime(List<String> lastPage) {
        String wholePage = String.join("", lastPage);
        Matcher matcher = ALT_END_TIME.matcher(wholePage);
        if (!matcher.find())
            return null;
        return matcher.group("altTime");
    }
}
