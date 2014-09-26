package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.law.LawFileDao;
import gov.nysenate.openleg.model.law.LawFile;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LawParserTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(LawParserTests.class);

    @Autowired
    private LawFileDao lawFileDao;

    @Test
    public void testExtractDocuments() throws Exception {
        LawFile lawFile = lawFileDao.getPendingLawFiles(LimitOffset.ONE).get(0);
        LawParser parser = new LawParser(lawFile);
        parser.getRawDocuments().forEach(r -> logger.info(r.getHeader()));
    }
}
