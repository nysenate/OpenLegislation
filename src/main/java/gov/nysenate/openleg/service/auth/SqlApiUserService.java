package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SqlApiUserService implements ApiUserService
{
    @Autowired
    protected ApiUserDao apiUserDao;

    private static final Logger logger = LoggerFactory.getLogger(SqlApiUserService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUser(String email) {
        createUser(new ApiUser(email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUser(ApiUser user) {
        try {
            apiUserDao.insertUser(user);

        } catch (DataAccessException ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(ApiUser user) {
        try {
            apiUserDao.deleteApiUser(user);

        } catch (DataAccessException ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUserByEmail(String emailAddr) {
        try {
            apiUserDao.deleteApiUserByEmail(emailAddr);

        } catch (DataAccessException ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * {@inheritDoc}
     */
    public ApiUser getUserByEmail (String email) {
        return apiUserDao.getApiUserFromEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUser(ApiUser user) {
        try {
            apiUserDao.updateUser(user);

        } catch (DataAccessException ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }
}
