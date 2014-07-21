package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.util.OutputHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class SqlMemberServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberServiceTests.class);

    @Autowired
    private MemberService sqlMemberService;

    @Test
    public void testGetMemberByShortName_UsesCache() throws Exception {
        logger.info(OutputHelper.toJson(sqlMemberService.getMemberByLBDCName("MARTINS", 2013, Chamber.SENATE)));
        logger.info(OutputHelper.toJson(sqlMemberService.getMemberByLBDCName("MARTINS", 2013, Chamber.SENATE)));
    }
}
