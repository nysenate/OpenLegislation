package gov.nysenate.openleg.notifications.subscription;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.NotificationType;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Objects;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@Category(IntegrationTest.class)
public class NotificationSubscriptionDataServiceIT extends BaseTests {

    @Autowired private NotificationSubscriptionDataService subDataService;
    @Autowired private AdminUserService adminUserDao;

    private static final String fakeName1 = "JebediahFakeName@nysenate.gov";
    private static final String fakeName2 = "AnnaNim@nysenate.gov";

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
        adminUserDao.createAdmin(fakeName1, "pword", true, false);
        adminUserDao.createAdmin(fakeName2, "pword", true, false);
    }

    @Test
    public void getByUserNameTest() {
        subDataService.updateSubscription(instantSub);
        assertTrue(isPresent(instantSub, subDataService.getSubscriptions(fakeName1)));
        assertFalse(isPresent(instantSub, subDataService.getSubscriptions(fakeName2)));
    }

}