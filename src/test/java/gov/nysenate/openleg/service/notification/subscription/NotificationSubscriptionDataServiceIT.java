package gov.nysenate.openleg.service.notification.subscription;

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
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import static gov.nysenate.openleg.model.notification.NotificationType.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Category(IntegrationTest.class)
public class NotificationSubscriptionDataServiceIT extends BaseTests {

    @Autowired private NotificationSubscriptionDataService subDataService;
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
    private long numPresent(NotificationSubscription sub, Collection<NotificationSubscription> subscriptions) {
        return subscriptions.stream()
                .filter(rSub -> equalsIgnoreId(sub, rSub))
                .count();
    }

    private boolean isPresent(NotificationSubscription sub, Collection<NotificationSubscription> subscriptions) {
        return numPresent(sub, subscriptions) > 0;
    }

    @Before
    public void setUp() {
        // Ensure fake admin accounts are set up
        adminUserDao.addAdmin(new AdminUser(fakeName1, "pword", true, false));
        adminUserDao.addAdmin(new AdminUser(fakeName2, "pword", true, false));
    }

    @Test
    public void crudTest() {
        Set<NotificationSubscription> allSubs;
        // Subs not present at start
        allSubs = subDataService.getAllSubscriptions();
        assertFalse(isPresent(instantSub, allSubs));
        assertFalse(isPresent(schedSub, allSubs));

        // Subs should be present as they are updated
        subDataService.updateSubscription(instantSub);

        allSubs = subDataService.getAllSubscriptions();
        assertTrue(isPresent(instantSub, allSubs));
        assertFalse(isPresent(schedSub, allSubs));

        NotificationSubscription idedSchedSub = subDataService.updateSubscription(schedSub);

        allSubs = subDataService.getAllSubscriptions();
        assertTrue(isPresent(instantSub, allSubs));
        assertTrue(isPresent(schedSub, allSubs));

        // Test update behavior
        final ScheduledNotificationSubscription newSchedSub = ScheduledNotificationSubscription.builder()
                .copy(schedSub)
                .setDaysOfWeek(EnumSet.of(DayOfWeek.SATURDAY))
                .setId(idedSchedSub.getId())
                .build();

        subDataService.updateSubscription(newSchedSub);

        allSubs = subDataService.getAllSubscriptions();
        assertTrue(isPresent(instantSub, allSubs));
        assertFalse(isPresent(schedSub, allSubs));
        assertTrue(isPresent(newSchedSub, allSubs));

        // Test insert of sub with identical data
        subDataService.updateSubscription(instantSub);
        allSubs = subDataService.getAllSubscriptions();
        assertEquals(2, numPresent(instantSub, allSubs));

        // Change subscription type
        final InstantNotificationSubscription schedSubToInstant = InstantNotificationSubscription.builder()
                .copy(newSchedSub)
                .setRateLimit(Duration.ZERO)
                .build();
        subDataService.updateSubscription(schedSubToInstant);

        allSubs = subDataService.getAllSubscriptions();
        assertEquals(2, numPresent(instantSub, allSubs));
        assertFalse(isPresent(schedSub, allSubs));
        assertFalse(isPresent(newSchedSub, allSubs));
        assertTrue(isPresent(schedSubToInstant, allSubs));

        // Test removal
        subDataService.removeSubscription(schedSubToInstant.getId());
        allSubs = subDataService.getAllSubscriptions();
        assertFalse(isPresent(schedSubToInstant, allSubs));
    }

    @Test
    public void lastSentTest() {
        LocalDateTime refDateTime = LocalDateTime.now();
        InstantNotificationSubscription initialSub = InstantNotificationSubscription.builder()
                .copy(instantSub)
                .setLastSent(refDateTime)
                .build();
        assertFalse(isPresent(initialSub, subDataService.getAllSubscriptions()));
        int subId = subDataService.updateSubscription(initialSub).getId();
        assertTrue(isPresent(initialSub, subDataService.getAllSubscriptions()));

        LocalDateTime nextDateTime = refDateTime.plusHours(3);
        InstantNotificationSubscription timeChanged = InstantNotificationSubscription.builder()
                .copy(initialSub)
                .setId(subId)
                .setLastSent(nextDateTime)
                .build();

        subDataService.setLastSent(subId, nextDateTime);
        assertFalse(isPresent(initialSub, subDataService.getAllSubscriptions()));
        assertTrue(isPresent(timeChanged, subDataService.getAllSubscriptions()));
    }

    @Test
    public void getByUserNameTest() {
        subDataService.updateSubscription(instantSub);
        subDataService.updateSubscription(schedSub);
        assertTrue(isPresent(instantSub, subDataService.getSubscriptions(fakeName1)));
        assertFalse(isPresent(instantSub, subDataService.getSubscriptions(fakeName2)));
        assertFalse(isPresent(schedSub, subDataService.getSubscriptions(fakeName1)));
        assertTrue(isPresent(schedSub, subDataService.getSubscriptions(fakeName2)));
    }

    @Test
    public void getByTypeTest() {
        final NotificationSubscription all = instantSub.copy().setNotificationType(NotificationType.ALL).build();
        final NotificationSubscription warning = schedSub.copy().setNotificationType(WARNING).build();
        final NotificationSubscription reqExcept = schedSub.copy().setNotificationType(REQUEST_EXCEPTION).build();
        final NotificationSubscription daybreak = instantSub.copy().setNotificationType(DAYBREAK_SPOTCHECK).build();
        subDataService.updateSubscription(all);
        subDataService.updateSubscription(warning);
        subDataService.updateSubscription(reqExcept);
        subDataService.updateSubscription(daybreak);

        Set<NotificationSubscription> allSubs = subDataService.getSubscriptions(ALL);
        assertTrue(isPresent(all, allSubs));
        assertFalse(isPresent(warning, allSubs));
        assertFalse(isPresent(reqExcept, allSubs));
        assertFalse(isPresent(daybreak, allSubs));

        Set<NotificationSubscription> pWarningSubs = subDataService.getSubscriptions(PROCESS_WARNING);
        assertTrue(isPresent(all, pWarningSubs));
        assertTrue(isPresent(warning, pWarningSubs));
        assertFalse(isPresent(reqExcept, pWarningSubs));
        assertFalse(isPresent(daybreak, pWarningSubs));

        Set<NotificationSubscription> reqExceptSubs = subDataService.getSubscriptions(REQUEST_EXCEPTION);
        assertTrue(isPresent(all, reqExceptSubs));
        assertFalse(isPresent(warning, reqExceptSubs));
        assertTrue(isPresent(reqExcept, reqExceptSubs));
        assertFalse(isPresent(daybreak, reqExceptSubs));

        Set<NotificationSubscription> daybreakSubs = subDataService.getSubscriptions(DAYBREAK_SPOTCHECK);
        assertTrue(isPresent(all, daybreakSubs));
        assertTrue(isPresent(warning, daybreakSubs));
        assertFalse(isPresent(reqExcept, daybreakSubs));
        assertTrue(isPresent(daybreak, daybreakSubs));
    }
}