package gov.nysenate.openleg.notifications.history;

import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static gov.nysenate.openleg.notifications.model.NotificationType.*;
import static org.junit.Assert.*;

public class SqlNotificationDaoIT extends BaseTests {

    @Autowired private NotificationDao notificationDao;

    /** Far enough in the past that no real notification falls in the range. */
    private static final LocalDateTime start = LocalDateTime.of(1990, 1, 1, 0, 0);
    private static final Range<LocalDateTime> testRange = Range.closedOpen(start, start.plusDays(1));

    private RegisteredNotification register(NotificationType type, int minutesAfterStart, String summary, String message) {
        return notificationDao.registerNotification(
                new Notification(type, start.plusMinutes(minutesAfterStart), summary, message));
    }

    private static List<Long> ids(List<RegisteredNotification> notifications) {
        return notifications.stream().map(RegisteredNotification::getId).toList();
    }

    @Test
    public void registerAndGetTest() {
        RegisteredNotification registered = register(PROCESS_EXCEPTION, 5, "summary", "message");
        RegisteredNotification retrieved = notificationDao.getNotification(registered.getId());
        assertEquals(registered.getId(), retrieved.getId());
        assertEquals(PROCESS_EXCEPTION, retrieved.getNotificationType());
        assertEquals(start.plusMinutes(5), retrieved.getOccurred());
        assertEquals("summary", retrieved.getSummary());
        assertEquals("message", retrieved.getMessage());
    }

    @Test
    public void registerAssignsDistinctIdsTest() {
        RegisteredNotification first = register(PROCESS_EXCEPTION, 0, "a", "a");
        RegisteredNotification second = register(PROCESS_EXCEPTION, 0, "a", "a");
        assertNotEquals(first.getId(), second.getId());
    }

    @Test(expected = NotificationNotFoundException.class)
    public void getNotFoundTest() {
        notificationDao.getNotification(-1);
    }

    @Test
    public void listFiltersAndOrdersTest() {
        RegisteredNotification early = register(PROCESS_EXCEPTION, 10, "early", "");
        RegisteredNotification late = register(PROCESS_EXCEPTION, 20, "late", "");
        RegisteredNotification otherType = register(PROCESS_WARNING, 15, "warning", "");
        register(PROCESS_EXCEPTION, 60 * 24, "at the open end of the range", "");

        PaginatedList<RegisteredNotification> exceptions = notificationDao.getNotifications(
                Set.of(PROCESS_EXCEPTION), testRange, SortOrder.DESC, LimitOffset.ALL);
        assertEquals(List.of(late.getId(), early.getId()), ids(exceptions.results()));
        assertEquals(2, exceptions.total());

        PaginatedList<RegisteredNotification> all = notificationDao.getNotifications(
                EnumSet.allOf(NotificationType.class), testRange, SortOrder.ASC, LimitOffset.ALL);
        assertEquals(List.of(early.getId(), otherType.getId(), late.getId()), ids(all.results()));
    }

    @Test
    public void listPaginatesTest() {
        register(PROCESS_EXCEPTION, 1, "", "");
        RegisteredNotification second = register(PROCESS_EXCEPTION, 2, "", "");
        register(PROCESS_EXCEPTION, 3, "", "");

        PaginatedList<RegisteredNotification> page = notificationDao.getNotifications(
                Set.of(PROCESS_EXCEPTION), testRange, SortOrder.ASC, new LimitOffset(1, 2));
        assertEquals(List.of(second.getId()), ids(page.results()));
        assertEquals(3, page.total());
    }
}
