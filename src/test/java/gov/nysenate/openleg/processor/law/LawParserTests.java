package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.law.data.LawFileDao;
import gov.nysenate.openleg.model.law.LawFile;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LawParserTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(LawParserTests.class);

    @Autowired
    private LawFileDao lawFileDao;

    @Test
    public void testExtractDocuments() throws Exception {
        LawFile lawFile = lawFileDao.getPendingLawFiles(SortOrder.ASC, LimitOffset.TEN).get(6);
        //LawParser parser = new LawParser(lawFile);
        logger.info("{}", lawFile);
//        parser.getLawBlocks().stream().filter(l -> !l.isConsolidated()).
//                forEach(r -> logger.info(r.getLawId() + " : " + r.getLocationId() + " : " + r.isConsolidated()));
    }
}
