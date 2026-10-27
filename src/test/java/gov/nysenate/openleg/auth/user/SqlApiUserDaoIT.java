package gov.nysenate.openleg.auth.user;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.auth.user.ApiUserSubscriptionType.BREAKING_CHANGES;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class SqlApiUserDaoIT extends BaseTests {
    @Autowired private SqlApiUserDao apiUserDao;

    @Test
    public void onlyVerifiedSubscribersAreListed() {
        ApiUser verified = insert("verified@daoit.example.com", true);
        ApiUser unverified = insert("unverified@daoit.example.com", false);

        Set<String> keys = apiUserDao.getVerifiedUsersWithSubscription(BREAKING_CHANGES).stream()
                .map(ApiUser::getApiKey).collect(Collectors.toSet());

        assertTrue(keys.contains(verified.getApiKey()));
        assertFalse(keys.contains(unverified.getApiKey()));
    }

    private ApiUser insert(String email, boolean verified) {
        ApiUser user = new ApiUser(email);
        user.setName("Name");
        user.setOrganizationName("");
        user.setRegistrationToken(email);
        user.setAuthenticated(verified);
        user.addSubscription(BREAKING_CHANGES);
        apiUserDao.insertUser(user);
        return user;
    }
}
