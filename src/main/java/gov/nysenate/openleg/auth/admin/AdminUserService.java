package gov.nysenate.openleg.auth.admin;

import gov.nysenate.openleg.auth.exception.InvalidUsernameException;
import org.apache.shiro.authc.AuthenticationException;

import java.util.List;

public interface AdminUserService {

    /**
     * Create a new admin user
     * @param username The email of the admin
     * @param password Their password
     * @param master
     */
    void createAdmin(String username, String password, boolean active, boolean master) throws InvalidUsernameException;

    /**
     * Create a new admin user
     * @param user The new user
     * @throws InvalidUsernameException if the user's username does not match specification
     */
    void createAdmin(AdminUser user) throws InvalidUsernameException;

    /**
     * Deletes the specified admin user
     * @param username The username of the user to delete
     */
    void deleteAdmin(String username);

    /**
     * @return a list of all admin users
     */
    List<AdminUser> getAdminUsers();

    /**
     * Retrieve the user with the matching username.
     *
     * @param username The given username
     * @return A Login Code representing the outcome of the login attempt
     */
    AdminUser getAdminUser(String username) throws AuthenticationException;

    /**
     * This method will return true if the given admin username is located within
     * the database.
     * @param username The adminusername
     * @return boolean - True if the admin is in the Database.
     */
    boolean adminInDb(String username);

    /**
     * This method will return true if the given admin username is a valid admin and is a master admin
     * @param username The adminusername
     * @return boolean - true if there is a master admin with the given user name
     */
    boolean isMasterAdmin(String username);

    /**
     * Update an admin.
     * @param admin
     */
    void updateAdmin(AdminUser admin);
}