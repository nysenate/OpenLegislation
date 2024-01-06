package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.spotchecks.base.SimpleCheckMailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ActiveListAlertCheckMailService extends SimpleCheckMailService {
    private static final Pattern activeListAlertSubjectPattern =
            Pattern.compile("^Senate Active List No\\. (?<calNoAndSup>\\d+(-\\d+)?)[A-Z]? for [A-z]+day " +
                    datePattern + "$");

    @Override
    protected Pattern getPattern() {
        return activeListAlertSubjectPattern;
    }

    @Override
    protected String getFilename(String sentDate, Matcher matcher) {
        LocalDate date = LocalDate.parse(matcher.group("date"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return String.format("active_list_alert-%d-%s-%s.html", date.getYear(),
                matcher.group("calNoAndSup"), sentDate);
    }

    @Override
    protected String getCheckMailType() {
        return "active list";
    }
}
