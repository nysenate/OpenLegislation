package gov.nysenate.openleg.dao.entity.member;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.entity.member.data.SqlMemberDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlMemberDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberDaoTests.class);

    @Autowired
    private SqlMemberDao memberDao;

    @Test
    public void testGetMember() throws Exception {
        logger.info(OutputUtils.toJson(memberDao.getMembersByShortName("JOHNSON C", Chamber.SENATE)));
    }

    @Test
    public void testGetMemberById() throws Exception {
        logger.info(OutputUtils.toJson(memberDao.getMemberById(459, SessionYear.of(2013))));
    }

    @Test
    public void testGetMemberBySessionMemberId() throws Exception {
        SessionMember member = memberDao.getMemberBySessionId(661);
        logger.info(OutputUtils.toJson(member));
        assert(member.getSessionMemberId()==306);
        assert(member.getLbdcShortName().equals("PEOPLES-STOKES"));
    }
}
