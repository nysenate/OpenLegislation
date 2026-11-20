package gov.nysenate.openleg.auth.user;

import gov.nysenate.openleg.auth.model.ApiUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

/** Creates and deletes API users for integration tests that don't roll back. */
public final class ApiUserTestSupport {
    private ApiUserTestSupport() {}

    /** Saves a new API user whose key is valid, as activating a registration leaves it. */
    public static ApiUser insertActiveUser(SqlApiUserDao apiUserDao) {
        ApiUser user = new ApiUser("api-user-test-" + UUID.randomUUID() + "@example.com");
        user.setName("API user test");
        user.setRegistrationToken(RandomStringUtils.randomAlphanumeric(32));
        user.setActive(true);
        user.setAuthenticated(true);
        apiUserDao.insertUser(user);
        return user;
    }

    /** Deletes an API user with its roles and subscriptions. */
    public static void deleteUser(JdbcTemplate jdbcTemplate, String apiKey) {
        jdbcTemplate.update("DELETE FROM public.apiuser_roles WHERE apikey = ?", apiKey);
        jdbcTemplate.update("DELETE FROM public.apiuser_subscription WHERE apikey = ?", apiKey);
        jdbcTemplate.update("DELETE FROM public.apiuser WHERE apikey = ?", apiKey);
    }
}
