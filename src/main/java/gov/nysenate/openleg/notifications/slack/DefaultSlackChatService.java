package gov.nysenate.openleg.notifications.slack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import gov.nysenate.openleg.notifications.slack.model.SlackAddress;
import gov.nysenate.openleg.notifications.slack.model.SlackMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class DefaultSlackChatService implements SlackChatService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSlackChatService.class);

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private SlackApi slackApi;

    @PostConstruct
    public void init() {
        try {
            slackApi = new SlackApi(webhookUrl);
        } catch (IllegalArgumentException ex) {
            slackApi = null;
            logger.error("Invalid Slack webhook URL!  " +
                    "Slack messages will NOT be sent:\n" + ex.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String text) {
        sendMessage(new SlackMessage(text));
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String text, Collection<String> mentions) {
        sendMessage(new SlackMessage(text).setMentions(mentions));
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(SlackMessage message) {
        if (slackApi == null) {
            throw new IllegalStateException("slackApi is null!");
        }
        try {
            slackApi.call(message);
        } catch (SlackChannelNotFoundException ex) {
            logger.warn("Attempt to post slack message to invalid channel: " + ex.getChannelName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(SlackMessage messageContent, Collection<SlackAddress> addresses) {

        // Clear any existing channel or mentions
        messageContent.setChannel(null);
        messageContent.setMentions(null);

        // If there are no addresses, just send the message to default channel and get out of here
        if (addresses.isEmpty()) {
            sendMessage(messageContent);
            return;
        }

        // Map addresses to their channel
        HashMultimap<String, SlackAddress> channelMap =
                HashMultimap.create(Multimaps.index(addresses, SlackAddress::getChannel));

        // Send a message for each channel with appropriate mentions
        for (String channel : channelMap.keySet()) {
            SlackMessage message = new SlackMessage(messageContent);

            // Set channel if it is not default
            if (!SlackAddress.DEFAULT_CHANNEL_ID.equals(channel)) {
                message.setChannel(channel);
            }

            // Set mentions to usernames mapped to channel
            message.setMentions(
                    channelMap.get(channel).stream()
                            .map(SlackAddress::getUsername)
                            .filter(StringUtils::isNotBlank)
                            .toList());

            this.sendMessage(message);
        }
    }

    /* --- Internal Methods --- */

    /** Takes a collection of addresses and transforms it into
     * a multimap of channels with all usernames attached to that channel */
    private ListMultimap<String, String> getChannelMentionsMap(
            Collection<SlackAddress> addresses) {
        ListMultimap<String, String> channelMentionsMap = ArrayListMultimap.create();
        addresses.forEach(address ->
                channelMentionsMap.put(address.getChannel(), address.getUsername()));
        return channelMentionsMap;
    }
}
