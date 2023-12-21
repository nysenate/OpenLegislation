package gov.nysenate.openleg.spotchecks;

import com.google.common.base.Stopwatch;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.spotchecks.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class SpotcheckReportDaoIT extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(SpotcheckReportDaoIT.class);
    // A bill that only exists this test world.
    private static final BaseBillId billId = new BaseBillId("S999999", 2017);

    @Autowired
    private SpotCheckReportDao reportDao;
    private LocalDateTime start;

    @Before
    public void setup() {
       start = LocalDateTime.now();
    }

    /**
     * Test status updates when saving new mismatches
     */

    @Test
    public void testSaveNewMismatch() {
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> actual = queryMostRecentOpenMismatch();
        assertEquals(billId, actual.getKey());
        assertEquals(MismatchState.OPEN, actual.getState());
    }

    @Test
    public void testSaveExistingMismatch() {
        // Save new mismatch
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> newMismatch = queryMostRecentOpenMismatch();
        // Save same mismatch again
        reportDao.saveReport(createMismatchReport(start.plusMinutes(1)));
        DeNormSpotCheckMismatch<?> existingMismatch = queryMostRecentOpenMismatch();
        assertEquals(newMismatch.getFirstSeenDateTime(), existingMismatch.getFirstSeenDateTime());
    }

    @Test
    public void testSaveClosedMismatch() {
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> newMismatch = queryMostRecentOpenMismatch();
        // Then save report without a mismatch
        reportDao.saveReport(createEmptyReport(start.plusMinutes(1)));
        DeNormSpotCheckMismatch<?> closedMismatch = queryMostRecentClosedMismatch();
        assertEquals(MismatchState.CLOSED, closedMismatch.getState());
        assertEquals(newMismatch.getFirstSeenDateTime(), closedMismatch.getFirstSeenDateTime());
    }

    @Test
    public void regressionMismatchResetsFirstSeenDateTime() {
        // Save new mismatch
        reportDao.saveReport(createMismatchReport(start));
        // Resolve the mismatch
        reportDao.saveReport(createEmptyReport(start.plusMinutes(1)));
        DeNormSpotCheckMismatch<?> closedMismatch = queryMostRecentClosedMismatch();
        // Encounter it again
        reportDao.saveReport(createMismatchReport(start.plusMinutes(2)));
        DeNormSpotCheckMismatch<?> regressionMismatch = queryMostRecentOpenMismatch();
        assertEquals(MismatchState.OPEN, regressionMismatch.getState());
        assertNotEquals(closedMismatch.getFirstSeenDateTime(), regressionMismatch.getFirstSeenDateTime());
    }

    /**
     * Test setMismatchIgnoreStatus()
     */
    @Test(expected = IllegalArgumentException.class)
    public void givenNullIgnoreStatus_throwException() {
        reportDao.setMismatchIgnoreStatus(1, null);
    }

    @Ignore // queryMostRecentOpenMismatch is not reliable. Need to get Id's and use them.
    @Test
    public void canUpdateIgnoreStatus() {
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> mismatch = queryMostRecentOpenMismatch();
        reportDao.setMismatchIgnoreStatus(mismatch.getMismatchId(), SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        DeNormSpotCheckMismatch<?> actual = queryMostRecentOpenMismatch();
        assertEquals(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY, actual.getIgnoreStatus());
    }

    /**
     * Test add/delete issue id's
     */
    @Test
    public void canAddIssueId() {
        final String issueId = "10800";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> mismatch = queryMostRecentOpenMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        Set<String> actual = queryMostRecentOpenMismatch().getIssueIds();
        assertTrue(actual.contains(issueId));
    }

    @Test
    public void canAddMultipleIssueIds() {
        String issueId = "10800";
        String otherIssueId = "10899";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> mismatch = queryMostRecentOpenMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        reportDao.addIssueId(mismatch.getMismatchId(), otherIssueId);
        Set<String> actual = queryMostRecentOpenMismatch().getIssueIds();
        assertTrue(actual.contains(issueId));
        assertTrue(actual.contains(otherIssueId));
    }

    @Test
    public void duplicateIssuesNotSaved() {
        String issueId = "10800";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> mismatch = queryMostRecentOpenMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        Set<String> actual = queryMostRecentOpenMismatch().getIssueIds();
        assertEquals(actual, Set.of(issueId));
    }

    @Test
    public void canDeleteIssueIds() {
        String issueId = "10800";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch<?> mismatch = queryMostRecentOpenMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        reportDao.deleteIssueId(mismatch.getMismatchId(), issueId);
        Set<String> actual = queryMostRecentOpenMismatch().getIssueIds();
        assertTrue(actual.isEmpty());
    }

    /** Summary query tests */

    @Test
    public void canGetStatusSummary() {
        reportDao.getMismatchStatusSummary(LocalDate.now(), SpotCheckDataSource.LBDC,
                SpotCheckContentType.BILL, Set.of(SpotCheckMismatchIgnore.NOT_IGNORED));
    }

    @Test
    public void canGetMismatchTypeSummary() {
        reportDao.getMismatchTypeSummary(LocalDate.now(), SpotCheckDataSource.LBDC,
                SpotCheckContentType.BILL, MismatchStatus.EXISTING,
                Set.of(SpotCheckMismatchIgnore.NOT_IGNORED));
    }

    @Test
    public void canGetContentTypeSummary() {
        reportDao.getMismatchContentTypeSummary(LocalDate.now(), SpotCheckDataSource.LBDC,
                Set.of(SpotCheckMismatchIgnore.NOT_IGNORED));
    }

    @Test
    public void massiveSaveTest() {
        int session = 2017;

        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK,
                LocalDateTime.now(), LocalDateTime.now());
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>(reportId);

        List<BaseBillId> baseBillIds = new ArrayList<>();
        for (int i = 2; i < 400; i++) {
            String house = i % 2 == 0 ? "S" : "A";
            String printNo = house + i / 2;
            BaseBillId baseBillId = new BaseBillId(printNo, session);
            baseBillIds.add(baseBillId);
        }

        Random rando = new Random(1234567890);

        var typeList = new ArrayList<>(SpotCheckMismatchType.
                getMismatchTypes(reportId.getReferenceType()));
        baseBillIds.stream()
                .map(baseBillId -> {
                    var obs = new SpotCheckObservation<>(reportId.getReferenceId(), baseBillId);
                    while (rando.nextInt(10) == 0) {
                        SpotCheckMismatchType type = typeList.get(rando.nextInt(typeList.size()));
                        obs.addMismatch(new SpotCheckMismatch(type, "D:", ":D"));
                    }
                    return obs;
                })
                .forEach(report::addObservation);
        logger.info("Saving report, {} obs, {} mm", report.getObservedCount(),
                report.getOpenMismatchCount(false));
        Stopwatch sw = Stopwatch.createStarted();
        reportDao.saveReport(report);
        logger.info("done {}", sw.stop());
    }

    /* --- Internal Methods --- */

    private DeNormSpotCheckMismatch<?> queryMostRecentOpenMismatch() {
        MismatchQuery query = new MismatchQuery(start.toLocalDate(), SpotCheckDataSource.LBDC,
                MismatchStatus.OPEN, Set.of(SpotCheckContentType.BILL));
        return reportDao.getMismatches(query, LimitOffset.ALL).results().get(0);
    }

    private DeNormSpotCheckMismatch<?> queryMostRecentClosedMismatch() {
        MismatchQuery query = new MismatchQuery(start.toLocalDate(), SpotCheckDataSource.LBDC,
                MismatchStatus.RESOLVED, Collections.singleton(SpotCheckContentType.BILL));
        return reportDao.getMismatches(query, LimitOffset.ALL).results().get(0);
    }

    private static SpotCheckReport<BaseBillId> createMismatchReport(LocalDateTime refDateTime) {
        return createMismatchReport(refDateTime, SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    private static SpotCheckReport<BaseBillId> createMismatchReport(LocalDateTime refDateTime, SpotCheckMismatchIgnore ignoreStatus) {
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK,
                refDateTime, LocalDateTime.now());
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(reportId);
        SpotCheckObservation<BaseBillId> ob = new SpotCheckObservation<>(
                new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime), billId);
        SpotCheckMismatch mm = new SpotCheckMismatch(SpotCheckMismatchType.BILL_COSPONSOR,
                "ObservedSponsor", "ReferenceSponsor");
        mm.setIgnoreStatus(ignoreStatus);
        ob.addMismatch(mm);
        report.addObservation(ob);
        return report;
    }

    private static SpotCheckReport<BaseBillId> createEmptyReport(LocalDateTime refDateTime)  {
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK,
                refDateTime, LocalDateTime.now());
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(reportId);
        SpotCheckObservation<BaseBillId> ob = new SpotCheckObservation<>(
                new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime), billId);
        report.addObservation(ob);
        return report;
    }
}
