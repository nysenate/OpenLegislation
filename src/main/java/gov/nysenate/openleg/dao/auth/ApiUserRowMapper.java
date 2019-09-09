package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.auth.OpenLegRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ApiUserRowMapper implements RowMapper<ApiUser> {

    @Override
    public ApiUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiUser user = new ApiUser(rs.getString("email_addr"));
        user.setAuthenticated(rs.getBoolean("authenticated"));
        user.setName(rs.getString("users_name"));
        user.setRegistrationToken(rs.getString("reg_token"));
        user.setApiKey(rs.getString("apikey"));
        user.setOrganizationName(rs.getString("org_name"));

        String[] subscriptions = null;
        if (rs.getArray("subscriptions") != null) {
            subscriptions = (String[]) rs.getArray("subscriptions").getArray();
        }
        if (subscriptions != null) {
            //use a for loop to add all the subscriptions
            for (String subscription: subscriptions) {
                user.addSubscription(ApiUserSubscriptionType.valueOf(subscription));
            }
        }

        String[] roles = null;
        if (rs.getArray("roles") != null) {
            roles = (String[]) rs.getArray("roles").getArray();
        }
        if (roles != null) {
            //use a for loop to add all the roles
            for (String role: roles) {
                user.addRole(OpenLegRole.valueOf(role));
            }
        }

        return user;
    }
}
