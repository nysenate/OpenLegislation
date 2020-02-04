package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.MismatchUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class MismatchUtilsTest {

    private static final String printNo = "S100";
    private static final BaseBillId testBillId = new BaseBillId(printNo, 2017);
    private DeNormSpotCheckMismatch openMismatch;
    private DeNormSpotCheckMismatch closedMismatch;
    private SpotCheckReport<BaseBillId> spotcheckReport;
    private SpotCheckReferenceId referenceId;

    @Before
    public void before() {
        openMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.OPEN);
        closedMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.CLOSED);
        spotcheckReport = new SpotCheckReport<>(
                new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now(), LocalDateTime.now()));
        spotcheckReport.setId(2);
        referenceId = new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now());
    }

    /* --- Custom Asserts --- */

    private void assertEmpty(Collection<DeNormSpotCheckMismatch> collection) {
        assertTrue(collection.isEmpty());
    }

    private void assertIgnoreStatus(DeNormSpotCheckMismatch mismatch, SpotCheckMismatchIgnore ignoreStatus) {
        assertThat(mismatch.getIgnoreStatus(), is(ignoreStatus));
    }

    /* --- calculateIgnoreStatus() tests --- */

    @Test
    public void givenNotIgnored_returnNotIgnored() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.NOT_IGNORED);
        MismatchUtils.updateIgnoreStatus(openMismatch);
        assertIgnoreStatus(openMismatch, SpotCheckMismatchIgnore.NOT_IGNORED);
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.NOT_IGNORED);
        MismatchUtils.updateIgnoreStatus(closedMismatch);
        assertIgnoreStatus(closedMismatch, SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    @Test
    public void givenIgnoreOnce_returnNotIgnored() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_ONCE);
        MismatchUtils.updateIgnoreStatus(openMismatch);
        assertIgnoreStatus(openMismatch, SpotCheckMismatchIgnore.NOT_IGNORED);

        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_ONCE);
        MismatchUtils.updateIgnoreStatus(closedMismatch);
        assertIgnoreStatus(closedMismatch, SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    @Test
    public void givenIgnorePermanently_returnIgnorePermanently() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        MismatchUtils.updateIgnoreStatus(openMismatch);
        assertIgnoreStatus(openMismatch, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        MismatchUtils.updateIgnoreStatus(closedMismatch);
        assertIgnoreStatus(closedMismatch, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
    }

    @Test
    public void givenOpenIgnoreUntilResolved_returnIgnoreUntilResolved() {
        openMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
        MismatchUtils.updateIgnoreStatus(openMismatch);
        assertIgnoreStatus(openMismatch, SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
    }

    @Test
    public void givenClosedIgnoreUntilResolved_returnNotIgnored() {
        closedMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
        MismatchUtils.updateIgnoreStatus(closedMismatch);
        assertIgnoreStatus(closedMismatch, SpotCheckMismatchIgnore.NOT_IGNORED);
    }

    /* --- determineClosedMismatches() tests --- */

    @Test
    public void givenEmptyCurrentMismatches_returnNoResolved() {
        assertEmpty(MismatchUtils.determineClosedMismatches(new ArrayList<>(), spotcheckReport));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedKeys_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedTypes_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        spotcheckReport.setReportId(new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_AGENDA, LocalDateTime.now(), LocalDateTime.now()));
        assertFalse(spotcheckReport.getReferenceType().checkedMismatchTypes().contains(openMismatch.getType()));
        spotcheckReport.addObservation(new SpotCheckObservation<>(referenceId, testBillId));
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenMismatchInReportAndCurrent_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        SpotCheckObservation<BaseBillId> obs = new SpotCheckObservation<>(referenceId, testBillId);
        SpotCheckMismatch mm = new SpotCheckMismatch(openMismatch.getType(), "", "");
        obs.addMismatch(mm);
        spotcheckReport.addObservation(obs);
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenResolved_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(closedMismatch);
        spotcheckReport.setObservationMap(new HashMap<>());
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenMismatchOnlyInCurrent_returnResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        spotcheckReport.addObservation(new SpotCheckObservation<>(referenceId, testBillId));
        DeNormSpotCheckMismatch resolved = MismatchUtils.determineClosedMismatches(current, spotcheckReport).get(0);
        assertThat(resolved.getState(), is(MismatchState.CLOSED));
    }

    @Test
    public void givenResolvedMismatch_updateFields() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        LocalDateTime origReportDateTime = openMismatch.getReportDateTime();
        LocalDateTime origObservedDateTime = openMismatch.getObservedDateTime();
        spotcheckReport = aFutureSpotcheckReport(spotcheckReport);
        spotcheckReport.addObservation(new SpotCheckObservation<>(referenceId, testBillId));

        List<DeNormSpotCheckMismatch> closedMismatches = MismatchUtils.determineClosedMismatches(current, spotcheckReport);
        assertTrue("at least 1 closed", closedMismatches.size() > 0);

        DeNormSpotCheckMismatch resolved = closedMismatches.get(0);

        assertThat(resolved.getReportId(), is(spotcheckReport.getId()));
        assertThat(resolved.getReferenceId().getReferenceType(), is(spotcheckReport.getReferenceType()));
        assertThat(resolved.getReferenceId().getRefActiveDateTime(), is(spotcheckReport.getReferenceDateTime()));
        assertThat(resolved.getState(), is(MismatchState.CLOSED));
        assertThat(resolved.getReportDateTime(), is(greaterThan(origReportDateTime)));
        assertThat(resolved.getObservedDateTime(), is(greaterThan(origObservedDateTime)));
    }

    private <T> SpotCheckReport<T> aFutureSpotcheckReport(SpotCheckReport<T> spotcheckReport) {
        return new SpotCheckReport<>(
                new SpotCheckReportId(
                        SpotCheckRefType.LBDC_SCRAPED_BILL,
                        spotcheckReport.getReferenceDateTime().plusHours(1),
                        spotcheckReport.getReportDateTime().plusHours(1)
                )
        );
    }

    /* --- First Seen Date Time Tests --- */

    @Test
    public void newMismatchGetsNewFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch newMismatch = openMismatch;
        MismatchUtils.updateFirstSeenDateTime(newMismatch, Optional.empty());
        assertThat(newMismatch.getFirstSeenDateTime(), is(newMismatch.getObservedDateTime()));
    }

    @Test
    public void reoccurringMismatchCopiesFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch reoccurringMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.OPEN);
        reoccurringMismatch.setObservedDateTime(LocalDateTime.now().plusHours(1));
        MismatchUtils.updateFirstSeenDateTime(reoccurringMismatch, Optional.of(openMismatch));
        assertThat(reoccurringMismatch.getFirstSeenDateTime(), is(openMismatch.getFirstSeenDateTime()));
    }

    @Test
    public void closedMismatchCopiesFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch closedMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.CLOSED);
        closedMismatch.setFirstSeenDateTime(null);
        closedMismatch.setState(MismatchState.CLOSED);
        MismatchUtils.updateFirstSeenDateTime(closedMismatch, Optional.of(openMismatch));
        assertThat(closedMismatch.getFirstSeenDateTime(), is(openMismatch.getFirstSeenDateTime()));
    }

    @Test
    public void regressionMismatchResetsFirstSeenDateTime() {
        LocalDateTime dt = LocalDateTime.now();
        closedMismatch.setFirstSeenDateTime(dt);
        closedMismatch.setObservedDateTime(dt);
        openMismatch.setObservedDateTime(dt.plusHours(1));
        MismatchUtils.updateFirstSeenDateTime(openMismatch, Optional.of(closedMismatch));
        assertThat(openMismatch.getFirstSeenDateTime(), is(greaterThan(closedMismatch.getFirstSeenDateTime())));
        assertThat(openMismatch.getFirstSeenDateTime(), is(openMismatch.getObservedDateTime()));
    }

    private DeNormSpotCheckMismatch createMismatch(SpotCheckMismatchType type, MismatchState state) {
        DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch<>(testBillId, type, SpotCheckDataSource.LBDC);
        mismatch.setReportId(1);
        mismatch.setState(state);
        mismatch.setReferenceId(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        mismatch.setReportDateTime(LocalDateTime.now());
        mismatch.setObservedDateTime(LocalDateTime.now());
        mismatch.setFirstSeenDateTime(LocalDateTime.now());
        return mismatch;
    }
}