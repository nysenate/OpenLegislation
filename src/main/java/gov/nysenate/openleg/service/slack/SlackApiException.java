package gov.nysenate.openleg.service.slack;

import java.io.IOException;

/**
 * An exception thrown when an error is raised while calling the Slack API
 */
public class SlackApiException extends RuntimeException {

    private static final long serialVersionUID = -4184025881063010964L;

    public SlackApiException(String message) {
        super(message);
    }

    public SlackApiException(String errorMessage, int errorCode) {
        this("Unknown slack api exception! " +
                "error message: " + errorMessage + " error code: " + errorCode);
    }

    public SlackApiException(IOException ex) {
        super("IOException encountered while calling the Slack API", ex);
    }
}
