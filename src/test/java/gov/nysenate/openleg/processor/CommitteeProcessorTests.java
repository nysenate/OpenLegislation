package gov.nysenate.openleg.processor;


import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SOBIFileDao;
import gov.nysenate.openleg.dao.sobi.SOBIFragmentDao;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.model.sobi.SOBIFragmentType;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.processors.entity.CommitteeProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class CommitteeProcessorTests extends BaseTests{

    @Autowired
    protected DataProcessor dataProcessor;
    @Autowired
    protected SOBIFileDao sobiFileDao;
    @Autowired
    protected SOBIFragmentDao sobiFragmentDao;
    @Autowired
    protected CommitteeProcessor committeeProcessor;

    @Test
    public void committeeProcessorTest() throws Exception{
        dataProcessor.stage(null, null);
        dataProcessor.collate();

        for (SOBIFile sobiFile : sobiFileDao.getPendingSOBIFiles(SortOrder.ASC)) {
            List<SOBIFragment> fragments = sobiFragmentDao.getSOBIFragments(sobiFile, SortOrder.ASC);
            for (SOBIFragment fragment : fragments) {
                if(fragment.getType()== SOBIFragmentType.COMMITTEE){
                    committeeProcessor.process(fragment);
                }
            }
            sobiFile.incrementProcessedCount();
            sobiFile.setProcessedDateTime(new Date());
            sobiFile.setPendingProcessing(false);
            sobiFileDao.updateSOBIFile(sobiFile);
        }
    }
}
