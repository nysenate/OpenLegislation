package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.UnsentRecipient;

import java.util.List;

/**
 * How many copies of a batch email were sent, and each recipient who may not have got one, with the status
 * and reason. See {@link BatchEmailResult} for what each status means.
 */
public record BatchEmailResultView(int sent, List<UnsentRecipient> unsent) implements ViewObject {

    public BatchEmailResultView(BatchEmailResult result) {
        this(result.sent(), result.unsent());
    }

    @Override
    public String getViewType() {
        return "batch-email-result";
    }
}
