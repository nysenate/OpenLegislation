package gov.nysenate.openleg.dao.auth;
import gov.nysenate.openleg.model.auth.AdminUser;

/** DAO interface for connecting to and modifying AdminUser data. */

public interface AdminUserDao {

    public AdminUser getAdmin(String user);

    public String getUsername(AdminUser admin);

    public String getPassword(AdminUser admin);

    public String getRole(AdminUser admin);

}
