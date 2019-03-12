package gov.nysenate.openleg.model.notification;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.*;
import java.util.EnumSet;

import static gov.nysenate.openleg.model.notification.ScheduledNotificationSubscription.lateDispatchTolerance;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class ScheduledNotificationSubscriptionTest {

    private static final LocalDate lastTuesday;

    static {
        LocalDate day = LocalDate.now();
        while (day.getDayOfWeek() != DayOfWeek.TUESDAY) {
            day = day.minusDays(1);
        }
        lastTuesday = day;
    }

    private static final ScheduledNotificationSubscription schedSub = ScheduledNotificationSubscription.builder()
            .setUserName("AnnaNim")
            .setMedium(NotificationMedium.SLACK)
            .setTargetAddress("@AnnaNimMouse")
            .setDetail(true)
            .setActive(true)
            .setNotificationType(NotificationType.SCRAPING_EXCEPTION)
            .setDaysOfWeek(EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY))
            .setTimeOfDay(LocalTime.NOON)
            .setLastSent(lastTuesday.atTime(LocalTime.NOON))
            .build();

    @Test
    public void canDispatch() {
        LocalDate nextFriday = lastTuesday.plusDays(3);
        LocalDateTime nextFridayNoon = nextFriday.atTime(LocalTime.NOON);
        assertTrue(schedSub.canDispatch(nextFridayNoon));
        assertFalse(schedSub.canDispatch(nextFridayNoon.minusNanos(1)));

        LocalDateTime nextFridayNoonLate = nextFridayNoon.plus(lateDispatchTolerance);
        assertTrue(schedSub.canDispatch(nextFridayNoonLate));
        assertFalse(schedSub.canDispatch(nextFridayNoonLate.plusNanos(1)));

        LocalTime minsToMidnight = LocalTime.MIDNIGHT.minusMinutes(1);
        LocalDateTime nextFridayLate = nextFriday.atTime(minsToMidnight);
        LocalDateTime satMorning = nextFriday.plusDays(1).atStartOfDay();
        ScheduledNotificationSubscription lateNightSub = ScheduledNotificationSubscription.builder()
                .copy(schedSub)
                .setTimeOfDay(minsToMidnight)
                .setLastSent(lastTuesday.atTime(minsToMidnight))
                .build();
        assertTrue(lateNightSub.canDispatch(nextFridayLate));
        assertEquals(-1, Duration.between(nextFridayLate, satMorning).compareTo(lateDispatchTolerance));
        assertTrue(lateNightSub.canDispatch(nextFriday.plusDays(1).atStartOfDay()));
        assertFalse(lateNightSub.canDispatch(nextFridayLate.plus(lateDispatchTolerance).plusNanos(1)));
    }

    @Test
    public void getDigestStartTime() {
        assertEquals(lastTuesday.atTime(LocalTime.NOON), schedSub.getDigestStartTime());

        final ScheduledNotificationSubscription newSchedSub = ScheduledNotificationSubscription.builder()
                .copy(schedSub)
                .setLastSent(null)
                .build();

        LocalDateTime lastScheduled = LocalDate.now().atTime(LocalTime.NOON);
        assertFalse(newSchedSub.getDaysOfWeek().isEmpty());
        while (LocalDateTime.now().isBefore(lastScheduled) ||
                !newSchedSub.getDaysOfWeek().contains(lastScheduled.getDayOfWeek())) {
            lastScheduled = lastScheduled.minusDays(1);
        }
        lastScheduled = lastScheduled.minusDays(1);
        while (!newSchedSub.getDaysOfWeek().contains(lastScheduled.getDayOfWeek())) {
            lastScheduled = lastScheduled.minusDays(1);
        }
        assertEquals(lastScheduled, newSchedSub.getDigestStartTime());
    }
}