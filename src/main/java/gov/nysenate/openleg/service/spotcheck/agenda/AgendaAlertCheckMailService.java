package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.service.spotcheck.base.CheckMailService;
import gov.nysenate.openleg.service.spotcheck.base.SimpleCheckMailService;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgendaAlertCheckMailService extends SimpleCheckMailService implements CheckMailService {

    protected static final Pattern agendaAlertSubjectPattern =
            Pattern.compile("^Senate\\s+Agenda\\s+for\\s+week\\s+of\\s+(\\d{2}/\\d{2}/\\d{4})$");

    @Override
    public int checkMail() {
        return checkMail(agendaAlertSubjectPattern);
    }

    @Override
    protected String getCheckMailType() {
        return "full agenda";
    }

    @Override
    protected File getSaveFile(Message message) throws MessagingException {
        Matcher subjectMatcher = agendaAlertSubjectPattern.matcher(message.getSubject());
        if (subjectMatcher.matches()) {
            LocalDate weekOf = LocalDate.parse(subjectMatcher.group(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String filename = String.format("agenda_alert-%s-full-%s.html",
                    weekOf.format(DateTimeFormatter.BASIC_ISO_DATE), getSentDateString(message));
            return new File(new File(environment.getStagingDir(), "alerts"), filename);
        }
        throw new IllegalArgumentException();
    }
}
