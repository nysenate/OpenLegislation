package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.service.spotcheck.base.CheckMailService;
import gov.nysenate.openleg.service.spotcheck.base.SimpleCheckMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FloorCalAlertCheckMailService extends SimpleCheckMailService implements CheckMailService {

    private static final Logger logger = LoggerFactory.getLogger(FloorCalAlertCheckMailService.class);

    protected static final Pattern floorCalAlertSubjectPattern =
            Pattern.compile("^Senate Cal No\\.\\s+(\\d+[A-z]?)\\s+for\\s+[A-z]+day\\s+(\\d{2}/\\d{2}/\\d{4})$");

    @Override
    public int checkMail() {
        return checkMail(floorCalAlertSubjectPattern);
    }

    @Override
    protected File getSaveFile(Message message) throws MessagingException {
        Matcher subjectMatcher = floorCalAlertSubjectPattern.matcher(message.getSubject());
        if (subjectMatcher.matches()) {
            LocalDate calDate = LocalDate.parse(subjectMatcher.group(2), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String filename = String.format("floor_cal_alert-%d-%s-%s.html",
                    calDate.getYear(), subjectMatcher.group(1), getSentDateString(message));
            return new File(new File(environment.getStagingDir(), "alerts"), filename);
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected String getCheckMailType() {
        return "floor calendar";
    }
}
