package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.processors.ParseError;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class HearingDateTimeParser {
    private static final String TIME_STR = "\\d{1,2}:\\d{2} (a.m.|p.m.)";
    private static final Pattern ALT_END_TIME = Pattern.compile("Whereupon.+at.+?(?<altTime>" + TIME_STR +")");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("[EEEE, ]MMMM d, yyyy"),
            TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private final LocalDate date;
    private final LocalTime startTime, endTime;

    public HearingDateTimeParser(String dateTimeText, List<String> lastPage) {
        String[] dateTimeAr = dateTimeText.replaceAll("( *(Date|Time):? *)", " ")
                .replaceAll(", at", "\n").replaceAll(" +", " ").trim().split("\n", 2);
        try {
            this.date = LocalDate.parse(dateTimeAr[0], DATE_FORMATTER);
        }
        catch (DateTimeParseException ex) {
            throw new ParseError("No date found in public hearing!");
        }
        String[] timeAr = (dateTimeAr.length > 1 ? dateTimeAr[1] : "").split("(-|to|–)+");
        this.startTime = formatAmPm(timeAr[0]);
        this.endTime = formatAmPm(timeAr.length > 1 ? timeAr[1] : altEndTime(lastPage));
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
        time = time.toUpperCase().replaceAll("\\.", "").trim();
        try {
            return LocalTime.parse(time, TIME_FORMATTER);
        }
        catch (DateTimeParseException ex) {
            return null;
        }
    }

    @Nonnull
    private static String altEndTime(List<String> lastPage) {
        String wholePage = String.join(" ", lastPage);
        Matcher matcher = ALT_END_TIME.matcher(wholePage);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group("altTime");
    }
}
