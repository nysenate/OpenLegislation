package gov.nysenate.openleg.service.slack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

public class SlackMessage {

    private List<SlackAttachment> attach = null;
    private String channel  = null;
    private String icon     = null;
    private JsonObject slackMessage = new JsonObject();

    private String text     = null;
    private String username = null;
    private List<String> mentions = null;

    public SlackMessage() {

    }

    public SlackMessage(String text) {
        this.text = text;
    }

    public SlackMessage(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public SlackMessage(String channel, String username, String text) {
        this.channel = channel;
        if(username != null) {
            this.username = username;
        }
        this.text = text;
    }

    public SlackMessage(SlackMessage other) {
        this.channel = other.channel;
        this.icon = other.icon;
        this.text = other.text;
        this.username = other.username;
        this.mentions = other.mentions != null ? new ArrayList<>(other.mentions) : null;
        this.attach = other.attach != null ? other.attach.stream()
                .map(SlackAttachment::new)
                .collect(Collectors.toList())
            : null;
    }

    public SlackMessage addAttachments(SlackAttachment attach) {
        if(this.attach == null) {
            this.attach = new ArrayList<SlackAttachment>();
        }
        this.attach.add(attach);

        return this;
    }

    public JsonObject prepare() {
        if(channel != null) {
            slackMessage.addProperty("channel", channel);
        }

        if(username != null) {
            slackMessage.addProperty("username", username);
        }

        if(icon != null) {
            if(icon.contains("http")) {
                slackMessage.addProperty("icon_url", icon);
            } else {
                slackMessage.addProperty("icon_emoji", icon);
            }
        }

        if(text == null) {
            throw new IllegalArgumentException("Missing Text field @ SlackMessage");
        } else {
            slackMessage.addProperty("text", addMentions(text, mentions));
        }

        if(attach != null && attach.size() > 0) {
            slackMessage.add("attachments", this.prepareAttach());
        }

        return slackMessage;
    }

    private JsonArray prepareAttach() {
        JsonArray attachs = new JsonArray();
        for(SlackAttachment attach: this.attach) {
            attachs.add(attach.toJson());
        }

        return attachs;
    }

    public SlackMessage removeAttachment(Integer index) {
        if(this.attach != null) {
            this.attach.remove(index);
        }

        return this;
    }

    public SlackMessage setAttachments(ArrayList<SlackAttachment> attach) {
        this.attach = attach;

        return this;
    }

    public SlackMessage setChannel(String channel) {
        if(channel != null) {
            this.channel = channel;
        }

        return this;
    }

    // http://www.emoji-cheat-sheet.com/
    public SlackMessage setIcon(String icon) {
        if(icon != null) {
            this.icon = icon;
        }

        return this;
    }

    public SlackMessage setText(String message) {
        if(message != null) {
            this.text = message;
        }

        return this;
    }

    public SlackMessage setUsername(String username) {
        if(username != null) {
            this.username = username;
        }

        return this;
    }

    public SlackMessage setMentions(Collection<String> mentions) {
        if (mentions != null) {
            this.mentions = new ArrayList<>(mentions);
        }

        return this;
    }

    public SlackMessage addMention(String mention) {
        if (StringUtils.isNotBlank(mention)) {
            if (mentions == null) {
                mentions = new ArrayList<>();
            }
            mentions.add(mention);
        }

        return this;
    }

    /**
     * Adds slack api formatted user name mentions to the front of a string message
     * @param message String
     * @param mentions Collection<String>
     * @return String - the message with mentions added
     */
    private String addMentions(String message, Collection<String> mentions) {
        if (mentions == null) {
            return message;
        }
        String mentionString = mentions.stream()
                .filter(StringUtils::isNotBlank)
                .reduce("", (a, b) -> a + "<@" + b + "> ");
        return mentionString + (message != null ? "\n" + message : "");
    }
}
