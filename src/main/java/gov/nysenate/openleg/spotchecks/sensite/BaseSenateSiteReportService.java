package gov.nysenate.openleg.spotchecks.sensite;

import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;
import gov.nysenate.openleg.spotchecks.model.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReportId;
import gov.nysenate.openleg.spotchecks.sensite.bill.SenateSiteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Base class for NYSenate.gov report services.
 *
 * Handles retrieval of NYSenate.gov node dumps and creating report objects.
 * @param <ContentKey>
 */
public abstract class BaseSenateSiteReportService<ContentKey> implements SpotCheckReportService<ContentKey> {

    private static final Logger logger = LoggerFactory.getLogger(BaseSenateSiteReportService.class);

    @Autowired
    private SenateSiteDao senateSiteDao;

    @Override
    public synchronized SpotCheckReport<ContentKey> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        SenateSiteDump dump = getMostRecentDump();
        SpotCheckReportId reportId = new SpotCheckReportId(
                getSpotcheckRefType(), dump.getDumpId().dumpTime(), LocalDateTime.now());
        SpotCheckReport<ContentKey> report = new SpotCheckReport<>(reportId);
        report.setNotes(dump.getDumpId().getNotes());
        try {
            checkDump(dump, report);
        } finally {
            logger.info("archiving {} dump...", getSpotcheckRefType());
            senateSiteDao.archiveDump(dump);
        }
        return report;
    }

    /**
     * Use the given dump to perform checks, saving mismatches to the given report.
     */
    protected abstract void checkDump(SenateSiteDump dump, SpotCheckReport<ContentKey> report);

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(getSpotcheckRefType()).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full " + getSpotcheckRefType() + " dumps"));
    }
}
