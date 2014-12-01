package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.dao.auth.SqlAdminUserDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import gov.nysenate.openleg.service.auth.AuthenticationEx;
import org.springframework.stereotype.Service;

@Service
public class SqlAdminUserService implements AdminUserService
{
    @Autowired
    protected AdminUserDao adminDao;

    /**
     * Log the user in if they have proper account credentials.
     *
     * @param username The given username
     * @param password The given password
     * @return A Login Code representing the outcome of the login attempt
     * @throws AuthenticationEx
     */
    @Override
    public int login(String username, String password)  {
        if ((username == null) || (password == null))
            throw new IllegalArgumentException("Username or password cannot be null!");

        AdminUser admin = null;
        try {
            admin = adminDao.getAdmin(username);

        } catch (DataAccessException dae) {
            throw new AuthenticationEx(username, password);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (admin == null)
                return -1; // -1 = Username not found in the database

            boolean pwCheck = BCrypt.checkpw(password, admin.getPassword());
            if (pwCheck)
                return 0; // 0 = Everything checks out
            else
                return -2; // -2 = Invalid Password
        }
    }

    /**
     * Create a new admin user
     * @param username The name of the admin
     * @param password Their password
     */
    @Override
    public void createUser(String username, String password) {
        try {
            adminDao.addAdmin(new AdminUser(username, password, 2));

        } catch (DataAccessException dae) {
            dae.printStackTrace();
        }
    }

    /**
     * Change an user's password.
     * @param username Their username
     * @param passNew Their password
     */
    @Override
    public void changePass(String username, String passNew) {
        AdminUser admin = null;
        try {
            admin = adminDao.getAdmin(username);
            admin.setPassword(passNew);
            adminDao.updateAdmin(admin);

        } catch (DataAccessException dae) {
            dae.printStackTrace();
        } finally {

        }
    }

    /**
     * This method will return true if the given admin username is located within
     * the database.
     * @param username The adminusername
     * @return True if the admin is in the Database.
     */
    public boolean adminInDb(String username) {
        if (adminDao.getAdmin(username) != null)
            return true;
        else
            return false;
    }
}
