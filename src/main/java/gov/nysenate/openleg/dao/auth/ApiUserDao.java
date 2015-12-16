package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.service.auth.OpenLegRole;
import org.springframework.dao.DataAccessException;

import java.util.List;

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
    public ApiUser getApiUserFromEmail(String email) throws DataAccessException;

    /**
     * Finds the user with the specified key
     * @param apikey The User's API key
     * @return The ApiUser
     */
    public ApiUser getApiUserFromKey(String apikey) throws DataAccessException;

    /**
     * Insert a new user into the database
     * @param user The new apiuser
     * @throws org.springframework.dao.DataAccessException
     */
    public void insertUser(ApiUser user) throws DataAccessException;

    /**
     * Update a preexisting user
     * @param user The APIUser to update
     * @throws DataAccessException
     */
    public void updateUser(ApiUser user) throws DataAccessException;

    /**
     * @return List<ApiUser> a list of all api users
     * @throws DataAccessException
     */
    public List<ApiUser> getAllUsers() throws DataAccessException;

    /**
     * Remove an ApiUser from the database
     * @param apiuser The apiuser to be deleted
     */
    public void deleteApiUser(ApiUser apiuser) throws DataAccessException;

    /**
     * Finds the user with the specified registration token
     * @param token The registration token for the user
     * @return A user if the token is valid
     */
    public ApiUser getApiUserFromToken(String token);

    /**
     * Grants a role to an api user
     * @param apiKey String
     * @param role String
     */
    public void grantRole(String apiKey, OpenLegRole role);

    /**
     * Revokes a role from an api user
     * @param apiKey String
     * @param role String
     */
    public void revokeRole(String apiKey, OpenLegRole role);
}
