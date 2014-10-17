package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.AdminUser;

public class SqlAdminUserDao implements AdminUserDao
{
    public AdminUser getAdmin(String username) {
        return null;
    }
    
    public String getUsername(AdminUser admin) {
        return "";
    }

    public String getPassword(AdminUser admin) {
        return "";
    }

    public String getRole(AdminUser admin) {
        return "";
    }

}
