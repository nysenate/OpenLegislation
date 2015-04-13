package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.util.DateUtils;
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
import java.util.regex.Pattern;

public abstract class SimpleCheckMailService extends BaseCheckMailService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCheckMailService.class);

    /**
     * Saves the text of all emails in the email receiving folder that have subjects that match the given pattern
     */
    protected int checkMail(Pattern subjectPattern) {
        Store store = null;
        int savedReports = 0;
        try {
            logger.info("checking for " + getCheckMailType() + " emails...");
            store = mailUtils.getCheckMailStore();

            Folder sourceFolder = mailUtils.navigateToFolder(environment.getEmailReceivingFolder(), store);
            Folder archiveFolder = mailUtils.navigateToFolder(environment.getEmailProcessedFolder(), store);
            sourceFolder.open(Folder.READ_WRITE);

            List<Message> messages = getMatchingMessages(subjectPattern, sourceFolder);
            logger.info("found {} messages", messages.size());
            List<Message> savedMessages = new ArrayList<>();
            for (Message message : messages) {
                try {
                    saveMessageBody(message, getSaveFile(message));
                    savedMessages.add(message);
                } catch (Exception ex) {
                    logger.error("Could not save message {}", message.getSubject());
                }
            }
            logger.info("archiving messages");
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

    /** Designates where a matched message will be saved */
    protected abstract File getSaveFile(Message message) throws MessagingException;

    protected abstract String getCheckMailType();

    protected String getSentDateString(Message message) throws MessagingException {
        return DateUtils.getLocalDateTime(message.getSentDate()).format(DateUtils.BASIC_ISO_DATE_TIME);
    }

    /** Gets all messages from the source folder whose subjects match the given pattern */
    private ArrayList<Message> getMatchingMessages(Pattern subjectPattern, Folder sourceFolder) throws MessagingException {
        ArrayList<Message> messages = new ArrayList<>();
        for (Message message : sourceFolder.getMessages()) {
            if (subjectPattern.matcher(message.getSubject()).matches()) {
                messages.add(message);
            }
        }
        return messages;
    }
}
