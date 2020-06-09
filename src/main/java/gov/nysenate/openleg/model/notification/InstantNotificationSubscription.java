package gov.nysenate.openleg.model.notification;

import com.google.common.base.Objects;
import org.apache.commons.lang3.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A {@link NotificationSubscription} for receiving notifications "instantly", as their triggering events occur.
 *
 * Also includes a rate limit for making notifications less spammy.
 */
public class InstantNotificationSubscription extends NotificationSubscription {

    /** Subscriber should get no more than one notification digest for any period of time this long. */
    private final Duration rateLimit;

    /** Private Constructor for use with builder */
    private InstantNotificationSubscription(Builder builder) {
        super(builder);
        this.rateLimit = Optional.ofNullable(builder.rateLimit)
                .orElse(Duration.ZERO);
    }

    /**
     * Builder class
     * @see #builder()
     */
    public static class Builder extends NotificationSubscription.Builder<Builder> {

        private Duration rateLimit;

        protected Builder() {}

        @Override
        public InstantNotificationSubscription build() {
            return new InstantNotificationSubscription(this);
        }

        public Builder copy(InstantNotificationSubscription sub) {
            return super.copy(sub)
                    .setRateLimit(sub.rateLimit);
        }

        public Builder setRateLimit(Duration rateLimit) {
            this.rateLimit = rateLimit;
            return this;
        }
    }

    /** Get an instance of the instant sub builder */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder copy() {
        return builder().copy(this);
    }

    @Override
    public LocalDateTime getNextDispatchTime(LocalDateTime from) {
        return ObjectUtils.max(from, getLastSentSafe().plus(rateLimit));
    }

    @Override
    boolean canDispatch(LocalDateTime time) {
        Duration fromLastSent = Duration.between(getLastSentSafe(), time);
        return fromLastSent.compareTo(rateLimit) >= 0;
    }

    @Override
    public LocalDateTime getDigestStartTime() {
        return Optional.ofNullable(getLastSent())
                .orElse(LocalDateTime.now());
    }

    @Override
    public NotificationSubscriptionType getSubscriptionType() {
        return NotificationSubscriptionType.INSTANT;
    }

    @Override
    public boolean receivesDigests() {
        // Receive digests only if there is a nonzero rate limit
        return this.rateLimit.compareTo(Duration.ZERO) > 0;
    }

    @Override
    public boolean sentInstantly() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstantNotificationSubscription)) return false;
        if (!super.equals(o)) return false;
        InstantNotificationSubscription that = (InstantNotificationSubscription) o;
        return Objects.equal(rateLimit, that.rateLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), rateLimit);
    }

    public Duration getRateLimit() {
        return rateLimit;
    }
}
