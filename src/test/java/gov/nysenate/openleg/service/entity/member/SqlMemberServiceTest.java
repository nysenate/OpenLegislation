package gov.nysenate.openleg.service.entity.member;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
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
    private MemberService sqlMemberService;

    @Test
    public void testGetMemberByShortName_UsesCache() throws Exception {
        logger.info(OutputUtils.toJson(sqlMemberService.getMemberBySessionId(667)));
    }
}
