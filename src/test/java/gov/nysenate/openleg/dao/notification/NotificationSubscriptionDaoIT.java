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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class NotificationSubscriptionDaoIT extends BaseTests {

    @Autowired private NotificationSubscriptionDao subDao;
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

        // Subs should be present as they are updated
        subDao.updateSubscription(instantSub);
        assertEquals(1, numPresent(instantSub));

        // Test insert of sub with identical data
        subDao.updateSubscription(instantSub);
        assertEquals(2, numPresent(instantSub));
    }

    @Test
    public void getByIdTest() {
        NotificationSubscription insertedInstant = subDao.updateSubscription(instantSub);
        assertEquals(insertedInstant, subDao.getSubscription(insertedInstant.getId()));
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