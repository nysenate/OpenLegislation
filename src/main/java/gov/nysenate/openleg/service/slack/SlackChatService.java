package gov.nysenate.openleg.service.slack;

import gov.nysenate.openleg.model.slack.SlackAddress;
import gov.nysenate.openleg.model.slack.SlackMessage;

import java.util.Collection;

/**
 * Allows for the posting of slack messages via a configured slack webhook integration
 */
public interface SlackChatService {

    /**
     * Sends a simple string message posting with the default username,
     * icon, and channel from the webhook integration
     *
     * @param text String
     */
    void sendMessage(String text);

    /**
     * Sends a message that mentions the given user names.
     * Uses the default username, icon and channel
     *
     * @param text     String
     * @param mentions Collection<String>
     */
    void sendMessage(String text, Collection<String> mentions);

    /**
     * Sends a customized slack message
     *
     * @param message SlackMessage
     */
    void sendMessage(SlackMessage message);

    /**
     * Sends the given slack message to the given address strings.
     * Each address string should specify a channel and username.
     * Will send one message per channel that includes mentions for
     * all users included in addresses that target the channel
     *
     * @param messageContent {@link SlackMessage}
     * @param addresses      Collection<{@link SlackAddress}>
     */
    void sendMessage(SlackMessage messageContent,
                            Collection<SlackAddress> addresses);
}

