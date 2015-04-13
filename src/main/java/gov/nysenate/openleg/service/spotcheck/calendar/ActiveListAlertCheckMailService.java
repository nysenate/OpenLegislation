package gov.nysenate.openleg.service.spotcheck.calendar;

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
public class ActiveListAlertCheckMailService extends SimpleCheckMailService implements CheckMailService {

    protected static final Pattern activeListAlertSubjectPattern =
            Pattern.compile("^Senate\\s+Active\\s+List\\s+No\\.\\s+(\\d+)\\s+for\\s+[A-z]+day\\s+(\\d{2}/\\d{2}/\\d{4})$");

    @Override
    public int checkMail() {
        return checkMail(activeListAlertSubjectPattern);
    }

    @Override
    protected File getSaveFile(Message message) throws MessagingException {
        Matcher subjectMatcher = activeListAlertSubjectPattern.matcher(message.getSubject());
        if (subjectMatcher.matches()) {
            LocalDate calDate = LocalDate.parse(subjectMatcher.group(2), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String filename = String.format("active_list_alert-%d-%s-%s.html",
                    calDate.getYear(), subjectMatcher.group(1), getSentDateString(message));
            return new File(new File(environment.getStagingDir(), "alerts"), filename);
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected String getCheckMailType() {
        return "active list";
    }
}
