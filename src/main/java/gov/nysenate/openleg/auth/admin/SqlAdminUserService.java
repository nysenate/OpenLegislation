package gov.nysenate.openleg.auth.admin;

import gov.nysenate.openleg.auth.exception.InvalidUsernameException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SqlAdminUserService implements AdminUserService {
    private final SqlAdminUserDao adminDao;
    @Value("${admin.email.regex}")
    private String emailRegex;
    private Pattern emailRegexPattern;

    @Autowired
    public SqlAdminUserService(SqlAdminUserDao adminDao) {
        this.adminDao = adminDao;
    }

    @PostConstruct
    public void init() {
        emailRegexPattern = Pattern.compile(emailRegex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminUser getAdminUser(String username) {
        if (username == null)
            throw new IllegalArgumentException("Username or password cannot be null!");

        try {
            return adminDao.getAdminUser(username);
        } catch (EmptyResultDataAccessException ex) {
            throw new UnknownAccountException("Username: " + username + " does not exist.");
        }
    }

    @Override
    public List<AdminUser> getAdminUsers() {
        return adminDao.getAdminUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createAdmin(String username, String password, boolean active, boolean master) throws InvalidUsernameException {
        createAdmin(new AdminUser(username, password, active, master));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createAdmin(AdminUser user) throws InvalidUsernameException {
        if (!emailRegexPattern.matcher(user.getUsername()).matches()) {
            throw new InvalidUsernameException(user.getUsername(), emailRegex);
        }
        adminDao.addAdmin(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAdmin(String username) {
        adminDao.deleteAdmin(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean adminInDb(String username) {
        try {
            return adminDao.getAdminUser(username) != null;
        } catch (EmptyResultDataAccessException dae) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMasterAdmin(String username) {
        try {
            return adminDao.getAdminUser(username).isMaster();
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    @Override
    public void updateAdmin(AdminUser admin) {
        adminDao.updateAdmin(admin);
    }
}
