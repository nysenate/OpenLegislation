package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.MismatchStatusService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MismatchStatusServiceTest {

    private static final String printNo = "S100";
    private DeNormSpotCheckMismatch newMismatch;
    private DeNormSpotCheckMismatch existingMismatch;
    private DeNormSpotCheckMismatch resolvedMismatch;
    private DeNormSpotCheckMismatch regressionMismatch;
    private List<DeNormSpotCheckMismatch> reportMismatches;

    @Before
    public void before() {
        newMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, SpotCheckMismatchStatus.NEW);
        existingMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, SpotCheckMismatchStatus.EXISTING);
        resolvedMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, SpotCheckMismatchStatus.RESOLVED);
        regressionMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, SpotCheckMismatchStatus.REGRESSION);
        reportMismatches = Lists.newArrayList(newMismatch);
    }

    private void assertEmpty(List<DeNormSpotCheckMismatch> list) {
        assertTrue(list.isEmpty());
    }

    /**
     * --- deriveStatuses() tests ---
     */

    @Test
    public void givenEmptyLists_returnEmptyList() {
        assertEmpty(MismatchStatusService.deriveStatuses(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void givenNoCurrentMismatches_returnNewMismatch() {
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, new ArrayList<>()).get(0);
        assertThat(actual, is(newMismatch));
        assertThat(actual.getStatus(), is(SpotCheckMismatchStatus.NEW));
    }

    @Test
    public void givenEmptyReportMismatches_returnEmptyList() {
        List<DeNormSpotCheckMismatch> currentMismatches = Lists.newArrayList(newMismatch);
        assertEmpty(MismatchStatusService.deriveStatuses(new ArrayList<>(), currentMismatches));
    }

    @Test
    public void givenCurrentMismatchOfNew_returnExisting() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getStatus(), is(SpotCheckMismatchStatus.EXISTING));
    }

    @Test
    public void givenCurrentMismatchOfExisting_returnExisting() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getStatus(), is(SpotCheckMismatchStatus.EXISTING));
    }

    @Test
    public void givenCurrentMismatchOfResolved_returnRegression() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(resolvedMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getStatus(), is(SpotCheckMismatchStatus.REGRESSION));
    }

    @Test
    public void givenCurrentMismatchOfRegression_returnRegression() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(regressionMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getStatus(), is(SpotCheckMismatchStatus.REGRESSION));
    }

    @Test
    public void givenIgnoredPermanently_returnPermanentlyIgnoredMismatch() {
        existingMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getIgnoreStatus(), is(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY));
    }

    @Test
    public void givenIgnoredUntilResolved_returnIgnoredUntilResolvedMismatch() {
        existingMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getIgnoreStatus(), is(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED));
    }

    @Test
    public void givenIgnoredOnce_returnsNotIgnoredMismatch() {
        existingMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_ONCE);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        DeNormSpotCheckMismatch actual = MismatchStatusService.deriveStatuses(reportMismatches, current).get(0);
        assertThat(actual.getIgnoreStatus(), is(SpotCheckMismatchIgnore.NOT_IGNORED));
    }

    /**
     * --- deriveResolved() tests ---
     */

    @Test
    public void givenEmptyCurrentMismatches_returnNoResolved() {
        assertEmpty(MismatchStatusService.deriveResolved(new ArrayList<>(), new ArrayList<>(), new HashSet<>(),
                new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedKeys_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);
        assertEmpty(MismatchStatusService.deriveResolved(new ArrayList<>(), current, new HashSet<>(), types,
                LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedTypes_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        assertEmpty(MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, new HashSet<>(),
                LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenMismatchInReportAndCurrent_returnNoResolved() {
        List<DeNormSpotCheckMismatch> report = Lists.newArrayList(newMismatch);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        assertEmpty(MismatchStatusService.deriveResolved(report, current, keys, types, LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenResolved_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(resolvedMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        assertEmpty(MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types, LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenMismatchOnlyInCurrent_returnResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types,
                LocalDateTime.now(), LocalDateTime.now()).get(0);
        assertThat(resolved.getStatus(), is(SpotCheckMismatchStatus.RESOLVED));
    }

    @Test
    public void resolvedMismatchDatesAreUpdated() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        LocalDateTime originalReferenceDate = newMismatch.getReferenceId().getRefActiveDateTime();
        LocalDateTime originalReportDateTime = newMismatch.getReportDateTime();
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types,
                originalReportDateTime.plusHours(1), originalReferenceDate.plusHours(1)).get(0);
        assertThat(resolved.getStatus(), is(SpotCheckMismatchStatus.RESOLVED));
        assertThat(resolved.getReferenceId().getRefActiveDateTime(), is(greaterThan(originalReferenceDate)));
        assertThat(resolved.getReportDateTime(), is(greaterThan(originalReportDateTime)));
    }

    @Test
    public void givenIgnoreOnce_returnNotIgnoredMismatch() {
        existingMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_ONCE);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types,
                LocalDateTime.now(), LocalDateTime.now()).get(0);
        assertThat(resolved.getIgnoreStatus(), is(SpotCheckMismatchIgnore.NOT_IGNORED));
    }

    @Test
    public void givenIgnoredUntilResolved_returnNotIgnoredMismatch() {
        existingMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types,
                LocalDateTime.now(), LocalDateTime.now()).get(0);
        assertThat(resolved.getIgnoreStatus(), is(SpotCheckMismatchIgnore.NOT_IGNORED));
    }

    @Test
    public void givenIgnoredPermanently_returnIgnoredPermanentlyResolvedMismatch() {
        existingMismatch.setIgnoreStatus(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(existingMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types,
                LocalDateTime.now(), LocalDateTime.now()).get(0);
        assertThat(resolved.getIgnoreStatus(), is(SpotCheckMismatchIgnore.IGNORE_PERMANENTLY));
    }

    private DeNormSpotCheckMismatch createMismatch(SpotCheckMismatchType type, SpotCheckMismatchStatus status) {
        DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch(new BillId(printNo, 2017), type, SpotCheckDataSource.LBDC);
        mismatch.setStatus(status);
        mismatch.setReferenceId(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        mismatch.setReportDateTime(LocalDateTime.now());
        return mismatch;
    }
}