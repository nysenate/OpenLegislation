package gov.nysenate.openleg.service.spotcheck.senatesite.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.law.LawSpotCheckId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpId;
import gov.nysenate.openleg.util.FileIOUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckRefType.SENATE_SITE_LAW;

@Category(SillyTest.class)
public class SenateSiteLawReportServiceTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteLawReportServiceTest.class);

    @Autowired private SenateSiteLawReportService reportService;

    @Test
    public void Test() {
        final String testFilePath =
                "spotcheck/senatesite/law/report-service/senate-site-law_dump-2019-20190801T123143-001.json";
        File resourceFile = FileIOUtils.getResourceFile(testFilePath);
        SenateSiteDumpId dumpId = new SenateSiteDumpId(
                SpotCheckRefType.SENATE_SITE_LAW, 1, 2019, LocalDateTime.parse("2019-08-01T12:31:43")
        );
        SenateSiteDumpFragment fragment = new SenateSiteDumpFragment(dumpId, 1);
        fragment.setFragmentFile(resourceFile);
        SenateSiteDump dump = new SenateSiteDump(dumpId);
        dump.addDumpFragment(fragment);
        SpotCheckReportId reportId = new SpotCheckReportId(
                SENATE_SITE_LAW, dump.getDumpId().getDumpTime(), LocalDateTime.now());
        SpotCheckReport<LawSpotCheckId> report = new SpotCheckReport<>(reportId);
        reportService.checkDump(dump, report);
        logger.info("{}", report.getMismatchTypeCounts(false));
        logger.info("yup");
    }

}