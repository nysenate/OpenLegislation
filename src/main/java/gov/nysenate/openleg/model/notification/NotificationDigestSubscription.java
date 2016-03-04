package gov.nysenate.openleg.model.notification;

import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;

/** A subscription to a notification digest will periodically send all notifications of a particular type
 * that occurred within the period
 */
public class NotificationDigestSubscription extends NotificationSubscription {

    /** The digest subscription's unique id */
    private int id;

    /** Hours/minutes/seconds in the digest period*/
    private Duration periodHours;

    /** Days in the digest period (accounts for DST) */
    private Period periodDays;

    /** The date and time that the next digest will be sent */
    private LocalDateTime nextDigest;

    /** If set to true, the subscriber will receive an empty digest if no relevant notifications occurred during a period */
    private boolean sendEmptyDigest;

    /** If set to true, the subscriber will receive full notification messages for each notifications */
    private boolean full;

    /** Set everything */
    public NotificationDigestSubscription(int id, String userName, NotificationType type,
                                          NotificationTarget target, String targetAddress,
                                          Duration periodHours, Period periodDays, LocalDateTime nextDigest,
                                          boolean sendEmptyDigest, boolean full) {
        super(userName, type, target, targetAddress);
        this.id = id;
        this.periodHours = periodHours;
        this.periodDays = periodDays;
        this.nextDigest = nextDigest;
        this.sendEmptyDigest = sendEmptyDigest;
        this.full = full;
    }

    /** Set everything but id.  Used on initial creation */
    public NotificationDigestSubscription(String userName, NotificationType type,
                                          NotificationTarget target, String targetAddress,
                                          Duration periodHours, Period periodDays, LocalDateTime nextDigest,
                                          boolean sendEmptyDigest, boolean full) {
        this(-1, userName, type, target, targetAddress, periodHours, periodDays, nextDigest, sendEmptyDigest, full);
    }

    /** Set everything using a NotificationSubscription.  Used when mapping db query results */
    public NotificationDigestSubscription(NotificationSubscription subscription, int id,
                                          Duration periodHours, Period periodDays, LocalDateTime nextDigest,
                                          boolean sendEmptyDigest, boolean full) {
        this(id, subscription.getUserName(), subscription.getType(),
                subscription.getTarget(), subscription.getTargetAddress(),
                periodHours, periodDays, nextDigest, sendEmptyDigest, full);
    }

    /** --- Functional Getters / Setters --- */

    /** if the Period is zero, use the Duration, otherwise use the Period
     * @see Period
     * @see Duration
     * @return TemporalAmount
     */
    public TemporalAmount getPeriod() {
        return periodDays.isZero() ? periodHours : periodDays;
    }

    public LocalDateTime getStartDateTime() {
        return nextDigest.minus(getPeriod());
    }

    public LocalDateTime getNewNextDigest() {
        LocalDateTime nextNextDigest = LocalDateTime.from(nextDigest);
        if (nextNextDigest.plus(getPeriod()).isBefore(nextNextDigest) ||
                nextNextDigest.plus(getPeriod()).equals(nextNextDigest)) {
            return DateUtils.THE_FUTURE.atStartOfDay();
        }
        while (nextNextDigest.isBefore(LocalDateTime.now())) {
            nextNextDigest = nextNextDigest.plus(getPeriod());
        }
        return nextNextDigest;
    }

    /** --- Overridden Methods --- */

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("periodHours", periodHours)
                .append("periodDays", periodDays)
                .append("nextDigest", nextDigest)
                .append("sendEmptyDigest", sendEmptyDigest)
                .append("full", full)
                .toString();
    }

    /** --- Getters / Setters --- */

    public Duration getPeriodHours() {
        return periodHours;
    }

    public Period getPeriodDays() {
        return periodDays;
    }

    public LocalDateTime getNextDigest() {
        return nextDigest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSendEmptyDigest() {
        return sendEmptyDigest;
    }

    public boolean isFull() {
        return full;
    }
}
