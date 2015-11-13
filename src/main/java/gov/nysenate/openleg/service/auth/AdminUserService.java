package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.model.auth.AdminUser;
import org.apache.shiro.authc.AuthenticationException;

import java.util.List;

public interface AdminUserService
{

    /**
     * Create a new admin user
     * @param username The email of the admin
     * @param password Their password
     * @param master
     */
    public void createUser(String username, String password, boolean active, boolean master) throws InvalidUsernameException;

    /**
     * Create a new admin user
     * @param user The new user
     * @throws InvalidUsernameException if the user's username does not match specification
     */
    public void createUser(AdminUser user) throws InvalidUsernameException;

    /**
     * Deletes the specified admin user
     * @param username The username of the user to delete
     */
    public void deleteUser(String username);

    /**
     * @return a list of all admin users
     */
    public List<AdminUser> getAdminUsers();

    /**
     * Retrieve the user with the matching username.
     *
     * @param username The given username
     * @return A Login Code representing the outcome of the login attempt
     */
    public AdminUser getAdminUser(String username) throws AuthenticationException;

    /**
     * This method will return true if the given admin username is located within
     * the database.
     * @param username The adminusername
     * @return boolean - True if the admin is in the Database.
     */
    public boolean adminInDb(String username);

    /**
     * This method will return true if the given admin username is a valid admin and is a master admin
     * @param username The adminusername
     * @return boolean - true if there is a master admin with the given user name
     */
    public boolean isMasterAdmin(String username);
}