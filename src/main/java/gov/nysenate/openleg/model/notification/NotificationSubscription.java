package gov.nysenate.openleg.model.notification;

import com.google.common.base.Objects;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Contains notification subscription information of a single user to a single notification type
 */
public abstract class NotificationSubscription {

    /** The unique id that references this subscription. */
    private Integer id;

    /** The username of the subscribed user */
    private String userName;

    /** The type of notification that the user is subscribed to */
    private NotificationType notificationType;

    /** The medium through which the notification is sent */
    private NotificationMedium medium;

    /** The user's address for the specified target medium */
    private String targetAddress;

    /** Timestamp of the last time the user received notifications based on this subscription. */
    private LocalDateTime lastSent;

    /** Determines amount of content delivered with notifications.  If false they get just the summary. */
    private boolean detail;

    /** Flag that prevents notification delivery if false. */
    private boolean active;

    /* --- Constructor / Builder --- */

    NotificationSubscription(Builder builder) {
        this.id = builder.id;
        this.userName = builder.userName;
        this.notificationType = builder.notificationType;
        this.medium = builder.medium;
        this.targetAddress = builder.targetAddress;
        this.lastSent = builder.lastSent;
        this.detail = builder.detail;
        this.active = builder.active;
    }

    /**
     * Abstract builder class extensible for use in subclasses.
     *
     * Generic type {@link B} allows base methods to return instance of {@link Builder} implementation.
     */
    public static abstract class Builder<B extends Builder> {
        Integer id;
        String userName;
        NotificationType notificationType;
        NotificationMedium medium;
        String targetAddress;
        LocalDateTime lastSent;
        boolean detail;
        boolean active;

        public abstract NotificationSubscription build();

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
                    .setLastSent(sub.lastSent)
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

        public B setLastSent(LocalDateTime lastSent) {
            this.lastSent = lastSent;
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
    public abstract Builder copy();

    /**
     * Compute the next available time for notification dispatch based on {@link #lastSent}
     * @return LocalDateTime
     */
    public abstract LocalDateTime getNextDispatchTime(LocalDateTime from);

    /**
     * Returns true if the notifcation can be dispatched at the given time based on {@link #lastSent}.
     *
     * @see #canDispatchNow() which calls this method and is more practical.
     * This method really only exists for testabililty.
     * @param time LocalDateTime
     * @return boolean
     */
    abstract boolean canDispatch(LocalDateTime time);

    /**
     * Returns true if notifications for this subscription can be dispatched.
     * @return boolean
     */
    public final boolean canDispatchNow() {
        return canDispatch(LocalDateTime.now());
    }

    /**
     * Compute the start time for the next digest to be sent.
     * @return LocalDateTime
     */
    public abstract LocalDateTime getDigestStartTime();

    /**
     * @return {@link NotificationSubscriptionType} for this subscription.
     */
    public abstract NotificationSubscriptionType getSubscriptionType();

    /**
     * Whether or not the subscription should receive digest notifications.
     * @return boolean
     */
    public abstract boolean receivesDigests();

    /**
     * Whether or not a digest should be sent if there are no notifications in the digest period.
     * Defaults to false.
     * @return boolean
     */
    public boolean sendEmpty() {
        return false;
    }

    /**
     * Whether or not received notifications should be sent immediately if possible.
     * Defaults to false.
     * @return boolean
     */
    public boolean sendInstantly() {
        return false;
    }

    /**
     * Get {@link #lastSent} with a default value of {@link gov.nysenate.openleg.util.DateUtils#LONG_AGO} if it is null.
     * @return LocalDateTime
     */
    public final @NonNull LocalDateTime getLastSentSafe() {
        return Optional.ofNullable(lastSent)
                .orElse(DateUtils.LONG_AGO.atStartOfDay());
    }

    public final LocalDateTime getNextDispatchTime() {
        return getNextDispatchTime(LocalDateTime.now());
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
                Objects.equal(targetAddress, that.targetAddress) &&
                Objects.equal(lastSent, that.lastSent);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, userName, notificationType, medium, targetAddress, lastSent, detail, active);
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

    public LocalDateTime getLastSent() {
        return lastSent;
    }

    public boolean isDetail() {
        return detail;
    }

    public boolean isActive() {
        return active;
    }
}
