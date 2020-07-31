package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.AdminUser;
import org.springframework.dao.DataAccessException;

import java.util.List;

/** DAO interface for connecting to and modifying AdminUser data. */

public interface AdminUserDao
{
    void addAdmin(AdminUser admin) throws DataAccessException;
    void deleteAdmin(String user) throws DataAccessException;
    List<AdminUser> getAdminUsers() throws DataAccessException;
    AdminUser getAdminUser(String username) throws DataAccessException;
    void updateAdmin(AdminUser admin) throws DataAccessException;
}
