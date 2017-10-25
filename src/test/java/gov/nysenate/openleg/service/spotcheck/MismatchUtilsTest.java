package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.MismatchUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class MismatchUtilsTest {

    private static final String printNo = "S100";
    private DeNormSpotCheckMismatch openMismatch;
    private DeNormSpotCheckMismatch closedMismatch;
    private List<DeNormSpotCheckMismatch> reportMismatches;
    private SpotCheckReport spotcheckReport;
    private SpotCheckReferenceId referenceId;

    @Before
    public void before() {
        openMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.OPEN);
        closedMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.CLOSED);
        reportMismatches = Lists.newArrayList(openMismatch);
        spotcheckReport = new SpotCheckReport(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now(), LocalDateTime.now()));
        spotcheckReport.setId(2);
        referenceId = new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now());
    }

    /** --- Custom Asserts --- */

    private void assertEmpty(List<DeNormSpotCheckMismatch> list) {
        assertTrue(list.isEmpty());
    }

    private void assertIgnoreStatus(List<DeNormSpotCheckMismatch> reportMismatches, SpotCheckMismatchIgnore ignoreStatus) {
        reportMismatches.forEach(m ->
            assertThat(m.getIgnoreStatus(), is(ignoreStatus)));
    }

    /** --- calculateIgnoreStatus() tests --- */

    @Test
    public void givenNotIgnored_returnNotIgnored() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.NOT_IGNORED);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(openMismatch)), SpotCheckMismatchIgnore.NOT_IGNORED);
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.NOT_IGNORED);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(closedMismatch)), SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    @Test
    public void givenIgnoreOnce_returnNotIgnored() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_ONCE);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(openMismatch)), SpotCheckMismatchIgnore.NOT_IGNORED);
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_ONCE);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(closedMismatch)), SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    @Test
    public void givenIgnorePermanently_returnIgnorePermanently() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(openMismatch)), SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(closedMismatch)), SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
    }

    @Test
    public void givenOpenIgnoreUntilResolved_returnIgnoreUntilResolved() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
        List<DeNormSpotCheckMismatch> reportMismatches = Lists.newArrayList(openMismatch);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(reportMismatches), SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
    }

    @Test
    public void givenClosedIgnoreUntilResolved_returnNotIgnored() {
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
        assertIgnoreStatus(MismatchUtils.updateIgnoreStatus(Lists.newArrayList(closedMismatch)), SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    /** --- deriveClosedMismatches() tests --- */

    @Test
    public void givenEmptyCurrentMismatches_returnNoResolved() {
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), new ArrayList<>(), spotcheckReport));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedKeys_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, spotcheckReport));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedTypes_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        spotcheckReport.setReportId(new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_AGENDA, LocalDateTime.now(), LocalDateTime.now()));
        spotcheckReport.addObservation(new SpotCheckObservation(referenceId, new BillId(printNo, 2017)));
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, spotcheckReport));
    }

    @Test
    public void givenMismatchInReportAndCurrent_returnNoResolved() {
        List<DeNormSpotCheckMismatch> report = Lists.newArrayList(openMismatch);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        spotcheckReport.addObservation(new SpotCheckObservation(referenceId, new BillId(printNo, 2017)));
        assertEmpty(MismatchUtils.deriveClosedMismatches(report, current, spotcheckReport));
    }

    @Test
    public void givenResolved_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(closedMismatch);
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, spotcheckReport));
    }

    @Test
    public void givenMismatchOnlyInCurrent_returnResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        spotcheckReport.addObservation(new SpotCheckObservation(referenceId, new BillId(printNo, 2017)));
        DeNormSpotCheckMismatch resolved = MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, spotcheckReport).get(0);
        assertThat(resolved.getState(), is(MismatchState.CLOSED));
    }

    @Test
    public void givenResolvedMismatch_updateFields() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        LocalDateTime origReportDateTime = openMismatch.getReportDateTime();
        LocalDateTime origObservedDateTime = openMismatch.getObservedDateTime();
        spotcheckReport = aFutureSpotcheckReport(spotcheckReport);
        spotcheckReport.addObservation(new SpotCheckObservation(referenceId, new BillId(printNo, 2017)));

        DeNormSpotCheckMismatch resolved = MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, spotcheckReport).get(0);

        assertThat(resolved.getReportId(), is(spotcheckReport.getId()));
        assertThat(resolved.getReferenceId().getReferenceType(), is(spotcheckReport.getReferenceType()));
        assertThat(resolved.getReferenceId().getRefActiveDateTime(), is(spotcheckReport.getReferenceDateTime()));
        assertThat(resolved.getState(), is(MismatchState.CLOSED));
        assertThat(resolved.getReportDateTime(), is(greaterThan(origReportDateTime)));
        assertThat(resolved.getObservedDateTime(), is(greaterThan(origObservedDateTime)));
    }

    private SpotCheckReport aFutureSpotcheckReport(SpotCheckReport spotcheckReport) {
        return new SpotCheckReport(new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL, spotcheckReport.getReferenceDateTime().plusHours(1), spotcheckReport.getReportDateTime().plusHours(1)));
    }

    /**
     * --- First Seen Date Time Tests ---
     */

    @Test
    public void newMismatchGetsNewFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch newMismatch = MismatchUtils.updateFirstSeenDateTime(Lists.newArrayList(openMismatch), new ArrayList<>()).get(0);
        assertThat(newMismatch.getFirstSeenDateTime(), is(newMismatch.getObservedDateTime()));
    }

    @Test
    public void reoccurringMismatchCopiesFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch reoccurringMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.OPEN);
        reoccurringMismatch.setObservedDateTime(LocalDateTime.now().plusHours(1));
        reoccurringMismatch = MismatchUtils.updateFirstSeenDateTime(Lists.newArrayList(reoccurringMismatch), Lists.newArrayList(openMismatch)).get(0);
        assertThat(reoccurringMismatch.getFirstSeenDateTime(), is(openMismatch.getFirstSeenDateTime()));
    }

    @Test
    public void closedMismatchCopiesFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch closedMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.CLOSED);
        closedMismatch.setFirstSeenDateTime(null);
        closedMismatch.setState(MismatchState.CLOSED);
        closedMismatch = MismatchUtils.updateFirstSeenDateTime(Lists.newArrayList(closedMismatch), Lists.newArrayList(openMismatch)).get(0);
        assertThat(closedMismatch.getFirstSeenDateTime(), is(openMismatch.getFirstSeenDateTime()));
    }

    @Test
    public void regressionMismatchResetsFirstSeenDateTime() {
        LocalDateTime dt = LocalDateTime.now();
        closedMismatch.setFirstSeenDateTime(dt);
        closedMismatch.setObservedDateTime(dt);
        openMismatch.setObservedDateTime(dt.plusHours(1));
        openMismatch = MismatchUtils.updateFirstSeenDateTime(Lists.newArrayList(openMismatch), Lists.newArrayList(closedMismatch)).get(0);
        assertThat(openMismatch.getFirstSeenDateTime(), is(greaterThan(closedMismatch.getFirstSeenDateTime())));
        assertThat(openMismatch.getFirstSeenDateTime(), is(openMismatch.getObservedDateTime()));
    }

    private DeNormSpotCheckMismatch createMismatch(SpotCheckMismatchType type, MismatchState state) {
        DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch(new BillId(printNo, 2017), type, SpotCheckDataSource.LBDC);
        mismatch.setReportId(1);
        mismatch.setState(state);
        mismatch.setReferenceId(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        mismatch.setReportDateTime(LocalDateTime.now());
        mismatch.setObservedDateTime(LocalDateTime.now());
        mismatch.setFirstSeenDateTime(LocalDateTime.now());
        return mismatch;
    }
}