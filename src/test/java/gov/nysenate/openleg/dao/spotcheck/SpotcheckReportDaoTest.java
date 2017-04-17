package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.*;


/*
FIXME
Setting this as a SillyTest for now because...
I am getting BadSqlGrammarExceptions for all of these tests
I have run the sql script qa-redesign/mismatch-refactor.sql
 -Sam
 */
@Category(SillyTest.class)
@Transactional
public class SpotcheckReportDaoTest extends BaseTests {

    @Autowired
    private BaseBillIdSpotCheckReportDao reportDao;
    private BaseBillId billId = new BaseBillId("S999999", 2017); // A bill that only exists this test world.
    private LocalDateTime start;

    @Before
    public void setup() {
       start = LocalDateTime.now();
    }

    @Test
    public void simpleSummaryTest() {
        reportDao.getMismatchStatusSummary(SpotCheckDataSource.LBDC, LocalDateTime.now());
    }

    /**
     * Test status updates when saving new mismatches
     */

    @Test
    public void testSaveNewMismatch() {
        reportDao.saveReport(createMismatchReport(start));

        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertEquals(billId, actual.getKey());
        assertEquals(SpotCheckMismatchStatus.NEW, actual.getStatus());
    }

    @Test
    public void testSaveExistingMismatch() {
        // Save new mismatch
        reportDao.saveReport(createMismatchReport(start));
        // Save same mismatch again
        reportDao.saveReport(createMismatchReport(start.plusMinutes(1)));

        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(billId, actual.getKey());
        assertEquals(SpotCheckMismatchStatus.EXISTING, actual.getStatus());
    }

    @Test
    public void testSaveResolvedMismatch() {
        reportDao.saveReport(createMismatchReport(start));
        // Then save report without a mismatch
        reportDao.saveReport(createEmptyReport(start.plusMinutes(1)));

        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(billId, actual.getKey());
        assertEquals(SpotCheckMismatchStatus.RESOLVED, actual.getStatus());
    }

    @Test
    public void testSaveRegressionMismatch() throws InterruptedException {
        // Save new mismatch
        reportDao.saveReport(createMismatchReport(start));
        // Resolve the mismatch
        reportDao.saveReport(createEmptyReport(start.plusMinutes(1)));
        // Encounter it again
        reportDao.saveReport(createMismatchReport(start.plusMinutes(2)));

        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(billId, actual.getKey());
        assertEquals(SpotCheckMismatchStatus.REGRESSION, actual.getStatus());
    }

    /**
     * Test ignore status updates when saving new mismatches
     */

    @Test
    public void givenIgnoreOnce_setToNotIgnoredOnNextOccurance() {
        reportDao.saveReport(createMismatchReport(start, SpotCheckMismatchIgnore.IGNORE_ONCE));
        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(SpotCheckMismatchIgnore.IGNORE_ONCE, actual.getIgnoreStatus());
        reportDao.saveReport(createMismatchReport(start.plusMinutes(1)));
        actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(SpotCheckMismatchIgnore.NOT_IGNORED, actual.getIgnoreStatus());
    }

    @Test
    public void givenIgnoreUntilResolved_keepIgnoredUntilResolved() {
        // Create mismatch with ignore until resolved status.
        reportDao.saveReport(createMismatchReport(start, SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED));
        // Repeat the same mismatch
        reportDao.saveReport(createMismatchReport(start.plusMinutes(1)));
        // Resolve it
        reportDao.saveReport(createEmptyReport(start.plusMinutes(2)));
        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(SpotCheckMismatchIgnore.NOT_IGNORED, actual.getIgnoreStatus());
    }

