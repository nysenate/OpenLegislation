package gov.nysenate.openleg.service.auth;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Base realm layer which sets up some convenience methods for resolving permissions.
 */
public abstract class OpenLegAuthorizingRealm extends AuthorizingRealm
{
    private static final Logger logger = LoggerFactory.getLogger(OpenLegAuthorizingRealm.class);

    protected static class OpenLegRolePermissionResolver implements RolePermissionResolver
    {
        @Override
        public Collection<Permission> resolvePermissionsInRole(String roleString) {
            try {
                OpenLegRole openLegRole = OpenLegRole.valueOf(roleString);
                return openLegRole.getWildcardPermissions();
            }
            catch (IllegalArgumentException ex) {
                logger.warn("The role '{}' is not a known role! This needs to be addressed.", roleString);
            }
            return Collections.emptyList();
        }
    }

    protected static final RolePermissionResolver openlegRolePermResolver = new OpenLegRolePermissionResolver();

    @Override
    public void setRolePermissionResolver(RolePermissionResolver permissionRoleResolver) {
        throw new UnsupportedOperationException("Cannot set role resolvers.");
    }

    @Override
    public RolePermissionResolver getRolePermissionResolver() {
        return openlegRolePermResolver;
    }
}