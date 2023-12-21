package gov.nysenate.openleg.auth.model;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines roles and the permissions implied by them.
 */
public enum OpenLegRole {
    MASTER_ADMIN("*"),
    READONLY_ADMIN("admin:view", "ui:view"),
    INTERNAL_USER("ui:view"),
    API_USER("ui:view"),
    SEN_SITE_API_USER("senatesite:*:*");

    private final List<Permission> wildcardPermissions;

    OpenLegRole(String... permissions) {
        this.wildcardPermissions =
            Arrays.stream(permissions)
                .map(WildcardPermission::new)
                .collect(Collectors.toList());
    }

    public List<Permission> getWildcardPermissions() {
        return wildcardPermissions;
    }
}
