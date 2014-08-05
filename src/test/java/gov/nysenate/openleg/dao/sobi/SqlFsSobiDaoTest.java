package gov.nysenate.openleg.dao.sobi;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.util.OutputHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SqlFsSobiDaoTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsSobiDaoTest.class);

    @Autowired
    private SqlFsSobiDao sobiDao;

    @Test
    public void testMisc() throws Exception {
//        List<SobiFile> sobiFiles = sobiDao.getIncomingSobiFiles(SortOrder.ASC, LimitOffset.ALL);
//        for (SobiFile sobiFile : sobiFiles) {
//            sobiDao.archiveSobiFile(sobiFile);
//            sobiDao.updateSobiFile(sobiFile);
//            logger.info("{}", sobiFile);
//        }
        SobiFile sobiFile = sobiDao.getSobiFile("SOBI.D130102.T104813.TXT");

    }

    @Test
    public void testGetSobiFile() throws Exception {

    }

    @Test
    public void testGetSobiFiles() throws Exception {

    }

    @Test
    public void testGetSobiFilesDuring() throws Exception {
//        List<SobiFile> sobiFiles = sobiDao.getSobiFilesDuring(LocalDate.of(2013, 1, 1), LocalDate.of(2013, 2, 1),
//                SortOrder.ASC, LimitOffset.ALL);
//        logger.info("{}", OutputHelper.toJson(sobiFiles));
    }

    @Test
    public void testGetIncomingSobiFiles() throws Exception {

    }

    @Test
    public void testGetSobiFragment() throws Exception {

    }

    @Test
    public void testGetSobiFragments() throws Exception {

    }

    @Test
    public void testGetSobiFragments1() throws Exception {

    }

    @Test
    public void testGetPendingSobiFragments() throws Exception {

    }

    @Test
    public void testGetPendingSobiFragments_filterSet() throws Exception {
        ImmutableSet<SobiFragmentType> types = ImmutableSet.of(SobiFragmentType.AGENDA);
        List<SobiFragment> fragments =
            sobiDao.getPendingSobiFragments(types, SortOrder.ASC, LimitOffset.ALL);
        fragments.stream().forEach(f -> logger.info(f.getFragmentId()));
    }

    @Test
    public void testArchiveSobiFile() throws Exception {

    }

    @Test
    public void testUpdateSobiFile() throws Exception {

    }

    @Test
    public void testUpdateSobiFragment() throws Exception {

    }
}
