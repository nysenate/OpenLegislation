package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class SqlMemberServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberServiceTest.class);

    @Autowired
    public MemberService sqlMemberService;

    @Test
    public void testGetMemberByShortName_UsesCache() {
        logger.info(OutputUtils.toJson(sqlMemberService.getSessionMemberBySessionId(667)));
    }

    @Test(expected = MemberNotFoundEx.class)
    public void testGetMemberBySessionNegativeId() {
        logger.info(OutputUtils.toJson(sqlMemberService.getSessionMemberById(-1, SessionYear.current())));
    }

    @Test(expected = MemberNotFoundEx.class)
    public void testGetMemberByNegativeId() {
        logger.info(OutputUtils.toJson(sqlMemberService.getFullMemberById(-1)));
    }
}
