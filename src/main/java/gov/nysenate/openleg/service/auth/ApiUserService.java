package gov.nysenate.openleg.service.auth;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.model.auth.ApiUser;

import java.util.Optional;

public interface ApiUserService
{
    /**
     * This method will be called whenever there is an attempt to register a new user.
     * The appropriate checks will be made to ensure that a registration will only be successful if the
     * given email address has not already been sued for registration
     * @param email The user's submitted email address
     * @param name The entered name
     * @param orgName The entered name of the user's organization
     * @return A new ApiUser object if the registration is successful
     */
    public ApiUser registerNewUser(String email, String name, String orgName);

    /**
     * Get an API User from a given email address
     * @param email The email address of the user being search for.
     * @return An APIUser if the email is valid
     */
    public ApiUser getUser(String email);

    /**
     * Get an API User from a given api key
     * @param apiKey The apiKey of the user being search for.
     * @return An APIUser if the apiKey matches
     */
    public Optional<ApiUser> getUserByKey(String apiKey);

    /**
     * Attempt to activate a user based on the provided registration token. If a valid registration
     * token is indeed supplied, then that user will have their account activated, and their
     * API Key will be sent to them via email.
     *
     * @param regToken The supplied registration token.
     */
    public void activateUser(String regToken);

    /**
     * Check to see if a given Apikey is valid.
     * If the key belongs to a user, and the user has activated their account
     * then this method will return true.
     *
     * @param key The apikey used with the call to the API
     * @return True if the key is valid and the user has activated their account.
     */
    public boolean validateKey(String key);

    /**
     * Gets any permissions explicitly granted to this api key
     * @param key String
     * @return Set<String> set of permission strings
     */
    public ImmutableSet<OpenLegRole> getRoles(String key);

    /**
     * Grants a role to the api user with the given key
     * @param apiKey String
     * @param role String
     */
    public void grantRole(String apiKey, OpenLegRole role);

    /**
     * Revokes a role from the api user with the given key
     * @param apiKey String
     * @param role String
     */
    public void revokeRole(String apiKey, OpenLegRole role);
}
