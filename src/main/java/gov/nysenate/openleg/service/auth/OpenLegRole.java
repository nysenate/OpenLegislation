package gov.nysenate.openleg.service.auth;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines roles and the permissions implied by them.
 */
public enum OpenLegRole
{
    MASTER_ADMIN(Collections.singletonList("*")),
    READONLY_ADMIN(Arrays.asList("admin:view", "ui:view")),
    INTERNAL_USER(Arrays.asList("ui:view")),
    API_USER(Arrays.asList("ui:view")),
    SEN_SITE_API_USER(Collections.singletonList("senatesite:*:*"))
    ;

    private List<String> permissions;
    private List<Permission> wildcardPermissions;

    OpenLegRole(List<String> permissions) {
        this.permissions = permissions;
        this.wildcardPermissions =
            this.permissions.stream()
                .map(WildcardPermission::new)
                .collect(Collectors.toList());
    }

    public List<String> getPermissionStrings() {
        return permissions;
    }

    public List<Permission> getWildcardPermissions() {
        return wildcardPermissions;
    }
}
