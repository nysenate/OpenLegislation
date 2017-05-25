package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.MismatchUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MismatchUtilsTest {

    private static final String printNo = "S100";
    private DeNormSpotCheckMismatch openMismatch;
    private DeNormSpotCheckMismatch closedMismatch;
    private List<DeNormSpotCheckMismatch> reportMismatches;

    @Before
    public void before() {
        openMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.OPEN);
        closedMismatch = createMismatch(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT, MismatchState.CLOSED);
        reportMismatches = Lists.newArrayList(openMismatch);
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
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), new ArrayList<>(), new HashSet<>(),
                new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedKeys_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, new HashSet<>(), types,
                LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenCurrentMismatchNotInCheckedTypes_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, keys, new HashSet<>(),
                LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenMismatchInReportAndCurrent_returnNoResolved() {
        List<DeNormSpotCheckMismatch> report = Lists.newArrayList(openMismatch);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);
        assertEmpty(MismatchUtils.deriveClosedMismatches(report, current, keys, types, LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenResolved_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(closedMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        assertEmpty(MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, keys, types, LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void givenMismatchOnlyInCurrent_returnResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, keys, types,
                LocalDateTime.now(), LocalDateTime.now()).get(0);
        assertThat(resolved.getState(), is(MismatchState.CLOSED));
    }

    @Test
    public void resolvedMismatchDatesAreUpdated() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(openMismatch);
        LocalDateTime originalReferenceDate = openMismatch.getReferenceId().getRefActiveDateTime();
        LocalDateTime originalReportDateTime = openMismatch.getReportDateTime();
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        DeNormSpotCheckMismatch resolved = MismatchUtils.deriveClosedMismatches(new ArrayList<>(), current, keys, types,
                originalReportDateTime.plusHours(1), originalReferenceDate.plusHours(1)).get(0);
        assertThat(resolved.getState(), is(MismatchState.CLOSED));
        assertThat(resolved.getReferenceId().getRefActiveDateTime(), is(greaterThan(originalReferenceDate)));
        assertThat(resolved.getReportDateTime(), is(greaterThan(originalReportDateTime)));
    }

    private DeNormSpotCheckMismatch createMismatch(SpotCheckMismatchType type, MismatchState status) {
        DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch(new BillId(printNo, 2017), type, SpotCheckDataSource.LBDC);
        mismatch.setState(status);
        mismatch.setReferenceId(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        mismatch.setReportDateTime(LocalDateTime.now());
        return mismatch;
    }
}