package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SobiFile;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.*;

public class SqlSobiDaoTests extends BaseTests
{
    private static final Logger logger = Logger.getLogger(SqlSobiDaoTests.class);

    SqlSobiDao sqlSOBIDao = new SqlSobiDao();

    @Test
    public void stageSOBIFiles_stagesFilesProperly() throws Exception {
        sqlSOBIDao.stageSobiFiles(true);
    }

    @Test
    public void getSOBIFile() throws Exception {
        SobiFile sobiFile = sqlSOBIDao.getSobiFile("SOBI.D090101.T0000000.TXT");
        logger.info(sobiFile);
    }

    @Test
    public void getSOBIFiles_returnsHashMap() throws Exception {
        Map<String, SobiFile> map = sqlSOBIDao.getSobiFiles(
                Arrays.asList("lel", "SOBI.D090101.T000000.TXT", "SOBI.D090609.T045500.TXT", "SOBI.D090612.T165206.TXT"));
        logger.info(map);
    }

    @Test
    public void getSOBIFilesDuring_returnsList() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2009, Calendar.JANUARY, 1);
        Date start = cal.getTime();
        cal.set(2009, Calendar.DECEMBER, 31);
        Date end = cal.getTime();
        List<SobiFile> sobiFileList = sqlSOBIDao.getSobiFilesDuring(start, end, false, SortOrder.ASC);
        logger.info(sobiFileList);
    }

    @Test
    public void getPendingSOBIFiles_returnsList() throws Exception {
        List<SobiFile> sobiFiles = sqlSOBIDao.getPendingSobiFiles(SortOrder.ASC, 0, 0);
        logger.info(sobiFiles);
    }

    @Test
    public void deleteEverything() throws Exception {
        sqlSOBIDao.deleteAll();
    }

    @Test
    public void updateSOBIFile() throws Exception {
        List<SobiFile> sobiFiles = sqlSOBIDao.getPendingSobiFiles(SortOrder.ASC, 0, 0);
        for (SobiFile sobiFile : sobiFiles) {
            sobiFile.setPendingProcessing(false);
            sobiFile.setProcessedCount(sobiFile.getProcessedCount() + 1);
            sobiFile.setProcessedDateTime(new Date());
            sqlSOBIDao.updateSobiFile(sobiFile);
        }
    }
}
