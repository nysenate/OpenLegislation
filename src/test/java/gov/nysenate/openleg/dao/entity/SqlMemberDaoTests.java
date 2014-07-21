package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.util.OutputHelper;
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
        logger.info(OutputHelper.toJson(memberDao.getMembersByLBDCName("JOHNSON C", Chamber.SENATE)));
    }

    @Test
    public void testGetMemberById() throws Exception {
        logger.info(OutputHelper.toJson(memberDao.getMemberById(459, 2013)));
    }
}
