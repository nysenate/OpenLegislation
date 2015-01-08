package gov.nysenate.openleg.model.notification;

import java.time.LocalDateTime;

/**
 * The data portion of a notification.
 * @see RegisteredNotification
 */
public class Notification {

    /** Designates the type of notification */
    protected NotificationType type;

    /** The date and time when the notification occurred */
    protected LocalDateTime occurred;

    /** A brief summary of the notification */
    protected String summary;

    /** The full message of the notification */
    protected String message;

    /** --- Constructors --- */

    public Notification(NotificationType type, LocalDateTime occurred, String summary, String message) {
        this.type = type;
        this.occurred = occurred;
        this.summary = summary;
        this.message = message;
    }

    public Notification(Notification that) {
        this.type = that.type;
        this.occurred = that.occurred;
        this.summary = that.summary;
        this.message = that.message;
    }

    /** --- Getters --- */

    public NotificationType getType() {
        return type;
    }

    public LocalDateTime getOccurred() {
        return occurred;
    }

    public String getSummary() {
        return summary;
    }

    public String getMessage() {
        return message;
    }
}
