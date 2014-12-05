package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.model.auth.AdminUser;
import org.apache.shiro.authc.AuthenticationException;

public interface AdminUserService
{
    public boolean adminInDb(String username);

    public void createUser(String username, String password, boolean active);

    public AdminUser getAdminUser(String username) throws AuthenticationException;
}