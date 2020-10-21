package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(IntegrationTest.class)
public class SqlMemberDaoIT extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberDaoIT.class);

    @Autowired
    private SqlMemberDao memberDao;

    // todo make this into a proper integration test
//    @Test
    public void testGetMember() throws Exception {
        logger.info(OutputUtils.toJson(memberDao.getMembersByShortName("JOHNSON C", Chamber.SENATE)));
    }

    // todo make this into a proper integration test
//    @Test
    public void testGetMemberById() throws Exception {
        logger.info(OutputUtils.toJson(memberDao.getMemberById(459, SessionYear.of(2013))));
    }

    @Test
    public void testGetMemberBySessionMemberId() throws Exception {
        final int sessionMemberId = 306;
        final String shortName = "PEOPLES-STOKES";
        SessionMember member = memberDao.getMemberBySessionId(sessionMemberId);
//        logger.info(OutputUtils.toJson(member));
        assertNotNull(member);
        assertEquals(sessionMemberId, member.getSessionMemberId());
        assertEquals(shortName, member.getLbdcShortName());
    }
}
