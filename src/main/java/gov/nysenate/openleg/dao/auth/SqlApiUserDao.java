package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;


@Repository
public class SqlApiUserDao extends SqlBaseDao implements ApiUserDao
{

    public static final Logger logger = LoggerFactory.getLogger(SqlApiUserDao.class);

    /**
     * Insert a new user into the database
     * @param user The new apiuser
     * @throws org.springframework.dao.DataAccessException
     */
    public void insertUser (ApiUser user) throws DataAccessException {
        if (jdbcNamed.update(ApiUserQuery.INSERT_API_USER.getSql(schema()), getUserParams(user)) == 0)
            jdbcNamed.update(ApiUserQuery.INSERT_API_USER.getSql(schema()), getUserParams(user));
    }

    protected MapSqlParameterSource getUserParams(ApiUser user) {
        return new MapSqlParameterSource()
                .addValue("apikey", user.getApikey())
                .addValue("authenticated", user.getAuthStatus())
                .addValue("apiRequests", user.getNumRequests())
                .addValue("email", user.getEmail());
    }

    /**
     * Get the number of requests made by a user from their key
     * @param key The user's api key
     * @return The number of requests
     */
    public long getNumRequests(String key) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params,
                (rs,row) -> rs.getLong("num_requests"));
    }

    /**
     * Get the email address associated with a specfic key.
     * @param key The apikey
     * @return the email address
     */
    public String getEmailFromKey(String key) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params,
                (rs,row) -> rs.getString("email_addr"));
    }

    /**
     * Get the number of requests made by a user from their email addess
     * @param email_addr The user's email address
     * @return The number of requests
     * @throws org.springframework.dao.DataAccessException
     */
    public long getNumRequestFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getLong("num_requests"));
    }

    /**
     * Get a user's Apikey
     * @param user The ApiUser
     * @return the key
     */
    public String getApiKey (ApiUser user) {
        return user.getApikey();
    }

    /**
     * Get the ApiKey associated with the provided email address
     * @param email_addr The email address of the user you are looking for
     * @return The API key of the user, if their email is in the database
     */
    public String getApiKeyFromEmail (String email_addr) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getString("apikey"));
    }

    /**
     * Finds the user with the specified email address
     * @param email_addr The email address of the user you are looking for
     * @return The ApiUser
     */
    public ApiUser getApiUserFromEmail(String email_addr) throws DataAccessException {
        final long requestCount = getNumRequestFromEmail(email_addr);
        ApiUser user = new ApiUser(email_addr);
        user.setNumRequests(requestCount);

        if (requestCount > 0)
            user.setAuthStatus(true);

        user.setApiKey(getApiKeyFromEmail(email_addr));
        return user;
    }

    /**
     * Finds the user with the specfied key
     * @param key The User's API key
     * @return The ApiUser
     */
    public ApiUser getApiUserFromKey(String key) {
        final long requestCount = getNumRequests(key);
        final String email = getEmailFromKey(key);

        if (email.length() == 0)
            return null;

        ApiUser user = new ApiUser(email);
        user.setNumRequests(requestCount);

        if (requestCount > 0)
            user.setAuthStatus(true);

        user.setApiKey(key);
        return user;
    }

    /**
     * Remove an ApiUser from the database
     * @param user The apiuser to be deleted
     */
    public void deleteApiUser(ApiUser user)
    {
        if (jdbcNamed.update(ApiUserQuery.DELETE_USER.getSql(schema()), getUserParams(user)) == 0)
            jdbcNamed.update(ApiUserQuery.DELETE_USER.getSql(schema()), getUserParams(user));
    }
}
