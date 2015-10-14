package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SqlAdminUserService implements AdminUserService
{
    @Autowired
    protected AdminUserDao adminDao;

    @Value("${admin.email.regex}")
    private String emailRegex;

    private Pattern emailRegexPattern;

    private static final Logger logger = LoggerFactory.getLogger(SqlAdminUserService.class);

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
    public void createUser(String username, String password, boolean active, boolean master) throws InvalidUsernameException {
        createUser(new AdminUser(username, password, active, master));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUser(AdminUser user) throws InvalidUsernameException {
        if (!emailRegexPattern.matcher(user.getUsername()).matches()) {
            throw new InvalidUsernameException(user.getUsername(), emailRegex);
        }
        adminDao.addAdmin(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(String username) {
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
}
