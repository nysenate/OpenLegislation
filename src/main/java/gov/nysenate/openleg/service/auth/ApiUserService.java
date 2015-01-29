package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.model.auth.ApiUser;

public interface ApiUserService
{
    /**
     * Get an API User from a given email address
     * @param email The email address of the user being searched for.
     * @return An APIUser if the email is valid
     */
    public ApiUser getUserByEmail(String email);

    /**
     * Create a new APIUser from a given email address
     * @param email The email address of the APIUser
     */
    public void createUser(String email);

    /** Create an APIUser from an existing APIUser object
     * @param user The APIUser model object
     */
    public void createUser(ApiUser user);

    /**
     * Update an existing APIUser
     * @param user The APIUser to update
     */
    public void updateUser(ApiUser user);

    /**
     * Delete an APIUser
     * @param user The APIUser to delete
     */
    public void deleteUser(ApiUser user);

    /**
     * Delete an APIUser with the given email address
     * @param email The email address of the APIUser that will be deleted
     */
    public void deleteUserByEmail(String email);
}
