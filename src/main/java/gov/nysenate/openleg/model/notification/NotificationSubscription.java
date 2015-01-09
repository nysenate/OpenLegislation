package gov.nysenate.openleg.model.notification;

/**
 * Contains notification subscription information of a single user to a single notification type
 */
public class NotificationSubscription {

    /** The username of the subscribed user */
    private String userName;

    /** The type of notification that the user is subscribed to */
    private NotificationType type;

    /** The medium through which the notification is sent */
    private NotificationTarget target;

    /** The user's address for the specified target medium */
    private String targetAddress;

    public NotificationSubscription(String userName, NotificationType type, NotificationTarget target, String targetAddress) {
        this.userName = userName;
        this.type = type;
        this.target = target;
        this.targetAddress = targetAddress;
    }

    /** --- Getters --- */

    public String getUserName() {
        return userName;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationTarget getTarget() {
        return target;
    }

    public String getTargetAddress() {
        return targetAddress;
    }
}
