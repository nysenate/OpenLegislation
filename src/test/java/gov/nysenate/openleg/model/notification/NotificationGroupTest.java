package gov.nysenate.openleg.model.notification;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class NotificationGroupTest {

    private static final Duration SIXTY_MIN = Duration.ofMinutes(60);
    private static final LocalDateTime TODAY = LocalDate.now().atStartOfDay();
    private static final NotificationType NOTIFICATION_TYPE = NotificationType.PROCESS_EXCEPTION;

    @Test(expected = NullPointerException.class)
    public void givenNullNotificationType_thenExceptionThrown() {
        new NotificationGroup(null, SIXTY_MIN);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullDuration_thenExceptionThrown() {
        new NotificationGroup(NOTIFICATION_TYPE, null);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFirstSeenDateTime_thenExceptionThrown() {
        new NotificationGroup(NOTIFICATION_TYPE, null, LocalDateTime.now(), 0, SIXTY_MIN);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullLastSeenDateTime_thenExceptionThrown() {
        new NotificationGroup(NOTIFICATION_TYPE, LocalDateTime.now(), null, 0, SIXTY_MIN);
    }

    @Test
    public void testCountsThatShouldBeSent() {
        assertFalse(shouldSendNotificationWithCount(0));
        assertFalse(shouldSendNotificationWithCount(3));
        assertFalse(shouldSendNotificationWithCount(6));
        assertFalse(shouldSendNotificationWithCount(101));
        assertFalse(shouldSendNotificationWithCount(283176));

        assertTrue(shouldSendNotificationWithCount(1));
        assertTrue(shouldSendNotificationWithCount(2));
        assertTrue(shouldSendNotificationWithCount(4));
        assertTrue(shouldSendNotificationWithCount(8));
        assertTrue(shouldSendNotificationWithCount(16));
        assertTrue(shouldSendNotificationWithCount(128));
        assertTrue(shouldSendNotificationWithCount(1024));
        assertTrue(shouldSendNotificationWithCount(262144));
        // Counts over 1 mil should always be sent.
        assertTrue(shouldSendNotificationWithCount(1000001));
    }

    @Test
    public void givenNotificationsReoccurringLessThanPeriodApart_thenHandledAsTheSameException() {
        NotificationGroup group = new NotificationGroup(NOTIFICATION_TYPE, SIXTY_MIN);
        assertFalse(group.shouldSendNotification());

        // First instance of a notification should be sent.
        Notification newNotification = new Notification(NOTIFICATION_TYPE, TODAY, "", "");
        group.registerNotification(newNotification);
        assertTrue(group.shouldSendNotification());

        // 2nd instance 5 min later should be sent
        newNotification = new Notification(NOTIFICATION_TYPE, TODAY.plusMinutes(5), "", "");
        group.registerNotification(newNotification);
        assertTrue(group.shouldSendNotification());

        // 3rd instance 5 min later should not be sent
        newNotification = new Notification(NOTIFICATION_TYPE, TODAY.plusMinutes(10), "", "");
        group.registerNotification(newNotification);
        assertFalse(group.shouldSendNotification());

        // 4th instance 5 min later should be sent
        newNotification = new Notification(NOTIFICATION_TYPE, TODAY.plusMinutes(15), "", "");
        group.registerNotification(newNotification);
        assertTrue(group.shouldSendNotification());
    }

    @Test
    public void givenMoreThanPeriodBetweenExceptions_thenExceptionFixed() {
        NotificationGroup group = new NotificationGroup(NOTIFICATION_TYPE, SIXTY_MIN);
        assertFalse(group.shouldSendNotification());

        // First instance of a notification should be sent.
        Notification newNotification = new Notification(NOTIFICATION_TYPE, TODAY, "", "");
        group.registerNotification(newNotification);
        assertTrue(group.shouldSendNotification());

        // 2nd instance 5 min later should be sent
        newNotification = new Notification(NOTIFICATION_TYPE, TODAY.plusMinutes(5), "", "");
        group.registerNotification(newNotification);
        assertTrue(group.shouldSendNotification());

        // A 3rd instance 2 hours later
        // Normally, a 3rd instance would not be sent because shouldSendNotification will return false.
        // However, this exception occurs 2 hours later which is more than the specified period.
        // Therefore the previous exception is considered fixed and this is treated as a new exception.
        newNotification = new Notification(NOTIFICATION_TYPE, TODAY.plusMinutes(120), "", "");
        group.registerNotification(newNotification);
        assertTrue(group.shouldSendNotification());
    }

    private boolean shouldSendNotificationWithCount(int count) {
        NotificationGroup group = new NotificationGroup(NOTIFICATION_TYPE,
                LocalDateTime.now(), LocalDateTime.now(), count, SIXTY_MIN);
        return group.shouldSendNotification();
    }
}
