package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * DAO Interface for retrieving and persisting ApiUser data
 */

public interface ApiUserDao
{
    public ApiUser getApiUserFromEmail(String email) throws DataAccessException;

    public ApiUser getApiUserFromKey(String apikey) throws DataAccessException;

    public void insertUser(ApiUser user) throws DataAccessException;

    public void updateUser(ApiUser user) throws DataAccessException;

    public List<ApiUser> getAllUsers() throws DataAccessException;

    public void deleteApiUser(ApiUser apiuser) throws DataAccessException;

    public ApiUser getApiUserFromToken(String token);
}
