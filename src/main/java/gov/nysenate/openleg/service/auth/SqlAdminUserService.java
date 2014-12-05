package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.dao.auth.SqlAdminUserDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import gov.nysenate.openleg.service.auth.AuthenticationEx;
import org.springframework.stereotype.Service;

@Service
public class SqlAdminUserService implements AdminUserService
{

    @Autowired
    protected AdminUserDao adminDao;


    private static final Logger logger = LoggerFactory.getLogger(SqlAdminUserService.class);

    /**
     * Log the user in if they have proper account credentials.
     *
     * @param username The given username
     * @return A Login Code representing the outcome of the login attempt
     * @throws AuthenticationEx
     */
    @Override
    public AdminUser getAdminUser(String username)  {
        if (username == null)
            throw new IllegalArgumentException("Username or password cannot be null!");

        try {
           return adminDao.getAdminUser(username);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new UnknownAccountException("Username: " + username+ " does not exist.");
        }
    }

    /**
     * Create a new admin user
     * @param username The name of the admin
     * @param password Their password
     */
    @Override
    public void createUser(String username, String password, boolean active) {
        try {
            AdminUser newAdmin = new AdminUser(username, password, 2);
            newAdmin.setActive(true);
            adminDao.addAdmin(newAdmin);

        } catch (DataAccessException dae) {
            dae.printStackTrace();
        }
    }

    /**
     * This method will return true if the given admin username is located within
     * the database.
     * @param username The adminusername
     * @return True if the admin is in the Database.
     */
    public boolean adminInDb(String username) {
        try {
            if (adminDao.getAdminUser(username) != null)
                return true;
            else
                return false;
        } catch (DataAccessException dae) {

        }
        return false;
    }
}
