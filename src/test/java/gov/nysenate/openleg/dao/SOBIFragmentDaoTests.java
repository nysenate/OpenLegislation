package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SobiFileDao;
import gov.nysenate.openleg.dao.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SOBIFragmentDaoTests extends BaseTests
{
    private static final Logger logger = Logger.getLogger(SOBIFragmentDaoTests.class);

    @Autowired
    private SobiFileDao sobiFileDao;

    @Autowired
    private SobiFragmentDao sobiFragmentDao;

    @Test
    public void getSOBIFragmentsTest() throws Exception {
        SobiFile sobiFile = sobiFileDao.getPendingSobiFiles(SortOrder.ASC, 20, 0).get(10);
        for (SobiFragment fragment : sobiFragmentDao.getSOBIFragments(sobiFile, SortOrder.ASC)) {
            logger.info(fragment);
            logger.info(fragment.getText());
            logger.info(fragment.getParentSobiFile().getText());
        }
    }

    @Test
    public void getSOBIFragmentByFileName() throws Exception {
        String fileName = "SOBI.D120209.T170421.TXT-bill-1.sobi";
        SobiFragmentType type = SobiFragmentType.BILL;
        logger.info(sobiFragmentDao.getSOBIFragment(type, fileName).getText());
        logger.info(sobiFragmentDao.getSOBIFragment(type, fileName).getParentSobiFile().getText());

    }
}
