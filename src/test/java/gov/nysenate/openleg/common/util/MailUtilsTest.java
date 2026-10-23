package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class MailUtilsTest {

    @Test
    public void completeSettingsHaveNoProblems() {
        assertEquals(List.of(), problems("smtp.example.org", "openleg@example.org", false, "", ""));
        assertEquals(List.of(), problems("smtp.example.org", "OpenLeg <openleg@example.org>", true, "user", "pass"));
    }

    @Test
    public void blankHostIsReported() {
        assertEquals(List.of("mail.smtp.host is blank"), problems(" ", "openleg@example.org", false, "", ""));
    }

    @Test
    public void senderMustBeOneValidAddress() {
        assertEquals(List.of("mail.smtp.from is blank"), problems("smtp.example.org", "", false, "", ""));
        assertEquals(List.of("mail.smtp.from is not a valid address"),
                problems("smtp.example.org", "not an address", false, "", ""));
        assertEquals(List.of("mail.smtp.from is not a single address"),
                problems("smtp.example.org", "a@example.org, b@example.org", false, "", ""));
        assertEquals(List.of("mail.smtp.from is not a single address"),
                problems("smtp.example.org", "team:a@example.org,b@example.org;", false, "", ""));
        assertEquals(List.of("mail.smtp.from is not a valid address"),
                problems("smtp.example.org", "openleg", false, "", ""));
    }

    @Test
    public void authenticationNeedsCredentials() {
        assertEquals(List.of("mail.smtp.user is blank but mail.smtp.auth is true",
                        "mail.smtp.password is blank but mail.smtp.auth is true"),
                problems("smtp.example.org", "openleg@example.org", true, "", ""));
    }

    private static List<String> problems(String host, String from, boolean auth, String user, String password) {
        OpenLegEnvironment env = mock(OpenLegEnvironment.class);
        when(env.getEmailFromAddress()).thenReturn(from);
        return new MailUtils(host, "25", auth, user, password, false, false, false, "TLSv1.3",
                "imaps", "TLSv1.3", "5000", "5000", "5000", env).getSmtpSettingProblems();
    }
}
