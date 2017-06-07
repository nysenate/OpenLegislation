package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FsSobiDaoTest /*extends BaseTests */ {
    private static final Logger logger = LoggerFactory.getLogger(FsSobiDaoTest.class);
    
    @Autowired
    private FsSobiDao sobiDao;
    
    @Autowired
    private SobiFragmentDao fragmentDao;

    @Test
    public void testGetSobiFile() throws Exception{
        
    }
    
    @Test
    public void testGetSobiFiles() throws Exception{
        
    }
    
    @Test
    public void testGetSobiFilesDuring() throws Exception{
//        List<SobiFile> sobiFiles = sobiDao.getSobiFilesDuring(LocalDate.of(2013, 1, 1), LocalDate.of(2013, 2, 1),
//                SortOrder.ASC, LimitOffset.ALL);
//        logger.info("{}", OutputUtils.toJson(sobiFiles));
    }
    
    @Test
    public void testGetIncomingSobiFiles() throws Exception{
        
    }
    
    @Test
    public void testGetSobiFragment() throws Exception{
        logger.info("{}", fragmentDao.getSobiFragment("SOBI.D130110.T120242.TXT-0-BILL"));
    }
    
    @Test
    public void testGetSobiFragments() throws Exception{
        
    }
    
    @Test
    public void testGetSobiFragments1() throws Exception{
        
    }
    
    @Test
    public void testGetPendingSobiFragments() throws Exception{
        
    }
    
    @Test
    public void testGetPendingSobiFragments_filterSet() throws Exception{
        ImmutableSet<SobiFragmentType> types = ImmutableSet.of(SobiFragmentType.AGENDA);
        List<SobiFragment> fragments =
                fragmentDao.getPendingSobiFragments(types, SortOrder.ASC, LimitOffset.ALL);
        fragments.stream().forEach(f -> logger.info(f.getFragmentId()));
    }
    
    @Test
    public void testArchiveSobiFile() throws Exception{
        
    }
    
    @Test
    public void testUpdateSobiFile() throws Exception{
        
    }
    
    @Test
    public void testUpdateSobiFragment() throws Exception{
        
    }
}
