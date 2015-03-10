package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    /**
     * Get the number of requests made by a user from their key
     * @param key The user's api key
     * @return The number of requests
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public long getNumRequests(String key) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params,
                (rs,row) -> rs.getLong("num_requests"));
    }

    /**
     * Get the email address associated with a specfic key.
     * @param key The apikey
     * @return the email address
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public String getEmailFromKey(String key) throws DataAccessException {
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
    @Override
    public long getNumRequestFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getLong("num_requests"));
    }

    /**
     * Get the authentication status of the user with the given email address
     * @param email_addr The user's email address
     * @return Returns whether or not the user has authenticated their account
     * @throws DataAccessException
     */
    @Override
    public boolean getAuthStatusFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getBoolean("authenticated"));
    }

    /**
     * Get the authentication status of the user with the given Api Key
     * @param key The user's Api key
     * @return Returns whether or not the user has authenticated their account
     * @throws DataAccessException
     */
    @Override
    public boolean getAuthStatusFromKey(String key) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("apikey", key));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_KEY.getSql(schema()), params,
                (rs,row) -> rs.getBoolean("authenticated"));
    }

    /**
     * Get a user's Apikey
     * @param user The ApiUser
     * @return the key
     */
    @Override
    public String getApiKey (ApiUser user) {
        return user.getApikey();
    }

    /**
     * Get the ApiKey associated with the provided email address
     * @param email_addr The email address of the user you are looking for
     * @return The API key of the user, if their email is in the database
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public String getApiKeyFromEmail (String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getString("apikey"));
    }

    /**
     * Get the Registration Token associated with the provided email address
     * @param email_addr The email address of the user you are looking for
     * @return The Registration Token of the user, if their email is in the database
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public String getRegTokenFromEmail (String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getString("reg_token"));
    }

    /**
     * Get the email address associated with a registration token
     * @param regToken The registration token of the user you are looking for
     * @return The email address if the reg. token is valid
     * @throws DataAccessException
     */
    @Override
    public String getEmailFromToken(String regToken) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("registrationToken", regToken));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_TOKEN.getSql(schema()), params,
                (rs,row) -> rs.getString("email_addr"));
    }

    /**
     * Get the name of the user with the specified email address
     * @param email_addr Their email address
     * @return The name of the user if the email address is valid
     * @throws DataAccessException
     */
    @Override
    public String getNameFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getString("users_name"));
    }

    /**
     * Get the name of the user's organization from the given email address
     * @param email_addr Their email address
     * @return The name of the user's organization if the email address is valid
     * @throws DataAccessException
     */
    @Override
    public String getOrganizationFromEmail(String email_addr) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("email", email_addr));
        return jdbcNamed.queryForObject(ApiUserQuery.SELECT_BY_EMAIL.getSql(schema()), params,
                (rs,row) -> rs.getString("org_Name"));
    }

    /**
     * Finds the user with the specified email address
     * @param email_addr The email address of the user you are looking for
     * @return The ApiUser
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public ApiUser getApiUserFromEmail(String email_addr) throws DataAccessException{
        final long requestCount = getNumRequestFromEmail(email_addr);
        final String name = getNameFromEmail(email_addr);
        final String orgName = getOrganizationFromEmail(email_addr);
        final String regToken = getRegTokenFromEmail(email_addr);
        final boolean authStatus = getAuthStatusFromEmail(email_addr);

        ApiUser user = new ApiUser(email_addr);
        user.setNumRequests(requestCount);
        user.setName(name);
        user.setOrganizationName(orgName);
        user.setRegistrationToken(regToken);
        user.setAuthStatus(authStatus);
        user.setApiKey(getApiKeyFromEmail(email_addr));
        return user;
    }

    /**
     * Finds the user with the specified key
     * @param key The User's API key
     * @return The ApiUser
     */
    @Override
    public ApiUser getApiUserFromKey(String key) {
        final long requestCount = getNumRequests(key);
        final String email = getEmailFromKey(key);
        final String name = getNameFromEmail(email);
        final String orgName = getOrganizationFromEmail(email);
        final String regToken = getRegTokenFromEmail(email);
        final boolean authStatus = getAuthStatusFromKey(key);

        if (email.length() == 0)
            return null;

        ApiUser user = new ApiUser(email);
        user.setNumRequests(requestCount);
        user.setName(name);
        user.setOrganizationName(orgName);
        user.setRegistrationToken(regToken);
        user.setAuthStatus(authStatus);
        user.setApiKey(key);
        return user;
    }

    /**
     * Finds the user with the specified registration token
     * @param token The registration token for the user
     * @return A user if the token is valid
     */
    @Override
    public ApiUser getApiUserFromToken(String token) {
        String email = getEmailFromToken(token);
        if (email.length() == 0)
            return null;

        ApiUser user = getApiUserFromEmail(email);
        user.setRegistrationToken(token);
        return user;

    }

    /**
     * Remove an ApiUser from the database
     * @param user The apiuser to be deleted
     */
    @Override
    public void deleteApiUser(ApiUser user) throws DataAccessException {
        if (jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), getUserParams(user)) == 0)
            jdbcNamed.update(ApiUserQuery.DELETE_USER.getSql(schema()), getUserParams(user));
    }


    /**
     * Delete the ApiUser with the given email address from the database
     * @param email The user's email address
     * @throws DataAccessException
     */
    @Override
    public void deleteApiUserByEmail(String email) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("email", email));
        if (jdbcNamed.update(ApiUserQuery.UPDATE_API_USER.getSql(schema()), params) == 0)
            jdbcNamed.update(ApiUserQuery.DELETE_USER.getSql(schema()), params);
    }
}
