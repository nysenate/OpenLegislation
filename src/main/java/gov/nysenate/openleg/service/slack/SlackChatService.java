package gov.nysenate.openleg.service.slack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SlackChatService {

    private static final Logger logger = LoggerFactory.getLogger(SlackChatService.class);

    private static final Pattern slackAddressPattern = Pattern.compile("^#([a-z]+)(?:@([a-z]*))?$");

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private SlackApi slackApi;

    @PostConstruct
    public void init() {
        try {
            slackApi = new SlackApi(webhookUrl);
        } catch (IllegalArgumentException ex) {
            slackApi = null;
            logger.error("Invalid Slack webhook URL!  Slack messages will NOT be sent:\n" + ex.getMessage());
        }
    }

    /**
     * Sends a simple string message posting with the default username, icon, and channel
     *  from the webhook integration
     * @param text String
     */
    public void sendMessage(String text) {
        sendMessage(new SlackMessage(text));
    }

    /**
     * Sends a message that mentions the given user names.  Uses the default username, icon and channel
     * @param text String
     * @param mentions Collection<String>
     */
    public void sendMessage(String text, Collection<String> mentions) {
        sendMessage(new SlackMessage(text).setMentions(mentions));
    }

    /**
     * Sends a customized slack message
     * @param message SlackMessage
     */
    public void sendMessage(SlackMessage message) {
        if (slackApi != null) {
            slackApi.call(message);
        }
    }

    /**
     * Sends the given slack message to the given addresses that specify a channel and username
     * Will send one message per channel with all usernames addressed to that channel mentioned
     * @param messageContent SlackMessage
     * @param addresses Collection<String> - a collection of string addresses in the format #(channel name)@(username)
     */
    public void sendMessage(SlackMessage messageContent, Collection<String> addresses) {
        ListMultimap<String, String> channelMentionsMap = getChannelMentionsMap(addresses);
        // Send to default channel with no mentions if there are no valid addresses
        if (channelMentionsMap.isEmpty()) {
            sendMessage(messageContent);
        } else {
            channelMentionsMap.asMap().forEach((channel, mentions) -> {
                SlackMessage message = new SlackMessage(messageContent);
                mentions.stream()
                        .filter(StringUtils::isNotBlank)
                        .forEach(message::addMention);
                sendMessage(message);
            });
        }
    }

    /** --- Internal Methods --- */

    /** Takes a collection of addresses and transforms it into
     * a multimap of channels with all usernames attached to that channel */
    protected ListMultimap<String, String> getChannelMentionsMap(Collection<String> addresses) {
        ListMultimap<String, String> channelMentionsMap = ArrayListMultimap.create();
        addresses.forEach(address -> {
            Matcher addressMatcher = slackAddressPattern.matcher(address);
            if (addressMatcher.matches()) {
                channelMentionsMap.put(addressMatcher.group(1),
                        addressMatcher.groupCount() > 1 ? addressMatcher.group(2) : "");
            }
        });
        return channelMentionsMap;
    }
}
