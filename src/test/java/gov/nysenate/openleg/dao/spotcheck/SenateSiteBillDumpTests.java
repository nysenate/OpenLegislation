package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteBillDao;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBill;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDump;
import gov.nysenate.openleg.service.spotcheck.senatesite.SenateSiteBillJsonParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class SenateSiteBillDumpTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteBillDumpTests.class);

    @Autowired SenateSiteBillDao senateSiteBillDao;
    @Autowired SenateSiteBillJsonParser billJsonParser;

    @Test
    public void parseDumps() throws Exception {
        Collection<SenateSiteBillDump> dumps = senateSiteBillDao.getPendingDumps();
        SenateSiteBillDump dump = dumps.stream().findAny().orElseThrow(RuntimeException::new);
        Collection<SenateSiteBill> bills = billJsonParser.parseBills(dump);
        logger.info("glerp");
    }

    @Test
    public void setProcessedTest() throws Exception {
        Collection<SenateSiteBillDump> dumps = senateSiteBillDao.getPendingDumps();
        for (SenateSiteBillDump dump : dumps) {
            senateSiteBillDao.setProcessed(dump);
        }
    }
}
