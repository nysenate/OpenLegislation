package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.service.auth.OpenLegRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SqlApiUserDao extends SqlBaseDao implements ApiUserDao
{

    public static final Logger logger = LoggerFactory.getLogger(SqlApiUserDao.class);

    /** {@inheritDoc} */
    @Override
    public void insertUser (ApiUser user) throws DataAccessException {
        if (jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user)) == 0)
            jdbcNamed.update(ApiUserQuery.INSERT_API_USER.getSql(schema()), getUserParams(user));
    }

    /** {@inheritDoc} */
    @Override
    public void updateUser(ApiUser user) throws DataAccessException {
        jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user));
    }

    /** {@inheritDoc} */
    @Override
    public List<ApiUser> getAllUsers() throws DataAccessException {
        ApiUserRowHandler rowHandler = new ApiUserRowHandler();
        jdbcNamed.query(ApiUserQuery.SELECT_API_USERS.getSql(schema()), new MapSqlParameterSource(), rowHandler);
        return rowHandler.getUsers();
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getApiUserFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        ApiUserRowHandler rowHandler = new ApiUserRowHandler();
        jdbcNamed.query(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params, rowHandler);
        return rowHandler.getSingleUser();
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getApiUserFromKey(String key) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        ApiUserRowHandler rowHandler = new ApiUserRowHandler();
        jdbcNamed.query(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params, rowHandler);
        return rowHandler.getSingleUser();
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getApiUserFromToken(String token) {
        MapSqlParameterSource params = new MapSqlParameterSource("registrationToken", token);
        ApiUserRowHandler rowHandler = new ApiUserRowHandler();
        jdbcNamed.query(ApiUserQuery.SELECT_BY_TOKEN.getSql(schema()), params, rowHandler);
        return rowHandler.getSingleUser();
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApiUser(ApiUser user) throws DataAccessException {
        jdbcNamed.update(ApiUserQuery.DELETE_USER.getSql(schema()), getUserParams(user));
    }

    /** {@inheritDoc} */
    @Override
    public void grantRole(String apiKey, OpenLegRole role) {
        try {
            jdbcNamed.update(ApiUserQuery.INSERT_API_USER_ROLE.getSql(schema()), getRoleParams(apiKey, role));
        } catch (DuplicateKeyException ignored) {}
    }

    /** {@inheritDoc} */
    @Override
    public void revokeRole(String apiKey, OpenLegRole role) {
        jdbcNamed.update(ApiUserQuery.DELETE_API_USER_ROLE.getSql(schema()), getRoleParams(apiKey, role));
    }

    /** --- Internal Methods --- */

    protected MapSqlParameterSource getUserParams(ApiUser user) {
        return new MapSqlParameterSource()
                .addValue("apikey", user.getApiKey())
                .addValue("authenticated", user.isAuthenticated())
                .addValue("apiRequests", user.getNumApiRequests())
                .addValue("email", user.getEmail())
                .addValue("name", user.getName())
                .addValue("organizationName", user.getOrganizationName())
                .addValue("registrationToken", user.getRegistrationToken());
    }

    protected MapSqlParameterSource getRoleParams(String apiKey, OpenLegRole role) {
        return new MapSqlParameterSource()
                .addValue("apiKey", apiKey)
                .addValue("role", role.name());
    }

    private static final RowMapper <ApiUser> apiUserMapper = (rs, rowNum) -> {
        ApiUser user = new ApiUser(rs.getString("email_addr"));
        user.setAuthenticated(rs.getBoolean("authenticated"));
        user.setName(rs.getString("users_name"));
        user.setRegistrationToken(rs.getString("reg_token"));
        user.setApiKey(rs.getString("apikey"));
        user.setOrganizationName(rs.getString("org_name"));
        return user;
    };

    private static final class ApiUserRowHandler implements RowCallbackHandler
    {
        private final Map<String, ApiUser> apiUserMap = new LinkedHashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            String apiKey = rs.getString("apikey");
            if (!apiUserMap.containsKey(apiKey)) {
                apiUserMap.put(apiKey, apiUserMapper.mapRow(rs, rs.getRow()));
            }
            String roleStr = rs.getString("role");
            if (roleStr != null) {
                apiUserMap.get(apiKey).addRole(OpenLegRole.valueOf(roleStr));
            }
        }

        public List<ApiUser> getUsers() {
            return new ArrayList<>(apiUserMap.values());
        }

        public ApiUser getSingleUser() {
            if (apiUserMap.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(1, apiUserMap.size());
            }
            return apiUserMap.values().stream().findAny()
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));
        }
    }
}