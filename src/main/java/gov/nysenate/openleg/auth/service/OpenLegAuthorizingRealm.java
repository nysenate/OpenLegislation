package gov.nysenate.openleg.auth.service;

import gov.nysenate.openleg.auth.model.OpenLegRole;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base realm layer which sets up some convenience methods for resolving permissions.
 */
public abstract class OpenLegAuthorizingRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(OpenLegAuthorizingRealm.class);

    protected static final RolePermissionResolver openlegRolePermResolver = roleString -> {
        try {
            return OpenLegRole.valueOf(roleString).getWildcardPermissions();
        }
        catch (IllegalArgumentException ex) {
            logger.warn("The role '{}' is not a known role! This needs to be addressed.", roleString);
            return List.of();
        }
    };

    @Override
    public void setRolePermissionResolver(RolePermissionResolver permissionRoleResolver) {
        throw new UnsupportedOperationException("Cannot set role resolvers.");
    }

    @Override
    public RolePermissionResolver getRolePermissionResolver() {
        return openlegRolePermResolver;
    }
}
