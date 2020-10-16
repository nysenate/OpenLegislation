package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.auth.OpenLegRole;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Set;

/**
 * DAO Interface for retrieving and persisting ApiUser data
 */

public interface ApiUserDao
{
    /**
     * Finds the user with the specified email address
     * @param email The email address of the user you are looking for
     * @return The ApiUser
     * @throws org.springframework.dao.DataAccessException
     */
    ApiUser getApiUserFromEmail(String email) throws DataAccessException;

    /**
     * Finds the user with the specified key
     * @param apikey The User's API key
     * @return The ApiUser
     */
    ApiUser getApiUserFromKey(String apikey) throws DataAccessException;

    /**
     * Insert a new user into the database
     * @param user The new apiuser
     * @throws org.springframework.dao.DataAccessException
     */
    void insertUser(ApiUser user) throws DataAccessException;

    /**
     * Update a preexisting user
     * @param user The APIUser to update
     * @throws DataAccessException
     */
    void updateUser(ApiUser user) throws DataAccessException;

    /**
     * Update a preexisting user's email
     * @param apikey String, The apiKey of the user whose email is being
     *               updated
     * @param email String, The new email
     */
    void updateEmail(String apikey, String email);

    /**
     * @return List<ApiUser> a list of all api users
     * @throws DataAccessException
     */
    List<ApiUser> getAllUsers() throws DataAccessException;


    /**
     * Finds the user with the specified registration token
     * @param token The registration token for the user
     * @return A user if the token is valid
     */
    ApiUser getApiUserFromToken(String token);

    /**
     * Grants a role to an api user
     * @param apiKey String
     * @param role String
     */
    void grantRole(String apiKey, OpenLegRole role);

    /**
     * Revokes a role from an api user
     * @param apiKey String
     * @param role String
     */
    void revokeRole(String apiKey, OpenLegRole role);

    /**
     * Adds an e-mail subscription for an api user
     * @param subscription ApiUserSubscriptionType
     */
    void addSubscription(String apiKey, ApiUserSubscriptionType subscription);

    /**
     * Removes an e-mail subscription for an api user
     * @param subscription ApiUserSubscriptionType
     */
    void removeSubscription(String apiKey, ApiUserSubscriptionType subscription);

    /**
     * Removes all current subscriptions for a user and
     * adds the subscriptions in the set parameter
     * @param apiKey String
     * @param subscriptions Set<ApiUserSubscriptionType>
     */
    void setSubscriptions(String apiKey, Set<ApiUserSubscriptionType> subscriptions);

    /**
     * Gets all users with the given subscription
     * @param subscription_type ApiUserSubscriptionType
     * @return A list of ApiUsers
     */
    List<ApiUser> getUsersWithSubscription(ApiUserSubscriptionType subscription_type);
}
