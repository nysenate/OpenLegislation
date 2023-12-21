package gov.nysenate.openleg.auth.user;

import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.common.dao.ImmutableParams;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class SqlApiUserDao extends SqlBaseDao {
    /**
     * Insert a new user into the database
     * @param user The new apiuser
     * @throws org.springframework.dao.DataAccessException
     */
    void insertUser (ApiUser user) throws DataAccessException {
        if (jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user)) == 0) {
            jdbcNamed.update(ApiUserQuery.INSERT_API_USER.getSql(schema()), getUserParams(user));
        }
        setSubscriptions(user.getApiKey(), user.getSubscriptions());
        revokeRoles(user.getApiKey());
        for (OpenLegRole role : user.getGrantedRoles()) {
            grantRole(user.getApiKey(), role);
        }
    }

    /**
     * Update a preexisting user
     * @param user The APIUser to update
     * @throws DataAccessException
     */
     void updateUser(ApiUser user) throws DataAccessException {
        jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user));
         setSubscriptions(user.getApiKey(), user.getSubscriptions());
         revokeRoles(user.getApiKey());
         for (OpenLegRole role : user.getGrantedRoles()) {
             grantRole(user.getApiKey(), role);
         }
    }

    /**
     * @return List<ApiUser> a list of all api users
     * @throws DataAccessException
     */
    List<ApiUser> getAllUsers() throws DataAccessException {
        return jdbcNamed.query(ApiUserQuery.SELECT_API_USERS.getSql(schema()), new MapSqlParameterSource(),
                new ApiUserRowMapper());
    }

    /**
     * Finds the user with the specified email address
     * @param email The email address of the user you are looking for
     * @return The ApiUser
     * @throws org.springframework.dao.DataAccessException
     */
    ApiUser getApiUserFromEmail(String email) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params, new ApiUserRowMapper());
    }

    /**
     * Finds the user with the specified key
     * @param key The User's API key
     * @return The ApiUser
     */
    ApiUser getApiUserFromKey(String key) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params, new ApiUserRowMapper());
    }

    /**
     * Finds the user with the specified registration token
     * @param token The registration token for the user
     * @return A user if the token is valid
     */
    ApiUser getApiUserFromToken(String token) {
        MapSqlParameterSource params = new MapSqlParameterSource("registrationToken", token);
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_TOKEN.getSql(schema()), params, new ApiUserRowMapper());
    }

    /**
     * Gets all users with the given subscription
     * @param subscriptionType ApiUserSubscriptionType
     * @return A list of ApiUsers
     */
    List<ApiUser> getUsersWithSubscription(ApiUserSubscriptionType subscriptionType) {
        return jdbcNamed.query(ApiUserQuery.SELECT_API_USERS_BY_SUBSCRIPTION.getSql(schema()),
                new MapSqlParameterSource().addValue("subscription_type", subscriptionType.name()), new ApiUserRowMapper());
    }

    /**
     * Adds an e-mail subscription for an api user
     * @param subscription ApiUserSubscriptionType
     */
    private void addSubscription(String apiKey, ApiUserSubscriptionType subscription) {
        jdbcNamed.update(ApiUserQuery.INSERT_API_USER_SUBSCRIPTION.getSql(schema()),
                getSubscriptionParams(apiKey, subscription));
    }

    /**
     * Removes all current subscriptions for a user and
     * adds the subscriptions in the set parameter
     * @param apiKey String
     * @param subscriptions Set<ApiUserSubscriptionType>
     */
    private void setSubscriptions(String apiKey, Set<ApiUserSubscriptionType> subscriptions) {
        //delete existing subscriptions
        jdbcNamed.update(ApiUserQuery.DELETE_ALL_API_USER_SUBSCRIPTIONS.getSql(schema()),
                new MapSqlParameterSource().addValue("apiKey", apiKey));
        //add the new subscriptions
        for(ApiUserSubscriptionType sub : subscriptions) {
            addSubscription(apiKey, sub);
        }
    }

    private void grantRole(String apiKey, OpenLegRole role) {
        try {
            jdbcNamed.update(ApiUserQuery.INSERT_API_USER_ROLE.getSql(schema()), getRoleParams(apiKey, role));
        } catch (DuplicateKeyException ignored) {}
    }

    /**
     * Revokes all roles for an API user.
     */
    private void revokeRoles(String apiKey) {
        jdbcNamed.update(ApiUserQuery.DELETE_API_USER_ROLE.getSql(schema()), new MapSqlParameterSource("apiKey", apiKey));
    }

    /** --- Internal Methods --- */

    private static MapSqlParameterSource getUserParams(ApiUser user) {
        return new MapSqlParameterSource()
                .addValue("apikey", user.getApiKey())
                .addValue("authenticated", user.isAuthenticated())
                .addValue("apiRequests", user.getNumApiRequests())
                .addValue("email", user.getEmail())
                .addValue("name", user.getName())
                .addValue("organizationName", user.getOrganizationName())
                .addValue("registrationToken", user.getRegistrationToken());
    }

    private static MapSqlParameterSource getRoleParams(String apiKey, OpenLegRole role) {
        return new MapSqlParameterSource()
                .addValue("apiKey", apiKey)
                .addValue("role", role.name());
    }

    private static MapSqlParameterSource getSubscriptionParams(String apiKey, ApiUserSubscriptionType subscription) {
        return new MapSqlParameterSource()
                .addValue("apiKey", apiKey)
                .addValue("subscription_type", subscription.name());
    }
}
