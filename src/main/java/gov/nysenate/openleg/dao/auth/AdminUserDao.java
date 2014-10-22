package gov.nysenate.openleg.dao.auth;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.springframework.dao.DataAccessException;

import javax.xml.crypto.Data;

/** DAO interface for connecting to and modifying AdminUser data. */

public interface AdminUserDao
{
    public void deleteAdmin(String user) throws DataAccessException;

    public void deleteAdminByLevel (int level) throws DataAccessException;

    public void addAdmin(AdminUser admin) throws DataAccessException;

    public AdminUser getAdmin(String username) throws DataAccessException;

    public int getLevelFromUser(String user) throws DataAccessException;

    public String getPasswordFromUser(String user) throws DataAccessException;

    public String getUsername(AdminUser admin);

    public String getPassword(AdminUser admin);

    public int getPrivilegeLevel(AdminUser admin);


}
