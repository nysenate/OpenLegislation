package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.service.spotcheck.senatesite.SenateSiteBillJsonParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class SenateSiteBillDumpTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteBillDumpTests.class);

    @Autowired SenateSiteDao senateSiteDao;
    @Autowired SenateSiteBillJsonParser billJsonParser;

    @Test
    public void parseDumps() throws Exception {
        Collection<SenateSiteDump> dumps = senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_BILLS);
        SenateSiteDump dump = dumps.stream().findAny().orElseThrow(RuntimeException::new);
        Collection<SenateSiteBill> bills = billJsonParser.parseBills(dump);
        logger.info("glerp");
    }

    @Test
    public void setProcessedTest() throws Exception {
        Collection<SenateSiteDump> dumps = senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_BILLS);
        for (SenateSiteDump dump : dumps) {
            senateSiteDao.setProcessed(dump);
        }
    }
}
