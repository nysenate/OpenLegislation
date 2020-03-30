package gov.nysenate.openleg.service.entity.member;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.util.OutputUtils;
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
