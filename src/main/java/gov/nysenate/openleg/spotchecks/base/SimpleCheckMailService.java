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
import java.util.List;
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
        List<Message> messages = getMatchingMessages(mailUtils.getIncomingMessages());
        List<Message> savedMessages = new ArrayList<>();
        for (Message message : messages) {
            try {
                saveMessage(message, getSaveFile(message));
                savedMessages.add(message);
            } catch (Exception ex) {
                logger.error("Could not save message {}", message.getSubject());
            }
        }
        mailUtils.moveMessages(savedMessages, true);
        return savedMessages.size();
    }

    @Override
    protected void saveMessage(Message message, File file) throws MessagingException, IOException {
        if (message.isMimeType("text/*")) {
            FileIOUtils.write(file, (String) message.getContent());
        } else if (message.isMimeType("multipart/*")) {
            Multipart content = (Multipart) message.getContent();
            for (int i = 0; i < content.getCount(); i++) {
                Part part = content.getBodyPart(i);
                if (!part.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                    logger.info("\tsaving body of {} to {}", message.getSubject(), file.getAbsolutePath());
                    FileIOUtils.write(file, (String) part.getContent());
                }
            }
        }
    }

    protected abstract Pattern getPattern();

    protected abstract String getFilename(String sentDateTime, Matcher matcher) throws MessagingException;

    /** Designates where a matched message will be saved */
    protected File getSaveFile(Message message) throws MessagingException {
        Matcher subjectMatcher = getPattern().matcher(message.getSubject());
        // TODO: this repeats getMatchingMessages
        if (subjectMatcher.matches()) {
            String sentDate = DateUtils.getLocalDateTime(message.getSentDate()).format(DateUtils.BASIC_ISO_DATE_TIME);
            return new File(mailStagingDir, getFilename(sentDate, subjectMatcher));
        }
        throw new IllegalArgumentException();
    }

    protected abstract String getCheckMailType();

    /** Gets all messages from the source folder whose subjects match the given pattern */
    private List<Message> getMatchingMessages(Message[] sourceMessages) throws MessagingException {
        var messages = new ArrayList<Message>();
        for (Message message : sourceMessages) {
            if (getPattern().matcher(message.getSubject()).matches()) {
                messages.add(message);
                logger.info("Saving {} email message with subject: {}", getCheckMailType(), message.getSubject());
            }
        }
        return messages;
    }
}
