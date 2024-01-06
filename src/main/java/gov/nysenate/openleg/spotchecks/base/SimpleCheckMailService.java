package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleCheckMailService extends BaseCheckMailService {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCheckMailService.class);
    protected static final String datePattern = "(?<date>\\d{2}/\\d{2}/\\d{4})";
    private File mailStagingDir;

    @PostConstruct
    private void init() {
        mailStagingDir = new File(environment.getStagingDir(), "alerts");
    }

    @Override
    protected int saveReports() throws MessagingException {
        var messages = new ArrayList<Message>();
        for (Message message : mailUtils.getIncomingMessages()) {
            String subject = message.getSubject().replaceAll("\\s+", " ").trim();
            var subjectMatcher = getPattern().matcher(subject);
            if (!subjectMatcher.matches()) {
                continue;
            }
            messages.add(message);
            logger.info("Saving {} email message with subject: {}", getCheckMailType(), subject);
            String sentDate = DateUtils.getLocalDateTime(message.getSentDate())
                    .format(DateUtils.BASIC_ISO_DATE_TIME);
            var file = new File(mailStagingDir, getFilename(sentDate, subjectMatcher));
            try {
                saveMessage(message, file);
            }
            catch (Exception ex) {
                logger.error("Could not save message {}", subject);
            }
        }
        mailUtils.moveMessages(messages, true);
        return messages.size();
    }

    @Override
    protected void saveMessage(Message message, File file) throws MessagingException, IOException {
        if (message.isMimeType("text/*")) {
            FileIOUtils.write(file, (String) message.getContent());
        } else if (message.isMimeType("multipart/*")) {
            Multipart content = (Multipart) message.getContent();
            for (int i = 0; i < content.getCount(); i++) {
                Part part = content.getBodyPart(i);
                if (!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    logger.info("\tsaving body of {} to {}", message.getSubject(), file.getAbsolutePath());
                    FileIOUtils.write(file, (String) part.getContent());
                }
            }
        }
    }

    protected abstract Pattern getPattern();

    protected abstract String getFilename(String sentDateTime, Matcher matcher) throws MessagingException;

    protected abstract String getCheckMailType();
}
