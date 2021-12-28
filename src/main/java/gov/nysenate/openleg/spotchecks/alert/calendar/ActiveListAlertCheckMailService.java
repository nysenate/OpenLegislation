package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.spotchecks.base.SimpleCheckMailService;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ActiveListAlertCheckMailService extends SimpleCheckMailService {
    private static final Pattern activeListAlertSubjectPattern =
            Pattern.compile("^Senate\\s+Active\\s+List\\s+No\\.\\s+(?<calNoAndSup>\\d+(-\\d+)?)[A-Z]?\\s+for\\s+[A-z]+day\\s+" +
                    datePattern + "$");

    @Override
    protected Pattern getPattern() {
        return activeListAlertSubjectPattern;
    }

    @Override
    protected String getFilename(Message message, Matcher matcher) throws MessagingException {
        LocalDate date = LocalDate.parse(matcher.group("date"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return String.format("active_list_alert-%d-%s-%s.html",
                date.getYear(), matcher.group("calNoAndSup"), getSentDateString(message));
    }

    @Override
    protected String getCheckMailType() {
        return "active list";
    }
}
