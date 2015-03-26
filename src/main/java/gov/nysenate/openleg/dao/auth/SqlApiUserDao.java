package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqlApiUserDao extends SqlBaseDao implements ApiUserDao
{

    public static final Logger logger = LoggerFactory.getLogger(SqlApiUserDao.class);

    /**
     * Insert a new user into the database
     * @param user The new apiuser
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public void insertUser (ApiUser user) throws DataAccessException {
        if (jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user)) == 0)
            jdbcNamed.update(ApiUserQuery.INSERT_API_USER.getSql(schema()), getUserParams(user));
    }

    /**
     * Update a preexisting user
     * @param user The APIUser to update
     * @throws DataAccessException
     */
    @Override
    public void updateUser(ApiUser user) throws DataAccessException {
        jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user));
    }

    protected MapSqlParameterSource getUserParams(ApiUser user) {
        return new MapSqlParameterSource()
                .addValue("apikey", user.getApikey())
                .addValue("authenticated", user.getAuthStatus())
                .addValue("apiRequests", user.getNumRequests())
                .addValue("email", user.getEmail())
                .addValue("name", user.getName())
                .addValue("organizationName", user.getOrganizationName())
                .addValue("registrationToken", user.getRegistrationToken());
    }

    @Override
    public List<ApiUser> getAllUsers() throws DataAccessException {
        return jdbcNamed.query(ApiUserQuery.SELECT_ALL_USERS.getSql(schema()),
                new MapSqlParameterSource(), apiUserMapper);
    }

    private static final RowMapper <ApiUser> apiUserMapper = (rs, rowNum) -> {
        ApiUser user = new ApiUser(rs.getString("email_addr"));
        user.setAuthStatus(rs.getBoolean("authenticated"));
        user.setName(rs.getString("users_name"));
        user.setRegistrationToken(rs.getString("reg_token"));
        user.setApiKey(rs.getString("apikey"));
        user.setOrganizationName(rs.getString("org_name"));
        return user;
    };

    /**
     * Finds the user with the specified email address
     * @param email_addr The email address of the user you are looking for
     * @return The ApiUser
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public ApiUser getApiUserFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()),
                params, apiUserMapper);
    }

    /**
     * Finds the user with the specified key
     * @param key The User's API key
     * @return The ApiUser
     */
    @Override
    public ApiUser getApiUserFromKey(String key) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params,
                apiUserMapper);
    }

    /**
     * Finds the user with the specified registration token
     * @param token The registration token for the user
     * @return A user if the token is valid
     */
    @Override
    public ApiUser getApiUserFromToken(String token) {
        MapSqlParameterSource params = new MapSqlParameterSource("registrationToken", token);
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_TOKEN.getSql(schema()), params,
                apiUserMapper);

    }

    /**
     * Remove an ApiUser from the database
     * @param user The apiuser to be deleted
     */
    @Override
    public void deleteApiUser(ApiUser user) throws DataAccessException {
        jdbcNamed.update(ApiUserQuery.DELETE_USER.getSql(schema()), getUserParams(user));
    }
}
