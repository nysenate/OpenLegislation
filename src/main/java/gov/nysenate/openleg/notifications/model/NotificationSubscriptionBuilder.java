package gov.nysenate.openleg.notifications.model;

import java.time.LocalDateTime;

public abstract class NotificationSubscriptionBuilder {
    Integer id;
    String userName;
    NotificationType notificationType;
    NotificationMedium medium;
    String targetAddress;
    LocalDateTime lastSent;
    boolean full;
    boolean active;

    public abstract NotificationSubscription build();

    public NotificationSubscriptionBuilder setId(Integer id) {
        this.id = id;
        return this;
    }

    public NotificationSubscriptionBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public NotificationSubscriptionBuilder setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public NotificationSubscriptionBuilder setMedium(NotificationMedium medium) {
        this.medium = medium;
        return this;
    }

    public NotificationSubscriptionBuilder setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
        return this;
    }

    public NotificationSubscriptionBuilder setLastSent(LocalDateTime lastSent) {
        this.lastSent = lastSent;
        return this;
    }

    public NotificationSubscriptionBuilder setFull(boolean full) {
        this.full = full;
        return this;
    }

    public NotificationSubscriptionBuilder setActive(boolean active) {
        this.active = active;
        return this;
    }

}