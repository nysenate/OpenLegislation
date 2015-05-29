package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.AdminUser;
import org.springframework.dao.DataAccessException;

import java.util.List;

/** DAO interface for connecting to and modifying AdminUser data. */

public interface AdminUserDao
{
    public void addAdmin(AdminUser admin) throws DataAccessException;
    public void deleteAdmin(String user) throws DataAccessException;
    public List<AdminUser> getAdminUsers() throws DataAccessException;
    public AdminUser getAdminUser(String username) throws DataAccessException;
    public void updateAdmin(AdminUser admin) throws DataAccessException;
}
