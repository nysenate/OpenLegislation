package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.spotchecks.base.SimpleCheckMailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgendaAlertCheckMailService extends SimpleCheckMailService {
    private static final Pattern agendaAlertSubjectPattern =
            Pattern.compile("^Senate Agenda for week of " + datePattern + "$");

    @Override
    protected Pattern getPattern() {
        return agendaAlertSubjectPattern;
    }

    @Override
    protected String getFilename(String sentDate, Matcher matcher) {
        LocalDate date = LocalDate.parse(matcher.group("date"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return String.format("agenda_alert-%s-full-%s.html",
                date.format(DateTimeFormatter.BASIC_ISO_DATE), sentDate);
    }

    @Override
    protected String getCheckMailType() {
        return "full agenda";
    }
}
