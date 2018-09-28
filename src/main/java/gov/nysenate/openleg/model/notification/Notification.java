package gov.nysenate.openleg.model.notification;

import java.time.LocalDateTime;

/**
 * The data portion of a notification.
 * @see RegisteredNotification
 */
public class Notification {

    /** Designates the type of notification */
    protected NotificationType notificationType;

    /** The date and time when the notification occurred */
    protected LocalDateTime occurred;

    /** A brief summary of the notification */
    protected String summary;

    /** The full message of the notification */
    protected String message;

    /** --- Constructors --- */

    public Notification(NotificationType notificationType, LocalDateTime occurred, String summary, String message) {
        this.notificationType = notificationType;
        this.occurred = occurred;
        this.summary = summary;
        this.message = message;
    }

    public Notification(Notification that) {
        this.notificationType = that.notificationType;
        this.occurred = that.occurred;
        this.summary = that.summary;
        this.message = that.message;
    }

    /** --- Getters --- */

    public NotificationType getNotificationType() {
        return notificationType;
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
