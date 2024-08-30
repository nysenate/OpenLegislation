package gov.nysenate.openleg.legislation.law.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.processors.law.LawFile;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class SqlFsLawFileDaoTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsLawFileDaoTest.class);

    @Autowired
    private SqlFsLawFileDao lawDao;

    @Test
    public void testGetIncomingLawFiles() throws Exception {
        for (LawFile lawFile : lawDao.getIncomingLawFiles()) {
            logger.info("{}", lawFile);
            lawFile.setPendingProcessing(true);
            lawDao.archiveAndUpdateLawFile(lawFile);
        }
    }

    @Test
    public void testGetPendingLawFiles() {
        logger.info("{}", lawDao.getPendingLawFiles(SortOrder.ASC, LimitOffset.ALL));
    }
}