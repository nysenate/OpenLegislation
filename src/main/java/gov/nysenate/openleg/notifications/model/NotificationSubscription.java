package gov.nysenate.openleg.notifications.model;

import com.google.common.base.Objects;

/**
 * Contains notification subscription information of a single user to a single notification type
 */
public class NotificationSubscription {

    /** The unique id that references this subscription. */
    private final Integer id;

    /** The username of the subscribed user */
    private final String userName;

    /** The type of notification that the user is subscribed to */
    private final NotificationType notificationType;

    /** The medium through which the notification is sent */
    private final NotificationMedium medium;

    /** The user's address for the specified target medium */
    private final String targetAddress;

    /** Determines amount of content delivered with notifications.  If false they get just the summary. */
    private final boolean detail;

    /** Flag that prevents notification delivery if false. */
    private final boolean active;

    /* --- Constructor / Builder --- */

    NotificationSubscription(Builder builder) {
        this.id = builder.id;
        this.userName = builder.userName;
        this.notificationType = builder.notificationType;
        this.medium = builder.medium;
        this.targetAddress = builder.targetAddress;
        this.detail = builder.detail;
        this.active = builder.active;
    }

    /**
     * Abstract builder class extensible for use in subclasses.
     *
     * Generic type {@link B} allows base methods to return instance of {@link Builder} implementation.
     */
    public static class Builder<B extends Builder<?>> {
        Integer id;
        String userName;
        NotificationType notificationType;
        NotificationMedium medium;
        String targetAddress;
        boolean detail;
        boolean active;

        public NotificationSubscription build() {
            return new NotificationSubscription(this);
        }

        @SuppressWarnings("unchecked")
        protected final B self() {
            return (B) this;
        }

        public B copy(NotificationSubscription sub) {
            this
                    .setId(sub.id)
                    .setUserName(sub.userName)
                    .setNotificationType(sub.notificationType)
                    .setMedium(sub.medium)
                    .setTargetAddress(sub.targetAddress)
                    .setDetail(sub.detail)
                    .setActive(sub.active);
            return self();
        }

        public B setId(Integer id) {
            this.id = id;
            return self();
        }

        public B setUserName(String userName) {
            this.userName = userName;
            return self();
        }

        public B setNotificationType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return self();
        }

        public B setMedium(NotificationMedium medium) {
            this.medium = medium;
            return self();
        }

        public B setTargetAddress(String targetAddress) {
            this.targetAddress = targetAddress;
            return self();
        }

        public B setDetail(boolean detail) {
            this.detail = detail;
            return self();
        }

        public B setActive(boolean active) {
            this.active = active;
            return self();
        }
    }

    /* --- Abstract Methods --- */

    /**
     * @return {@link Builder} - A builder preset with the values of this subscription.
     */
    public Builder copy() {
        return new Builder().copy(this);
    }

    /**
     * Does this subscription subscribe to the given notification type.
     * @param type
     * @return
     */
    public boolean subscribesTo(NotificationType type) {
        return type == this.notificationType;
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationSubscription)) return false;
        NotificationSubscription that = (NotificationSubscription) o;
        return Objects.equal(id, that.id) &&
                detail == that.detail &&
                active == that.active &&
                Objects.equal(userName, that.userName) &&
                notificationType == that.notificationType &&
                medium == that.medium &&
                Objects.equal(targetAddress, that.targetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, userName, notificationType, medium, targetAddress, detail, active);
    }

    /* --- Getters --- */

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public NotificationMedium getMedium() {
        return medium;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public boolean isDetail() {
        return detail;
    }

    public boolean isActive() {
        return active;
    }
}
