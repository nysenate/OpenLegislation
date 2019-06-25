package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.service.mail.MailException;

public interface ApiUserBatchEmailService {

    /**
     * Sends zero or more email messages regarding specific email-subscription
     * categories. Emails are only sent to those Api Users whom are subscribed to
     * the given subscriptions (message.subscriptionTypes)
     * @param message ApiUserMessage
     * @return int, the number of emails that were sent
     */
    public int sendMessage(ApiUserMessage message) throws MailException;
}
