package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.spotchecks.base.SimpleCheckMailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FloorCalAlertCheckMailService extends SimpleCheckMailService {
    private static final Pattern floorCalAlertSubjectPattern =
            Pattern.compile("^Senate Cal No\\. (?<calNo>\\d+[A-z]?) for [A-z]+day " + datePattern + "$");

    @Override
    protected Pattern getPattern() {
        return floorCalAlertSubjectPattern;
    }

    @Override
    protected String getFilename(String sentDate, Matcher matcher) {
        LocalDate date = LocalDate.parse(matcher.group("date"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return String.format("floor_cal_alert-%d-%s-%s.html", date.getYear(), matcher.group("calNo"), sentDate);
    }

    @Override
    protected String getCheckMailType() {
        return "floor calendar";
    }
}
