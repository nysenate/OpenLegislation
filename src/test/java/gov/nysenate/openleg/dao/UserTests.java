package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.controller.api.admin.AdminAccountCtrl;
import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.dao.auth.SqlAdminUserDao;
import gov.nysenate.openleg.util.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class UserTests {


    private static final Logger logger = LoggerFactory.getLogger(UserTests.class);

    private String existingUser;
    private String newUser;

    @Mock
    @Autowired
    private AdminUserDao adminUserDao;

    @Mock
    @Autowired
    private SqlAdminUserDao sqlAdminUserDao;

    @Mock
    @Autowired
    private AdminAccountCtrl adminAccountCtrl;

    @Before
    public void setup() throws Exception {

        existingUser = "admin@nysenate.gov";
        newUser = "newUsee@nysenate.gov";
        MockitoAnnotations.initMocks(this);
    }

    /**
     *  From a given username, check the database to find their account.
     *  Expected Output: successful admin-registered response if the user was created
     */
    @Test
    public void existsInDb() {

        boolean exists = sqlAdminUserDao.getAdminUser(existingUser)!=null;
        assertEquals(true, exists);
        logger.info("The result of the test, user exists:", exists);
    }

    /**
     *  From a given username, check the database to find their account.
     *  Expected Output: non admin-registered response if the user not created
     */

    @Test
    public void existsNotInDb() {

        boolean exists = sqlAdminUserDao.getAdminUser(newUser)!=null;
        assertEquals(false, exists);
        logger.info("The result of the test, user exists not:", exists);
    }

    /**
     * Sends an email to a new user notifying them of their registration
     * Try to send with a existing user, activate the user so the use becomes visible to the UI as activated.
     */
    @Test
    public void sendUserMail()
    {
        logger.info("Test mock of sending emails");
        Mockito.doThrow(new RuntimeException()).doNothing().when(adminAccountCtrl).sendNewUserEmail(existingUser, RandomUtils.getRandomString(8));
    }

}
