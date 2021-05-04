package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
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
    private final static Pattern TIME = Pattern.compile(TIME_STR),
            DATE_TIME = Pattern.compile("(?i)(" + DAYS_OF_THE_WEEK + ")?(, )?" + "(?<date>(" + MONTHS + ") \\d{1,2}, \\d{4}) " +
                    "(?<startTime>" + TIME_STR + ")?" + "( to (?<endTime>" + TIME_STR + "))?"),
            ALT_END_TIME = Pattern.compile("Whereupon[^\\d]+(" + TIME +")");
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy"),
            TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    // TODO: change accessors.
    private final LocalDate date;
    private final LocalTime startTime, endTime;

    public PublicHearingDateParser(List<String> firstPage, List<String> lastPage) {
        String wholeFirstPage = firstPage.stream().map(PublicHearingDateParser::formatLine)
                .collect(Collectors.joining(" ")).replaceAll("\\s+", " ");
        Matcher matcher = DATE_TIME.matcher(wholeFirstPage);
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

    private static String toOptionString(Enum<?>... enums) {
        return Arrays.stream(enums).map(Enum::name).collect(Collectors.joining("|"));
    }

    /** Capitalize a.m./p.m and remove the all '.' characters. */
    private LocalTime formatAmPm(String time) {
        if (time == null)
            return null;
        time = time.toUpperCase().replaceAll("\\.", "");
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    private String altEndTime(List<String> lastPage) {
        String wholePage = String.join("", lastPage);
        Matcher matcher = ALT_END_TIME.matcher(wholePage);
        if (!matcher.find())
            return null;
        return matcher.group(1);
    }

    private static String formatLine(String line) {
        return PublicHearingTextUtils.stripLineNumber(line)
                .replaceAll("(\\n|- |\\s*(?:Date|Time):?\\s*)", "")
                .replaceAll(", at", "").replace(String.valueOf((char) 65533), "to");
    }
}
