package gov.nysenate.openleg.service.notification.data;

public class NotificationNotFoundException extends RuntimeException {

    long id;

    NotificationNotFoundException(long id) {
        super(String.format("Could not find notfication with id: %d", id));
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
