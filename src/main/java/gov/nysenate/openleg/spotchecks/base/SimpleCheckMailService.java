package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleCheckMailService extends BaseCheckMailService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCheckMailService.class);
    protected static String datePattern = "(?<date>\\d{2}/\\d{2}/\\d{4})";

    @Override
    protected int saveReports(Folder sourceFolder, Folder archiveFolder) throws MessagingException {
        List<Message> messages = getMatchingMessages(sourceFolder);
        List<Message> savedMessages = new ArrayList<>();
        for (Message message : messages) {
            try {
                saveMessage(message, getSaveFile(message));
                savedMessages.add(message);
            } catch (Exception ex) {
                logger.error("Could not save message {}", message.getSubject());
            }
        }
        moveToArchive(sourceFolder, archiveFolder, savedMessages.toArray(new Message[0]));
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
                if (!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    logger.info("\tsaving body of {} to {}", message.getSubject(), file.getAbsolutePath());
                    FileIOUtils.write(file, (String) part.getContent());
                }
            }
        }
    }

    protected abstract Pattern getPattern();

    protected abstract String getFilename(Message message, Matcher matcher) throws MessagingException;

    /** Designates where a matched message will be saved */
    protected File getSaveFile(Message message) throws MessagingException {
        Matcher subjectMatcher = getPattern().matcher(message.getSubject());
        if (subjectMatcher.matches())
            return new File(new File(environment.getStagingDir(), "alerts"), getFilename(message, subjectMatcher));
        throw new IllegalArgumentException();
    }

    protected abstract String getCheckMailType();

    protected String getSentDateString(Message message) throws MessagingException {
        return DateUtils.getLocalDateTime(message.getSentDate()).format(DateUtils.BASIC_ISO_DATE_TIME);
    }

    /** Gets all messages from the source folder whose subjects match the given pattern */
    private List<Message> getMatchingMessages(Folder sourceFolder) throws MessagingException {
        logger.info("Starting message search for " + getCheckMailType());
        var messages = new ArrayList<Message>();
        var sourceMessages = sourceFolder.getMessages();
        for (Message message : sourceMessages) {
            if (getPattern().matcher(message.getSubject()).matches()) {
                messages.add(message);
                logger.info("Saving and archiving {} email message with subject: {}", getCheckMailType(), message.getSubject());
            }
            // We could continue processing if the Thread has been interrupted, but it would take too long.
            if (Thread.currentThread().isInterrupted())
                return List.of();
        }
        return messages;
    }
}
