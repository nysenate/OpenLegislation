package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.law.data.SqlFsLawFileDao;
import gov.nysenate.openleg.model.law.LawFile;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlFsLawFileDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsLawFileDaoTests.class);

    @Autowired
    private SqlFsLawFileDao lawDao;

    @Test
    public void testGetIncomingLawFiles() throws Exception {
        for (LawFile lawFile : lawDao.getIncomingLawFiles(SortOrder.ASC, LimitOffset.ALL)) {
            logger.info("{}", lawFile);
            lawFile.setPendingProcessing(true);
            lawDao.archiveAndUpdateLawFile(lawFile);
        }
    }

    @Test
    public void testGetPendingLawFiles() throws Exception {
        logger.info("{}", lawDao.getPendingLawFiles(SortOrder.ASC, LimitOffset.ALL));
    }
}