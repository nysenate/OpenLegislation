package gov.nysenate.openleg.notifications.mail.apiuser;

import java.io.Serial;

/**
 * Thrown when a batch email is started while another is still being sent; nothing is sent.
 */
public class BatchEmailInProgressException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4196511316235084107L;

    public BatchEmailInProgressException(String subject) {
        super("Batch email \"" + subject + "\" is still being sent. Wait for it to finish before sending another.");
    }
}
