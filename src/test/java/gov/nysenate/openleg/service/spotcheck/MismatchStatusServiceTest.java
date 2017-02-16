package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.DeNormSpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckDataSource;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.service.spotcheck.base.MismatchStatusService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /** --- deriveStatuses() tests --- */

    @Test
    public void givenEmptyLists_returnEmptyList() {
        assertTrue(MismatchStatusService.deriveStatuses(new ArrayList<>(), new ArrayList<>()).isEmpty());
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
        assertTrue(MismatchStatusService.deriveStatuses(new ArrayList<>(), currentMismatches).isEmpty());
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

    /** --- deriveResolved() tests --- */

    @Test
    public void givenEmptyCurrentMismatches_returnNoResolved() {
        assertTrue(MismatchStatusService.deriveResolved(new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new HashSet<>()).isEmpty());
    }

    @Test
    public void givenCurrentMismatchNotInCheckedKeys_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        List<DeNormSpotCheckMismatch> resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, new HashSet<>(), types);
        assertTrue(resolved.isEmpty());
    }

    @Test
    public void givenCurrentMismatchNotInCheckedTypes_returnNoResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));

        List<DeNormSpotCheckMismatch> resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, new HashSet<>());
        assertTrue(resolved.isEmpty());
    }

    @Test
    public void givenMismatchInReportAndCurrent_returnNoResolved() {
        List<DeNormSpotCheckMismatch> report = Lists.newArrayList(newMismatch);
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        List<DeNormSpotCheckMismatch> resolved = MismatchStatusService.deriveResolved(report, current, keys, types);
        assertTrue(resolved.isEmpty());
    }

    @Test
    public void givenMismatchOnlyInCurrent_returnResolved() {
        List<DeNormSpotCheckMismatch> current = Lists.newArrayList(newMismatch);
        Set<Object> keys = Sets.newHashSet(new BillId(printNo, 2017));
        Set<SpotCheckMismatchType> types = Sets.newHashSet(SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT);

        List<DeNormSpotCheckMismatch> resolved = MismatchStatusService.deriveResolved(new ArrayList<>(), current, keys, types);
        assertThat(resolved.get(0).getStatus(), is(SpotCheckMismatchStatus.RESOLVED));
    }

    private DeNormSpotCheckMismatch createMismatch(SpotCheckMismatchType type, SpotCheckMismatchStatus status) {
        DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch(new BillId(printNo, 2017), type, SpotCheckDataSource.LBDC);
        mismatch.setStatus(status);
        return mismatch;
    }
}