package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
import org.springframework.dao.DataAccessException;

/**
 * DAO Interface for retrieving and persisting ApiUser data
 */

public interface ApiUserDao {
    public void insertUser(ApiUser user) throws DataAccessException;

    public void updateUser(ApiUser user) throws DataAccessException;

    public ApiUser getApiUserFromEmail(String email) throws DataAccessException;

    public ApiUser getApiUserFromKey(String apikey) throws DataAccessException;

    public String getApiKey(ApiUser user);

    public long getNumRequests(String email) throws DataAccessException;

    public long getNumRequestFromEmail(String email) throws DataAccessException;

    public String getApiKeyFromEmail(String email) throws DataAccessException;

    public void deleteApiUser(ApiUser apiuser) throws DataAccessException;

    public void deleteApiUserByEmail(String email) throws DataAccessException;

    public ApiUser getApiUserFromToken(String token);

    public String getOrganizationFromEmail(String email_addr) throws DataAccessException;

    public String getEmailFromToken(String regToken) throws DataAccessException;

    public String getRegTokenFromEmail (String email_addr) throws DataAccessException;

    public String getEmailFromKey(String key) throws DataAccessException;

    public String getNameFromEmail(String email) throws DataAccessException;

    public boolean getAuthStatusFromEmail(String email) throws DataAccessException;

    public boolean getAuthStatusFromKey(String key) throws DataAccessException;
}