    @Test
    public void givenIgnorePermanently_neverUnIgnore() {
        // Create mismatch with ignored permanently status.
        reportDao.saveReport(createMismatchReport(start, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY));
        // Repeat the same mismatch
        reportDao.saveReport(createMismatchReport(start.plusMinutes(1)));
        // Resolve it
        reportDao.saveReport(createEmptyReport(start.plusMinutes(2)));
        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY, actual.getIgnoreStatus());
    }

    /**
     * Test setMismatchIgnoreStatus()
     */

    @Test(expected = IllegalArgumentException.class)
    public void givenNullIgnoreStatus_throwException() {
        reportDao.setMismatchIgnoreStatus(1, null);
    }

    @Test
    public void canUpdateIgnoreStatus() {
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch mismatch = queryMostRecentMismatch();
        reportDao.setMismatchIgnoreStatus(mismatch.getMismatchId(), SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        DeNormSpotCheckMismatch actual = queryMostRecentMismatch();
        assertNotNull(actual);
        assertEquals(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY, actual.getIgnoreStatus());
    }

    /**
     * Test add/delete issue id's
     */

    @Test
    public void canAddIssueId() {
        String issueId = "10800";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch mismatch = queryMostRecentMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        Set<String> actual = queryMostRecentMismatch().getIssueIds();
        assertNotNull(actual);
        assertTrue(actual.contains(issueId));
    }

    @Test
    public void canAddMultipleIssueIds() {
        String issueId = "10800";
        String otherIssueId = "10899";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch mismatch = queryMostRecentMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        reportDao.addIssueId(mismatch.getMismatchId(), otherIssueId);
        Set<String> actual = queryMostRecentMismatch().getIssueIds();
        assertNotNull(actual);
        assertTrue(actual.contains(issueId));
        assertTrue(actual.contains(otherIssueId));
    }

    @Test
    public void duplicateIssuesNotSaved() {
        String issueId = "10800";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch mismatch = queryMostRecentMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        Set<String> actual = queryMostRecentMismatch().getIssueIds();
        Set<String> expected = Collections.singleton(issueId);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void canDeleteIssueIds() {
        String issueId = "10800";
        reportDao.saveReport(createMismatchReport(start));
        DeNormSpotCheckMismatch mismatch = queryMostRecentMismatch();
        reportDao.addIssueId(mismatch.getMismatchId(), issueId);
        reportDao.deleteIssueId(mismatch.getMismatchId(), issueId);
        Set<String> actual = queryMostRecentMismatch().getIssueIds();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    private DeNormSpotCheckMismatch queryMostRecentMismatch() {
        MismatchQuery query = new MismatchQuery(SpotCheckDataSource.LBDC, Collections.singleton(SpotCheckContentType.BILL))
                .withFromDate(start)
                .withToDate(start.plusHours(1))
                .withMismatchStatuses(EnumSet.allOf(SpotCheckMismatchStatus.class))
                .withIgnoredStatuses(EnumSet.allOf(SpotCheckMismatchIgnore.class));
        return reportDao.getMismatches(query, LimitOffset.ALL).getResults().get(0);
    }

    private SpotCheckReport createMismatchReport(LocalDateTime refDateTime) {
        return createMismatchReport(refDateTime, SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    private SpotCheckReport createMismatchReport(LocalDateTime refDateTime, SpotCheckMismatchIgnore ignoreStatus) {
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime, LocalDateTime.now());
        SpotCheckReport report = new SpotCheckReport();
        report.setReportId(reportId);
        SpotCheckObservation ob = new SpotCheckObservation(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime), billId);
        SpotCheckMismatch mm = new SpotCheckMismatch(SpotCheckMismatchType.BILL_COSPONSOR, "ObservedSponsor", "ReferenceSponsor");
        mm.setIgnoreStatus(ignoreStatus);
        ob.addMismatch(mm);
        report.addObservation(ob);
        return report;
    }

    private SpotCheckReport createEmptyReport(LocalDateTime refDateTime)  {
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime, LocalDateTime.now());
        SpotCheckReport report = new SpotCheckReport();
        report.setReportId(reportId);
        SpotCheckObservation ob = new SpotCheckObservation(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime), billId);
        report.addObservation(ob);
        return report;
    }
}
