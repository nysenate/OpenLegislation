package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
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
        return user;
    }
}
