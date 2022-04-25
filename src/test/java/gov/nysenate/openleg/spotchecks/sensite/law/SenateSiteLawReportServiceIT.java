package gov.nysenate.openleg.spotchecks.sensite.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReportId;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDump;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpId;
import gov.nysenate.openleg.spotchecks.sensite.bill.LawSpotCheckId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckRefType.SENATE_SITE_LAW;

@Category(SillyTest.class)
public class SenateSiteLawReportServiceIT extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteLawReportServiceIT.class);

    @Autowired private SenateSiteLawReportService reportService;

    @Test
    public void Test() {
        final String testFilePath =
                "spotcheck/senatesite/law/report-service/senate-site-law_dump-2019-20190801T123143-001.json";
        File resourceFile = FileIOUtils.getResourceFile(testFilePath);
        SenateSiteDumpId dumpId = new SenateSiteDumpId(
                SENATE_SITE_LAW, 1, 2019, LocalDateTime.parse("2019-08-01T12:31:43")
        );
        SenateSiteDumpFragment fragment = new SenateSiteDumpFragment(dumpId, 1, resourceFile);
        SenateSiteDump dump = new SenateSiteDump(dumpId);
        dump.addDumpFragment(fragment);
        SpotCheckReportId reportId = new SpotCheckReportId(
                SENATE_SITE_LAW, dump.getDumpId().dumpTime(), LocalDateTime.now());
        SpotCheckReport<LawSpotCheckId> report = new SpotCheckReport<>(reportId);
        reportService.checkDump(dump, report);
        logger.info("{}", report.getMismatchTypeCounts(false));
        logger.info("yup");
    }

}