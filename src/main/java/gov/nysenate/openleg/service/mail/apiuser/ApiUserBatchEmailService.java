package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.service.mail.MailException;

public interface ApiUserBatchEmailService {

    /**
     * Send an email to an admin. This function is called
     * when an admin is using Test Mode on the batch email
     * page and clicks send. The test email will be sent to
     * the admin.
     * @param email String
     * @param message ApiUserMessage
     */
    void sendTestMessage(String email, ApiUserMessage message) throws MailException;

    /**
     * Sends zero or more email messages regarding specific email-subscription
     * categories. Emails are only sent to those Api Users whom are subscribed to
     * the given subscriptions (message.subscriptionTypes)
     * @param message ApiUserMessage
     * @return int, the number of emails that were sent
     */
    int sendMessage(ApiUserMessage message) throws MailException;
}
