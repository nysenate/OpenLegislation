package gov.nysenate.openleg.notifications.mail;

import jakarta.annotation.Nullable;

/**
 * What is known about the delivery of one message.
 * @param outcome how far the message got
 * @param cause why it was not sent, or null if it was
 */
public record SendResult(Outcome outcome, @Nullable Exception cause) {

    public static final SendResult SENT = new SendResult(Outcome.SENT, null);

    public enum Outcome {
        /** The mail server accepted it. */
        SENT,
        /** Not delivered, for a reason that concerns this message alone: the mail server refused it, as it would
         *  an unknown recipient, or it could not be prepared. */
        REFUSED,
        /** Not delivered, because of the mail server: it stopped replying before the message was sent in full,
         *  or refused it for a reason that applies to every message, such as a refused sender. */
        SERVER_FAILED,
        /** Sent in full, but the mail server never confirmed it, so it may have been delivered. */
        UNCONFIRMED,
        /** Never offered to the mail server, because the connection failed. */
        NOT_CONNECTED,
        /** Never offered to the mail server, because it rejected the credentials. */
        LOGIN_REJECTED
    }
}
