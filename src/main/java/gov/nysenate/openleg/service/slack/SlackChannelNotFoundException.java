package gov.nysenate.openleg.service.slack;

/**
 * An exception that is raised when an attempt is made to post a slack message to an invalid channel
 */
public class SlackChannelNotFoundException extends SlackApiException {

    private static final long serialVersionUID = 8178877670168732120L;

    private String channelName;

    public SlackChannelNotFoundException(String channelName, String message) {
        super("Could not post a slack message to channel: " + channelName);
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }
}
