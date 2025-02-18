package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(IntegrationTest.class)
public class SqlMemberDaoIT extends BaseTests {
    @Autowired
    private SqlMemberDao memberDao;

    @Test
    public void testGetMemberBySessionMemberId() {
        final int sessionMemberId = 306;
        final String shortName = "PEOPLES-STOKES";
        SessionMember member = memberDao.getMemberBySessionId(sessionMemberId);
        assertNotNull(member);
        assertEquals(sessionMemberId, member.getSessionMemberId());
        assertEquals(shortName, member.getLbdcShortName());
    }
}
