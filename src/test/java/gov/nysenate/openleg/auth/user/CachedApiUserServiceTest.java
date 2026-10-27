package gov.nysenate.openleg.auth.user;

import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.notifications.mail.SendMailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CachedApiUserServiceTest {
    private SqlApiUserDao apiUserDao;
    private SendMailService sendMailService;
    private CachedApiUserService service;

    @Before
    public void setUp() {
        apiUserDao = mock(SqlApiUserDao.class);
        sendMailService = mock(SendMailService.class);
        OpenLegEnvironment environment = mock(OpenLegEnvironment.class);
        when(environment.getUrl()).thenReturn("https://legislation.example.org");
        service = new CachedApiUserService(apiUserDao, sendMailService, environment);
    }

    /** Addresses batch email would skip, so signup doesn't accept them. */
    private static final String[] INVALID = {"still bad@example.com", "not-an-address", "a..b@x.com", ".a@x.com",
            "a@localhost.lan", "a@-x.com", "a@x_y.com"};

    @Test
    public void signupRefusesAnInvalidAddress() {
        for (String email : INVALID) {
            assertThrows(email, IllegalArgumentException.class,
                    () -> service.registerNewUser(email, "Name", "", Set.of()));
        }
        verifyNoInteractions(apiUserDao, sendMailService);
    }

    @Test
    public void signupAcceptsAnAddressWithAPlusOrAnApostrophe() {
        for (String email : new String[] {"a_b@x.com", "a+b@x.com", "o'brien@x.com"}) {
            ApiUser user = service.registerNewUser(email, "Name", "", Set.of());

            assertEquals(email, user.getEmail());
            verify(sendMailService).sendMessage(eq(email), any(), any());
            verify(apiUserDao).insertUser(user);
        }
    }
}
