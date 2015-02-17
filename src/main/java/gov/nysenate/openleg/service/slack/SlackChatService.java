package gov.nysenate.openleg.service.slack;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class SlackChatService {

    private static final Logger logger = LoggerFactory.getLogger(SlackChatService.class);

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
        sendMessage(addMentions(text, mentions));
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
     * Adds slack api formatted user name mentions to the front of a string message
     * @param message String
     * @param mentions Collection<String>
     * @return String - the message with mentions added
     */
    public String addMentions(String message, Collection<String> mentions) {
        String mentionString = mentions.stream()
                .filter(StringUtils::isNotBlank)
                .reduce("", (a, b) -> a + "<@" + b + "> ");
        return mentionString + message;
    }
}
