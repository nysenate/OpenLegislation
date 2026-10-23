package gov.nysenate.openleg.notifications.mail.apiuser;

import java.util.List;

/**
 * The outcome of sending a batch email: how many copies were sent, and each recipient who may not have got one.
 * @param sent the number of copies the mail server accepted
 * @param unsent each recipient who may not have got a copy, in the order they were sent to
 */
public record BatchEmailResult(int sent, List<UnsentRecipient> unsent) {

    public enum Status {
        /** The stored address can't be used, so no copy was attempted. */
        SKIPPED,
        /** The mail server refused the copy or stopped replying before it was sent in full, or it could not be
         *  prepared; not delivered. */
        FAILED,
        /** Sent in full but never confirmed by the mail server, so it may have been delivered. */
        UNCONFIRMED,
        /** Never attempted, because the mail server could not be reached, rejected the credentials
         *  or had failed several copies in a row. */
        NOT_SENT
    }

    /**
     * @param email the stored address, which may be malformed or null
     * @param status why the recipient may not have got a copy
     * @param reason a short explanation, such as the mail server's reply
     */
    public record UnsentRecipient(String email, Status status, String reason) {}

    /** The unsent recipients with the given status, in order. */
    public List<UnsentRecipient> unsent(Status status) {
        return unsent.stream().filter(recipient -> recipient.status() == status).toList();
    }

    /** The number of recipients, whether or not they were sent a copy. */
    public int total() {
        return sent + unsent.size();
    }
}
