package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.spotchecks.base.SimpleCheckMailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommAgendaAlertCheckMailService extends SimpleCheckMailService {
    private static final Pattern commAgendaSubjectPattern =
            Pattern.compile("^Senate\\s+-\\s+([A-z,\\s]+)\\s+-\\s+Committee\\s+Agenda\\s+" +
                    "(?:-\\s+Addendum\\s+([A-z])\\s+-\\s+)?for\\s+week\\s+of\\s+" + datePattern +
                    "\\s+-\\s+Posted\\s+(\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))\\s*$");

    @Override
    protected Pattern getPattern() {
        return commAgendaSubjectPattern;
    }

    @Override
    protected String getFilename(Message message, Matcher matcher) throws MessagingException {
        LocalDate weekOf = LocalDate.parse(matcher.group("date"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String addendum = matcher.group(2);
        if (StringUtils.isBlank(addendum))
            addendum = "original";
        return String.format("agenda_alert-%s-%s-%s-%s.html",
                weekOf.format(DateTimeFormatter.BASIC_ISO_DATE),
                matcher.group(1).replace(' ', '_').replace(',', '.'), addendum,
                getSentDateString(message));
    }

    @Override
    protected String getCheckMailType() {
        return "committee agenda";
    }
}
