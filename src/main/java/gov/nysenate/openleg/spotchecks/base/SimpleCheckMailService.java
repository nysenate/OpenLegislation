package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.common.util.DateUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleCheckMailService extends BaseCheckMailService implements CheckMailService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCheckMailService.class);
    protected static String datePattern = "(?<date>\\d{2}/\\d{2}/\\d{4})";

    /**
     * Saves the text of all emails in the email receiving folder that have subjects that match the given pattern
     */
    @Override
    public int checkMail() {
        Store store = null;
        int savedReports = 0;
        try {
            store = mailUtils.getCheckMailStore();

            Folder sourceFolder = mailUtils.navigateToFolder(environment.getEmailReceivingFolder(), store);
            Folder archiveFolder = mailUtils.navigateToFolder(environment.getEmailProcessedFolder(), store);
            sourceFolder.open(Folder.READ_WRITE);

            List<Message> messages = getMatchingMessages(getPattern(), sourceFolder);
            List<Message> savedMessages = new ArrayList<>();
            for (Message message : messages) {
                try {
                    saveMessageBody(message, getSaveFile(message));
                    savedMessages.add(message);
                } catch (Exception ex) {
                    logger.error("Could not save message {}", message.getSubject());
                }
            }
            moveToArchive(sourceFolder, archiveFolder, savedMessages.toArray(new Message[messages.size()]));
        } catch (MessagingException ex) {
            logger.error("CheckMail Error\n{}", ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException ignored) {}
        }
        return savedReports;
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
    private List<Message> getMatchingMessages(Pattern subjectPattern, Folder sourceFolder) throws MessagingException {
        var messages = new ArrayList<Message>();
        var sourceMessages = sourceFolder.getMessages();
        for (Message message : sourceMessages) {
            if (subjectPattern.matcher(message.getSubject()).matches()) {
                messages.add(message);
                logger.info("Saving and archiving {} email message with subject: {}", getCheckMailType(), message.getSubject());
            }
        }
        return messages;
    }
}
