package gov.nysenate.openleg.auth.user;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.model.OpenLegRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ApiUserService {
    /**
     * Get an API User from a given email address
     * @param email The email address of the user being search for.
     * @return An APIUser if the email is valid
     */
    ApiUser getUser(String email);

    /**
     * Get an API User from a given api key
     * @param apiKey The apiKey of the user being search for.
     * @return An APIUser if the apiKey matches
     */
    Optional<ApiUser> getUserByKey(String apiKey);

    /**
     * Gets all users with the given subscription
     * @param subscriptionType ApiUserSubscriptionType
     * @return A list of ApiUsers
     */
    List<ApiUser> getUsersWithSubscription(ApiUserSubscriptionType subscriptionType);

    /**
     * This method will be called whenever there is an attempt to register a new user.
     * The appropriate checks will be made to ensure that a registration will only be successful if the
     * given email address has not already been sued for registration
     * @param email The user's submitted email address
     * @param name The entered name
     * @param orgName The entered name of the user's organization
     * @param subscriptions The email subscriptions the user has signed up for
     * @return A new ApiUser object if the registration is successful
     */
    ApiUser registerNewUser(String email, String name, String orgName, Set<ApiUserSubscriptionType> subscriptions);

    /**
     * Update a preexisting user's email
     * @param apiKey String, The apiKey of the user whose email is being
     *               updated
     * @param email String, The new email
     */
    void updateEmail(String apiKey, String email);

    /**
     * Attempt to activate a user based on the provided registration token. If a valid registration
     * token is indeed supplied, then that user will have their account activated, and their
     * API Key will be sent to them via email.
     *
     * @param regToken The supplied registration token.
     */
    void activateUser(String regToken);

    /**
     * Check to see if a given Apikey is valid.
     * If the key belongs to a user, and the user has activated their account
     * then this method will return true.
     *
     * @param key The apikey used with the call to the API
     * @return True if the key is valid and the user has activated their account.
     */
    boolean validateKey(String key);

    /**
     * Gets any permissions explicitly granted to this api key
     * @param key String
     * @return Set<String> set of permission strings
     */
    ImmutableSet<OpenLegRole> getRoles(String key);

    /**
     * Grants a role to the api user with the given key
     * @param apiKey String
     * @param role String
     */
    void grantRole(String apiKey, OpenLegRole role);

    /**
     * Revokes a role from the api user with the given key
     * @param apiKey String
     * @param role String
     */
    void revokeRole(String apiKey, OpenLegRole role);

    /**
     * Gets any e-mail subscriptions this api key is signed up for
     * @param key String
     * @return Set<ApiUserSubscriptionType> set of subscriptions
     */
    ImmutableSet<ApiUserSubscriptionType> getSubscriptions(String key);

    /**
     * Adds an e-mail subscription to a user with the given api key
     * @param apiKey String
     * @param subscription ApiUserSubscriptionType
     */
    void addSubscription(String apiKey, ApiUserSubscriptionType subscription);

    /**
     * Removes all current subscriptions for a user and
     * adds the subscriptions in the set parameter
     * @param apiKey String
     * @param subscriptions Set<ApiUserSubscriptionType>
     */
    void setSubscriptions(String apiKey, Set<ApiUserSubscriptionType> subscriptions);

    /**
     * Removes an e-mail subscription from the user with the given api key
     * @param apiKey String
     * @param subscription ApiUserSubscriptionType
     */
    void removeSubscription(String apiKey, ApiUserSubscriptionType subscription);
}
