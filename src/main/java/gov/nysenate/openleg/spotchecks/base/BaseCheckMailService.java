package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.Environment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import java.io.File;
import java.io.IOException;

public abstract class BaseCheckMailService implements CheckMailService {

    private static final Logger logger = LoggerFactory.getLogger(BaseCheckMailService.class);

    @Autowired
    protected Environment environment;

    @Autowired
    protected MailUtils mailUtils;

    @Override
    public int checkMail() {
        Store store = null;
        int savedCount = 0;
        try {
            store = mailUtils.getCheckMailStore();
            Folder sourceFolder = mailUtils.navigateToFolder(environment.getEmailReceivingFolder(), store);
            Folder archiveFolder = mailUtils.navigateToFolder(environment.getEmailProcessedFolder(), store);
            sourceFolder.open(Folder.READ_WRITE);
            savedCount = saveReports(sourceFolder, archiveFolder);
        }
        catch (MessagingException | IOException ex) {
            logger.error("CheckMail Error\n{}", ExceptionUtils.getStackTrace(ex));
        } catch (InterruptedException ignored) {
            logger.info("Shutdown message received, skipping checkMail()...");
        }
        finally {
            try {
                if (store != null)
                    store.close();
            } catch (MessagingException ignored) {}
        }
        return savedCount;
    }

    /**
     * Saves and archives all valid reports.
     * @param sourceFolder to check for valid emails in.
     * @param archiveFolder to move valid emails to.
     * @return the number of reports saved.
     */
    protected abstract int saveReports(Folder sourceFolder, Folder archiveFolder) throws MessagingException, IOException;

    /**
     * Saves a single email Message to the given File.
     */
    protected abstract void saveMessage(Message message, File file) throws MessagingException, IOException;

    protected void moveToArchive(Folder sourceFolder, Folder archive, Message... messages) throws MessagingException {
        sourceFolder.copyMessages(messages, archive);
        for (Message message : messages)
            message.setFlag(Flags.Flag.DELETED, true);
        sourceFolder.expunge();
    }
}
