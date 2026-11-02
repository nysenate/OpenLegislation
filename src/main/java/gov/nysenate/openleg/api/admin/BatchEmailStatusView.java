package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.UnsentRecipient;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A batch email being sent, or the last one sent: how many of its recipients there are, how many copies were sent
 * so far, and each recipient so far who may not have got one, with the status and reason. See
 * {@link BatchEmailResult} for what each status means.
 */
public record BatchEmailStatusView(String subject, int recipients, int sent, List<UnsentRecipient> unsent,
                                   boolean running, LocalDateTime started, LocalDateTime finished)
        implements ViewObject {

    public BatchEmailStatusView(BatchEmailStatus status) {
        this(status.subject(), status.recipients(), status.result().sent(), status.result().unsent(),
                status.running(), status.started(), status.finished());
    }

    @Override
    public String getViewType() {
        return "batch-email-status";
    }
}
