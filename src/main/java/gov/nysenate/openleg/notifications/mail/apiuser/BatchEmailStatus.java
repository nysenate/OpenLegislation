package gov.nysenate.openleg.notifications.mail.apiuser;

import jakarta.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * A batch email being sent in the background, or the last one sent.
 * @param subject the email's subject
 * @param recipients the number of recipients, including any not yet reached
 * @param result the copies sent so far, and each recipient so far who may not have got one
 * @param started when sending began
 * @param finished when sending ended, or null while the batch is being sent
 */
public record BatchEmailStatus(String subject, int recipients, BatchEmailResult result, LocalDateTime started,
                               @Nullable LocalDateTime finished) {

    public boolean running() {
        return finished == null;
    }
}
