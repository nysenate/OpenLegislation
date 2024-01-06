package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.auth.admin.AdminUser;

public record AdminUserView(String username, boolean active, boolean master) implements ViewObject {

    public AdminUserView(AdminUser user) {
        this(user.getUsername(), user.isActive(), user.isMaster());
    }

    @Override
    public String getViewType() {
        return "admin-user";
    }
}
