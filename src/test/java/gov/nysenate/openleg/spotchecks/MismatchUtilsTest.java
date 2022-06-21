package gov.nysenate.openleg.spotchecks;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.spotchecks.base.MismatchUtils;
import gov.nysenate.openleg.spotchecks.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.spotchecks.model.MismatchState.CLOSED;
import static gov.nysenate.openleg.spotchecks.model.MismatchState.OPEN;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckDataSource.LBDC;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckRefType.LBDC_DAYBREAK;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class MismatchUtilsTest {

    private static final String printNo = "S100";
    private static final BaseBillId testBillId = new BaseBillId(printNo, 2017);
    private DeNormSpotCheckMismatch<?> openMismatch;
    private DeNormSpotCheckMismatch<?> closedMismatch;
    private SpotCheckReport<BaseBillId> spotcheckReport;
    private SpotCheckReferenceId referenceId;

    @Before
    public void before() {
        openMismatch = createMismatch(OPEN);
        closedMismatch = createMismatch(CLOSED);
        spotcheckReport = new SpotCheckReport<>(new SpotCheckReportId(LBDC_DAYBREAK,
                LocalDateTime.now(), LocalDateTime.now()));
        spotcheckReport.setId(2);
        referenceId = new SpotCheckReferenceId(LBDC_DAYBREAK, LocalDateTime.now());
    }

    /* --- Custom Asserts --- */

    private void assertEmpty(Collection<DeNormSpotCheckMismatch<?>> collection) {
        assertTrue(collection.isEmpty());
    }

    private void assertIgnoreStatus(DeNormSpotCheckMismatch<?> mismatch,
                                    SpotCheckMismatchIgnore ignoreStatus) {
        assertEquals(ignoreStatus, mismatch.getIgnoreStatus());
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
        List<DeNormSpotCheckMismatch<?>> current = Lists.newArrayList(openMismatch);
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedTypes_returnNoResolved() {
        List<DeNormSpotCheckMismatch<?>> current = Lists.newArrayList(openMismatch);
        spotcheckReport.setReportId(new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_AGENDA,
                LocalDateTime.now(), LocalDateTime.now()));
        assertFalse(spotcheckReport.getReferenceType().checkedMismatchTypes().contains(openMismatch.getType()));
        spotcheckReport.addObservation(new SpotCheckObservation<>(referenceId, testBillId));
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenMismatchInReportAndCurrent_returnNoResolved() {
        List<DeNormSpotCheckMismatch<?>> current = Lists.newArrayList(openMismatch);
        SpotCheckObservation<BaseBillId> obs = new SpotCheckObservation<>(referenceId, testBillId);
        SpotCheckMismatch mm = new SpotCheckMismatch(openMismatch.getType(), "", "");
        obs.addMismatch(mm);
        spotcheckReport.addObservation(obs);
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenResolved_returnNoResolved() {
        List<DeNormSpotCheckMismatch<?>> current = Lists.newArrayList(closedMismatch);
        spotcheckReport.setObservationMap(new HashMap<>());
        assertEmpty(MismatchUtils.determineClosedMismatches(current, spotcheckReport));
    }

    @Test
    public void givenMismatchOnlyInCurrent_returnResolved() {
        List<DeNormSpotCheckMismatch<?>> current = Lists.newArrayList(openMismatch);
        spotcheckReport.addObservation(new SpotCheckObservation<>(referenceId, testBillId));
        DeNormSpotCheckMismatch<?> resolved = MismatchUtils
                .determineClosedMismatches(current, spotcheckReport).get(0);
        assertEquals(CLOSED, resolved.getState());
    }

    @Test
    public void givenResolvedMismatch_updateFields() {
        List<DeNormSpotCheckMismatch<?>> current = Lists.newArrayList(openMismatch);
        LocalDateTime origReportDateTime = openMismatch.getReportDateTime();
        LocalDateTime origObservedDateTime = openMismatch.getObservedDateTime();
        spotcheckReport = aFutureSpotcheckReport(spotcheckReport);
        spotcheckReport.addObservation(new SpotCheckObservation<>(referenceId, testBillId));

        List<DeNormSpotCheckMismatch<?>> closedMismatches = MismatchUtils
                .determineClosedMismatches(current, spotcheckReport);
        assertFalse(closedMismatches.isEmpty());

        DeNormSpotCheckMismatch<?> resolved = closedMismatches.get(0);
        assertEquals(spotcheckReport.getId(), resolved.getReportId());
        assertEquals(CLOSED, resolved.getState());
        isGreaterThan(resolved.getReportDateTime(), origReportDateTime);
        isGreaterThan(resolved.getObservedDateTime(), origObservedDateTime);
        var refId = resolved.getReferenceId();
        assertEquals(spotcheckReport.getReferenceType(), refId.getReferenceType());
        assertEquals(spotcheckReport.getReferenceDateTime(), refId.getRefActiveDateTime());
    }

    private <T> SpotCheckReport<T> aFutureSpotcheckReport(SpotCheckReport<T> spotcheckReport) {
        return new SpotCheckReport<>(new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL,
                spotcheckReport.getReferenceDateTime().plusHours(1),
                spotcheckReport.getReportDateTime().plusHours(1))
        );
    }

    /* --- First Seen Date Time Tests --- */

    @Test
    public void newMismatchGetsNewFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch<?> newMismatch = openMismatch;
        MismatchUtils.updateFirstSeenDateTime(newMismatch, Optional.empty());
        assertEquals(newMismatch.getObservedDateTime(), newMismatch.getFirstSeenDateTime());
    }

    @Test
    public void reoccurringMismatchCopiesFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch<?> reoccurringMismatch = createMismatch(OPEN);
        reoccurringMismatch.setObservedDateTime(LocalDateTime.now().plusHours(1));
        MismatchUtils.updateFirstSeenDateTime(reoccurringMismatch, Optional.of(openMismatch));
        assertEquals(openMismatch.getFirstSeenDateTime(), reoccurringMismatch.getFirstSeenDateTime());
    }

    @Test
    public void closedMismatchCopiesFirstSeenDateTime() {
        openMismatch.setObservedDateTime(LocalDateTime.now());
        DeNormSpotCheckMismatch<?> closedMismatch = createMismatch(CLOSED);
        closedMismatch.setFirstSeenDateTime(null);
        closedMismatch.setState(CLOSED);
        MismatchUtils.updateFirstSeenDateTime(closedMismatch, Optional.of(openMismatch));
        assertEquals(openMismatch.getFirstSeenDateTime(), closedMismatch.getFirstSeenDateTime());
    }

    @Test
    public void regressionMismatchResetsFirstSeenDateTime() {
        LocalDateTime dt = LocalDateTime.now();
        closedMismatch.setFirstSeenDateTime(dt);
        closedMismatch.setObservedDateTime(dt);
        openMismatch.setObservedDateTime(dt.plusHours(1));
        MismatchUtils.updateFirstSeenDateTime(openMismatch, Optional.of(closedMismatch));
        isGreaterThan(openMismatch.getFirstSeenDateTime(), closedMismatch.getFirstSeenDateTime());
        assertEquals(openMismatch.getObservedDateTime(), openMismatch.getFirstSeenDateTime());
    }

    private static DeNormSpotCheckMismatch<?> createMismatch(MismatchState state) {
        var mismatch = new DeNormSpotCheckMismatch<>(testBillId, BILL_ACTIVE_AMENDMENT, LBDC);
        mismatch.setReportId(1);
        mismatch.setState(state);
        mismatch.setReferenceId(new SpotCheckReferenceId(LBDC_DAYBREAK,
                LocalDateTime.now()));
        mismatch.setReportDateTime(LocalDateTime.now());
        mismatch.setObservedDateTime(LocalDateTime.now());
        mismatch.setFirstSeenDateTime(LocalDateTime.now());
        return mismatch;
    }

    private static <T> void isGreaterThan(Comparable<T> one, T two) {
        assertTrue(one.compareTo(two) > 0);
    }
}