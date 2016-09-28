package gov.nysenate.openleg.model.slack;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains data for routing a message within a single Slack team
 * Allows for sending a message to a specific channel, mentioning a specific user
 */
public class SlackAddress {

    /** String used to categorize assignments to the default channel,
     *  should be an invalid slack channel name to prevent collision */
    public static final String DEFAULT_CHANNEL_ID =
            "=== THIS IS NOT A VALID SLACK CHANNEL NAME ===";

    private String channel;
    private String username;

    /* -- Constructors -- */

    public SlackAddress(String channel, String username) {
        this.channel = channel;
        this.username = username;
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlackAddress)) return false;
        SlackAddress that = (SlackAddress) o;
        return Objects.equal(channel, that.channel) &&
                Objects.equal(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel, username);
    }

    @Override
    public String toString() {
        if (DEFAULT_CHANNEL_ID.equals(channel) && StringUtils.isBlank(username)) {
            return "default";
        }
        String channelString = channel.equals(DEFAULT_CHANNEL_ID) ? "" : "#" + channel;
        String usernameString = StringUtils.isBlank(username) ? "" : "@" + username;
        return channelString + usernameString;
    }

    /* --- Getters --- */

    public SlackAddress(String channel) {
        this(channel, "");
    }

    public SlackAddress() {
        this(DEFAULT_CHANNEL_ID);
    }

    public String getChannel() {
        return channel;
    }

    public String getUsername() {
        return username;
    }
}

