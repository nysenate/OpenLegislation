package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.service.spotcheck.base.CheckMailService;
import gov.nysenate.openleg.service.spotcheck.base.SimpleCheckMailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommAgendaAlertCheckMailService extends SimpleCheckMailService implements CheckMailService {

    protected static final Pattern commAgendaSubjectPattern =
            Pattern.compile("^Senate\\s+-\\s+([A-z,\\s]+)\\s+-\\s+Committee\\s+Agenda\\s+" +
                    "(?:-\\s+Addendum\\s+([A-z])\\s+-\\s+)?for\\s+week\\s+of\\s+" +
                    "(\\d{2}/\\d{2}/\\d{4})\\s+-\\s+Posted\\s+(\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))\\s*$");

    @Override
    public int checkMail() {
        return checkMail(commAgendaSubjectPattern);
    }

    @Override
    protected File getSaveFile(Message message) throws MessagingException {
        Matcher subjectMatcher = commAgendaSubjectPattern.matcher(message.getSubject());
        if (subjectMatcher.matches()) {
            String addendum = subjectMatcher.group(2);
            if (StringUtils.isBlank(addendum)) {
                addendum = "original";
            }
            LocalDate weekOf = LocalDate.parse(subjectMatcher.group(3), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            LocalDateTime posted = LocalDateTime.parse(subjectMatcher.group(4).replace("\\s+", " "),
                    DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
            String filename = String.format("agenda_alert-%s-%s-%s-%s.html",
                    weekOf.format(DateTimeFormatter.BASIC_ISO_DATE),
                    subjectMatcher.group(1).replace(' ', '_').replace(',', '.'), addendum,
                    getSentDateString(message));
            return new File(new File(environment.getStagingDir(), "alerts"), filename);
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected String getCheckMailType() {
        return "committee agenda";
    }
}
