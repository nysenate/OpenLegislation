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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Category(IntegrationTest.class)
public class NotificationSubscriptionDataServiceIT extends BaseTests {

    @Autowired private NotificationSubscriptionDataService subDataService;
    @Autowired private AdminUserDao adminUserDao;

    private static final String fakeName1 = "JebediahFakeName";
    private static final String fakeName2 = "AnnaNim";

    private static final NotificationSubscription instantSub = new NotificationSubscription.Builder()
            .setUserName(fakeName1)
            .setDetail(true)
            .setActive(true)
            .setMedium(NotificationMedium.EMAIL_SIMPLE)
            .setTargetAddress("jebediah@fake.com")
            .setNotificationType(NotificationType.LRS_OUTAGE)
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

        // Subs should be present as they are updated
        subDataService.updateSubscription(instantSub);

        allSubs = subDataService.getAllSubscriptions();
        assertTrue(isPresent(instantSub, allSubs));

        // Test insert of sub with identical data
        subDataService.updateSubscription(instantSub);
        allSubs = subDataService.getAllSubscriptions();
        assertEquals(2, numPresent(instantSub, allSubs));
    }

    @Test
    public void getByUserNameTest() {
        subDataService.updateSubscription(instantSub);
        assertTrue(isPresent(instantSub, subDataService.getSubscriptions(fakeName1)));
        assertFalse(isPresent(instantSub, subDataService.getSubscriptions(fakeName2)));
    }

}