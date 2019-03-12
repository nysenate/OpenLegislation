package gov.nysenate.openleg.model.notification;

import com.google.common.base.Objects;

import java.time.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

/**
 * A {@link NotificationSubscription} calling for the scheduled dispatch of notification digests
 * on a daily or weekly basis.
 */
public class ScheduledNotificationSubscription extends NotificationSubscription {

    /** Allow dispatch of pending notifications up to this long after their scheduled dispatch */
    static final Duration lateDispatchTolerance = Duration.ofHours(1);

    /** Days of week for dispatch.  Null/empty implies every day. */
    private final EnumSet<DayOfWeek> daysOfWeek;

    /** Time of day for scheduled dispatch. */
    private final LocalTime timeOfDay;

    /** Whether or not scheduled digest should be sent if there are no notifications in the digest period. */
    private final boolean sendEmpty;

    /** Private Constructor for use with builder */
    private ScheduledNotificationSubscription(Builder builder) {
        super(builder);
        this.daysOfWeek = (builder.daysOfWeek == null || builder.daysOfWeek.isEmpty())
                ? EnumSet.allOf(DayOfWeek.class)
                : EnumSet.copyOf(builder.daysOfWeek);
        this.timeOfDay = Optional.ofNullable(builder.timeOfDay)
                .orElse(LocalTime.NOON);
        this.sendEmpty = builder.sendEmpty;
    }

    /**
     * Builder class
     * @see #builder()
     */
    public static class Builder extends NotificationSubscription.Builder<Builder> {

        private Collection<DayOfWeek> daysOfWeek;
        private LocalTime timeOfDay;
        private boolean sendEmpty;

        protected Builder() {}

        @Override
        public ScheduledNotificationSubscription build() {
            return new ScheduledNotificationSubscription(this);
        }

        public Builder copy(ScheduledNotificationSubscription sub) {
            return super.copy(sub)
                    .setDaysOfWeek(sub.daysOfWeek)
                    .setTimeOfDay(sub.timeOfDay)
                    .setSendEmpty(sub.sendEmpty);
        }

        public Builder setDaysOfWeek(Collection<DayOfWeek> daysOfWeek) {
            this.daysOfWeek = EnumSet.copyOf(daysOfWeek);
            return this;
        }

        public Builder setTimeOfDay(LocalTime timeOfDay) {
            this.timeOfDay = timeOfDay;
            return this;
        }

        public Builder setSendEmpty(boolean sendEmpty) {
            this.sendEmpty = sendEmpty;
            return this;
        }
    }

    /** Get an instance of the instant sub builder */
    public static Builder builder() {
        return new Builder();
    }

    /* --- Overrides / Functional Getters --- */

    @Override
    public Builder copy() {
        return builder().copy(this);
    }

    @Override
    public LocalDateTime getNextDispatchTime(LocalDateTime from) {
        LocalDateTime nextTime = LocalDate.now().atTime(timeOfDay);
        while (from.isAfter(nextTime) || !daysOfWeek.contains(nextTime.getDayOfWeek())) {
            nextTime = nextTime.plusDays(1);
        }
        return nextTime;
    }

    @Override
    boolean canDispatch(LocalDateTime time) {
        LocalDateTime lastDispatchTime = getLastDispatchTime(time, true);
        LocalDateTime lastSent = getLastSentSafe();
        // Dispatch only if the last dispatch time is unaccounted for by the last sent time
        // and the last dispatch time is not too long ago
        return lastSent.isBefore(lastDispatchTime) &&
                Duration.between(lastDispatchTime, time).compareTo(lateDispatchTolerance) < 1;
    }

    @Override
    public LocalDateTime getDigestStartTime() {
        LocalDateTime lastSent = getLastSent();
        if (lastSent != null) {
            return lastSent;
        }
        // If last sent is not available (e.g. on the first send),
        // use the last dispatch time prior to the most recent dispatch time
        LocalDateTime latestDispatchTime = getLastDispatchTime(LocalDateTime.now(), true);
        return getLastDispatchTime(latestDispatchTime, false);
    }

    @Override
    public boolean sendEmpty() {
        return sendEmpty;
    }

    @Override
    public NotificationSubscriptionType getSubscriptionType() {
        return NotificationSubscriptionType.SCHEDULED;
    }

    @Override
    public boolean receivesDigests() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledNotificationSubscription)) return false;
        if (!super.equals(o)) return false;
        ScheduledNotificationSubscription that = (ScheduledNotificationSubscription) o;
        return sendEmpty == that.sendEmpty &&
                Objects.equal(daysOfWeek, that.daysOfWeek) &&
                Objects.equal(timeOfDay, that.timeOfDay);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), daysOfWeek, timeOfDay, sendEmpty);
    }

    /**
     * Determines if this subscription repeats on a weekly basis as opposed to daily.
     * @return boolean
     */
    public boolean isWeekly() {
        // If any days are specified, it is weekly
        return daysOfWeek != null && !daysOfWeek.isEmpty();
    }

    /**
     * Get the last scheduled dispatch time before and possibly including the reference time.
     *
     * @param referenceTime LocalDateTime
     * @param inclusive boolean - if true, will return times equal the the reference time
     * @return LocalDateTime
     */
    private LocalDateTime getLastDispatchTime(LocalDateTime referenceTime, boolean inclusive) {
        LocalDateTime lastTime = referenceTime.toLocalDate().atTime(timeOfDay);
        if (inclusive && lastTime.isEqual(referenceTime)) {
            return lastTime;
        }
        while (!lastTime.isBefore(referenceTime) || !daysOfWeek.contains(lastTime.getDayOfWeek())) {
            lastTime = lastTime.minusDays(1);
        }
        return lastTime;
    }

    /* --- Getters --- */

    public EnumSet<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public LocalTime getTimeOfDay() {
        return timeOfDay;
    }

}
