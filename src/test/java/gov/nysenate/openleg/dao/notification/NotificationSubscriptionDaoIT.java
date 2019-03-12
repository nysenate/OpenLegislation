package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import gov.nysenate.openleg.model.notification.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class NotificationSubscriptionDaoIT extends BaseTests {

    @Autowired private NotificationSubscriptionDao subDao;
    @Autowired private AdminUserDao adminUserDao;

    private static final String fakeName1 = "JebediahFakeName";
    private static final String fakeName2 = "AnnaNim";

    private static final InstantNotificationSubscription instantSub = InstantNotificationSubscription.builder()
            .setUserName(fakeName1)
            .setDetail(true)
            .setActive(true)
            .setMedium(NotificationMedium.EMAIL_SIMPLE)
            .setTargetAddress("jebediah@fake.com")
            .setNotificationType(NotificationType.LRS_OUTAGE)
            .setRateLimit(Duration.ofMillis(999))
            .build();

    private static final ScheduledNotificationSubscription schedSub = ScheduledNotificationSubscription.builder()
            .copy(instantSub)
            .setUserName(fakeName2)
            .setMedium(NotificationMedium.SLACK)
            .setTargetAddress("@AnnaNimMouse")
            .setDetail(false)
            .setNotificationType(NotificationType.SCRAPING_EXCEPTION)
            .setDaysOfWeek(EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY))
            .build();

    private static NotificationSubscription normalizeId(NotificationSubscription sub) {
        return sub.copy().setId(0).build();
    }

    private static boolean equalsIgnoreId(NotificationSubscription a, NotificationSubscription b) {
        return Objects.equals(normalizeId(a), normalizeId(b));
    }

    /** Returns the number of the given notification present in the data store */
    private long numPresent(NotificationSubscription sub) {
        return subDao.getSubscriptions().stream()
                .filter(rSub -> equalsIgnoreId(sub, rSub))
                .count();
    }

    @Before
    public void setUp() {
        // Ensure fake admin accounts are set up
        adminUserDao.addAdmin(new AdminUser(fakeName1, "pword", true, false));
        adminUserDao.addAdmin(new AdminUser(fakeName2, "pword", true, false));
    }

    @Test
    public void crudTest() {
        // Subs not present at start
        assertEquals(0, numPresent(instantSub));
        assertEquals(0, numPresent(schedSub));

        // Subs should be present as they are updated
        subDao.updateSubscription(instantSub);

        assertEquals(1, numPresent(instantSub));
        assertEquals(0, numPresent(schedSub));

        NotificationSubscription idedSchedSub = subDao.updateSubscription(schedSub);

        assertEquals(1, numPresent(instantSub));
        assertEquals(1, numPresent(schedSub));

        // Test update behavior
        final ScheduledNotificationSubscription newSchedSub = ScheduledNotificationSubscription.builder()
                .copy(schedSub)
                .setDaysOfWeek(EnumSet.of(DayOfWeek.SATURDAY))
                .setId(idedSchedSub.getId())
                .build();

        subDao.updateSubscription(newSchedSub);

        assertEquals(1, numPresent(instantSub));
        assertEquals(0, numPresent(schedSub));
        assertEquals(1, numPresent(newSchedSub));

        // Test insert of sub with identical data
        subDao.updateSubscription(instantSub);
        assertEquals(2, numPresent(instantSub));

        // Change subscription type
        final InstantNotificationSubscription schedSubToInstant = InstantNotificationSubscription.builder()
                .copy(newSchedSub)
                .setRateLimit(Duration.ZERO)
                .build();
        subDao.updateSubscription(schedSubToInstant);

        assertEquals(2, numPresent(instantSub));
        assertEquals(0, numPresent(schedSub));
        assertEquals(0, numPresent(newSchedSub));
        assertEquals(1, numPresent(schedSubToInstant));

        // Test removal
        subDao.removeSubscription(schedSubToInstant.getId());
        assertEquals(0, numPresent(schedSubToInstant));
    }

    @Test
    public void lastSentTest() {
        LocalDateTime refDateTime = LocalDateTime.now();
        InstantNotificationSubscription initialSub = InstantNotificationSubscription.builder()
                .copy(instantSub)
                .setLastSent(refDateTime)
                .build();
        assertEquals(0, numPresent(initialSub));
        int subId = subDao.updateSubscription(initialSub).getId();
        assertEquals(1, numPresent(initialSub));

        LocalDateTime nextDateTime = refDateTime.plusHours(3);
        InstantNotificationSubscription timeChanged = InstantNotificationSubscription.builder()
                .copy(initialSub)
                .setId(subId)
                .setLastSent(nextDateTime)
                .build();

        subDao.setLastSent(subId, nextDateTime);
        assertEquals(0, numPresent(initialSub));
        assertEquals(1, numPresent(timeChanged));
    }

    @Test
    public void getByIdTest() {
        NotificationSubscription insertedInstant = subDao.updateSubscription(instantSub);
        NotificationSubscription insertedSched = subDao.updateSubscription(schedSub);
        assertEquals(insertedInstant, subDao.getSubscription(insertedInstant.getId()));
        assertEquals(insertedSched, subDao.getSubscription(insertedSched.getId()));
    }

    @Test(expected = SubscriptionNotFoundEx.class)
    public void getByIdTestNotFound() {
        int maxId = subDao.getSubscriptions().stream()
                .map(NotificationSubscription::getId)
                .max(Integer::compareTo)
                .orElse(1336);
        int nonExistentId = maxId + 1;
        subDao.getSubscription(nonExistentId);
    }

}