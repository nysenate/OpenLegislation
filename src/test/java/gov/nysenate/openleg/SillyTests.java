package gov.nysenate.openleg;

import gov.nysenate.openleg.dao.auth.SqlAdminUserDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Autowired
    SqlAdminUserDao sqlaud;

    @Test
    public void updateTest()
    {
        AdminUser roy = sqlaud.getAdmin("Roy55U");
        logger.info("Old Password: " +roy.getPassword());
        roy.setPassword("newpassword");
        logger.info("New Password: " + roy.getPassword());

        sqlaud.updateAdmin(roy);
        logger.info("Pass from DB: " + sqlaud.getPasswordFromUser("Roy55U"));

    }

    /** --- Your silly tests go here --- */

    /*
                      __     __,
                      \,`~"~` /
      .-=-.           /    . .\
     / .-. \          {  =    Y}=
    (_/   \ \          \      /
           \ \        _/`'`'`b
            \ `.__.-'`        \-._
             |            '.__ `'-;_
             |            _.' `'-.__)
              \    ;_..--'/     //  \
              |   /  /   |     //    |
              \  \ \__)   \   //    /
               \__)        './/   .'
                             `'-'`
    */
}
