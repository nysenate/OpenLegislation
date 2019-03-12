package gov.nysenate.openleg.model.notification;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class InstantNotificationSubscriptionTest {

    private static final Duration refRate = Duration.ofMillis(999);

    private static final LocalDateTime refTime = Year.of(2019).atDay(1).atStartOfDay();

    private static final InstantNotificationSubscription newInstantSub = InstantNotificationSubscription.builder()
            .setUserName("JebediahFakeName")
            .setDetail(true)
            .setActive(true)
            .setMedium(NotificationMedium.EMAIL_SIMPLE)
            .setTargetAddress("jebediah@fake.com")
            .setNotificationType(NotificationType.LRS_OUTAGE)
            .build();

    private static final InstantNotificationSubscription establishedInstantSub = InstantNotificationSubscription.builder()
            .copy(newInstantSub)
            .setLastSent(refTime)
            .build();

    private static final InstantNotificationSubscription instantSubLimited = InstantNotificationSubscription.builder()
            .copy(newInstantSub)
            .setRateLimit(refRate)
            .setLastSent(refTime)
            .build();

    @Test
    public void canDispatch() {
        assertTrue(newInstantSub.canDispatch(refTime));
        assertTrue(establishedInstantSub.canDispatch(refTime));
        assertFalse(instantSubLimited.canDispatch(refTime));
        assertTrue(instantSubLimited.canDispatch(refTime.plus(refRate).plusNanos(1)));
    }

    @Test
    public void getDigestStartTime() {
        LocalDateTime testStart = LocalDateTime.now();
        assertEquals(refTime, establishedInstantSub.getDigestStartTime());
        assertEquals(refTime, instantSubLimited.getDigestStartTime());
        LocalDateTime newDStartTime = newInstantSub.getDigestStartTime();
        assertFalse(newDStartTime.isBefore(testStart));
        assertFalse(newDStartTime.isAfter(LocalDateTime.now()));
    }
}