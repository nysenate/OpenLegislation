package gov.nysenate.openleg.model.notification;

import java.time.LocalDateTime;

/**
 * A notification that has been registered in the data store.
 */
public class RegisteredNotification extends Notification {

    /** a unique arbitrary id assigned to the notification */
    protected long id;

    /** --- Constructors --- */

    public RegisteredNotification(long id, NotificationType type, LocalDateTime occurred, String summary, String message) {
        super(type, occurred, summary, message);
        this.id = id;
    }

    public RegisteredNotification(Notification that, long id) {
        super(that);
        this.id = id;
    }

    /** --- Getters --- */

    public long getId() {
        return id;
    }

}
