package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
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
import java.util.*;

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
        return jdbcNamed.query(ApiUserQuery.SELECT_API_USERS.getSql(schema()), new MapSqlParameterSource(),
                new ApiUserRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getApiUserFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params, new ApiUserRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getApiUserFromKey(String key) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params, new ApiUserRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getApiUserFromToken(String token) {
        MapSqlParameterSource params = new MapSqlParameterSource("registrationToken", token);
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_TOKEN.getSql(schema()), params, new ApiUserRowMapper());
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

    /** {@inheritDoc} */
    @Override
    public void addSubscription(String apiKey, ApiUserSubscriptionType subscription) {
        jdbcNamed.update(ApiUserQuery.INSERT_API_USER_SUBSCRIPTION.getSql(schema()),
                getSubscriptionParams(apiKey, subscription));
    }

    /** {@inheritDoc} */
    @Override
    public void removeSubscription(String apiKey, ApiUserSubscriptionType subscription) {
        jdbcNamed.update(ApiUserQuery.DELETE_API_USER_SUBSCRIPTION.getSql(schema()),
                getSubscriptionParams(apiKey, subscription));
    }

    /** {@inheritDoc} */
    @Override
    public void setSubscriptions(String apiKey, Set<ApiUserSubscriptionType> subscriptions) {
        //delete existing subscriptions
        jdbcNamed.update(ApiUserQuery.DELETE_ALL_API_USER_SUBSCRIPTIONS.getSql(schema()),
                new MapSqlParameterSource().addValue("apiKey", apiKey));
        //add the new subscriptions
        for(ApiUserSubscriptionType sub : subscriptions) {
            addSubscription(apiKey, sub);
        }
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

    protected MapSqlParameterSource getSubscriptionParams(String apiKey, ApiUserSubscriptionType subscription) {
        return new MapSqlParameterSource()
                .addValue("apiKey", apiKey)
                .addValue("subscription_type", subscription.name());
    }

    private static final RowMapper<ApiUser> apiUserMapper = new ApiUserRowMapper();

    private static final class ApiUserRowHandler implements RowCallbackHandler
    {
        private final Map<String, ApiUser> apiUserMap = new LinkedHashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            String apiKey = rs.getString("apikey");
            if (!apiUserMap.containsKey(apiKey)) {
                apiUserMap.put(apiKey, apiUserMapper.mapRow(rs, rs.getRow()));
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