package gov.nysenate.openleg.model.notification;

import java.time.LocalDateTime;

/**
 * A notification that has been registered in the data store.
 */
public class RegisteredNotification extends Notification {

    /** a unique arbitrary id assigned to the notification */
    protected int id;

    /** --- Constructors --- */

    public RegisteredNotification(int id, NotificationType type, LocalDateTime occurred, String summary, String message) {
        super(type, occurred, summary, message);
        this.id = id;
    }

    public RegisteredNotification(Notification that, int id) {
        super(that);
        this.id = id;
    }

    /** --- Getters --- */

    public int getId() {
        return id;
    }

}
