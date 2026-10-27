package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.notifications.mail.MailException;
import jakarta.annotation.Nullable;

public interface ApiUserBatchEmailService {

    /**
     * Send an email to an admin. This function is called
     * when an admin is using Test Mode on the batch email
     * page and clicks send. The test email will be sent to
     * the admin.
     * @param email String
     * @param message ApiUserMessage
     * @return BatchEmailResult, whether the admin's copy was sent, or why not
     */
    BatchEmailResult sendTestMessage(String email, ApiUserMessage message) throws MailException;

    /**
     * Sends zero or more email messages regarding specific email-subscription
     * categories. Emails are only sent to verified Api Users who are subscribed to
     * one or more of the given subscriptions (message.subscriptionTypes), one copy each.
     * Admins get a copy only as such users; test mode is the way to preview a message.
     * Each recipient's copy is sent separately, so an unusable address or a rejected copy
     * doesn't affect the other recipients.
     * Returns once every copy has been sent or given up on.
     * @param message ApiUserMessage
     * @return BatchEmailResult, the number of emails that were sent and each recipient that wasn't sent one
     * @throws MailException if the mail settings can't send any message; nothing is sent
     * @throws BatchEmailInProgressException if another batch email is still being sent; nothing is sent
     */
    BatchEmailResult sendMessage(ApiUserMessage message) throws MailException;

    /**
     * Starts sending the message as {@link #sendMessage} does, in the background, and returns at once.
     * Its progress can be followed with {@link #getStatus}.
     * @param message ApiUserMessage
     * @return BatchEmailStatus, the batch as sending begins
     * @throws MailException if the mail settings can't send any message; nothing is sent
     * @throws BatchEmailInProgressException if another batch email is still being sent; nothing is sent
     */
    BatchEmailStatus startMessage(ApiUserMessage message) throws MailException;

    /**
     * @return BatchEmailStatus, the batch email being sent, or the last one sent since the server started;
     *         null if there is none. Test emails aren't included.
     */
    @Nullable
    BatchEmailStatus getStatus();
}
