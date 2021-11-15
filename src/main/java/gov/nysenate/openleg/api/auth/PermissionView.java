package gov.nysenate.openleg.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.openleg.api.ViewObject;

/**
 * Represents the result of checking if the current subject has a given permission
 */
public class PermissionView implements ViewObject {

    /** The permission being checked for. */
    private String permission;

    /** True if the current subject has {@code permission} */
    private boolean isPermitted;

    /** Is the current subject authenticated */
    private boolean isAuthenticated;

    public PermissionView() {
    }

    public PermissionView(String permission, boolean isPermitted, boolean isAuthenticated) {
        this.permission = permission;
        this.isPermitted = isPermitted;
        this.isAuthenticated = isAuthenticated;
    }

    public String getPermission() {
        return permission;
    }

    @JsonProperty(value="isPermitted")
    public boolean isPermitted() {
        return isPermitted;
    }

    @JsonProperty(value="isAuthenticated")
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public String getViewType() {
        return "permission-view";
    }
}
