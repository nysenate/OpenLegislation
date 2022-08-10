package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

public abstract class BaseCheckMailService implements CheckMailService {
    private static final Logger logger = LoggerFactory.getLogger(BaseCheckMailService.class);

    @Autowired
    protected OpenLegEnvironment environment;
    @Autowired
    protected MailUtils mailUtils;

    @Override
    public int checkMail() {
        if (!environment.isCheckmailEnabled()) {
            return 0;
        }
        int savedCount = 0;
        try {
            mailUtils.createCheckMailConnection();
            savedCount = saveReports();
        }
        catch (MessagingException | IOException ex) {
            logger.error("CheckMail Error\n{}", ExceptionUtils.getStackTrace(ex));
        }
        return savedCount;
    }

    /**
     * Saves and archives all valid reports.
     * @return the number of reports saved.
     */
    protected abstract int saveReports() throws MessagingException, IOException;

    /**
     * Saves a single email Message to the given File.
     */
    protected abstract void saveMessage(Message message, File file) throws MessagingException, IOException;
}
