package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.spotcheck.MismatchStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertTrue;

public class MismatchStatusUtilsTests {

    private LocalDateTime reportEndDateTime;
    private int reportLength;

    @Before
    public void setup() {
        reportLength = 1;
        reportEndDateTime = LocalDateTime.of(2017, 4, 28, 20, 11, 32);
    }

    @Test
    public void newReturnsStartOfPeriod() {
        MismatchStatusUtils utils = new MismatchStatusUtils(reportLength);
        LocalDateTime actualDate = utils.getStatusStartDateTime(MismatchStatus.NEW, reportEndDateTime);
        LocalDateTime expectedDate = reportEndDateTime.truncatedTo(ChronoUnit.DAYS);
        assertTrue(actualDate.equals(expectedDate));

        utils = new MismatchStatusUtils(7);
        actualDate = utils.getStatusStartDateTime(MismatchStatus.NEW, reportEndDateTime);
        expectedDate = LocalDateTime.of(2017, 4, 22, 0, 0);
        assertTrue(actualDate.equals(expectedDate));
    }

    @Test
    public void resolvedReturnsStartOfPeriod() {
        MismatchStatusUtils utils = new MismatchStatusUtils(reportLength);
        LocalDateTime actualDate = utils.getStatusStartDateTime(MismatchStatus.RESOLVED, reportEndDateTime);
        LocalDateTime expectedDate = reportEndDateTime.truncatedTo(ChronoUnit.DAYS);
        assertTrue(actualDate.equals(expectedDate));

        utils = new MismatchStatusUtils(7);
        actualDate = utils.getStatusStartDateTime(MismatchStatus.RESOLVED, reportEndDateTime);
        expectedDate = LocalDateTime.of(2017, 4, 22, 0, 0);
        assertTrue(actualDate.equals(expectedDate));
    }

    @Test
    public void existingReturnsStartOfSession() {
        MismatchStatusUtils utils = new MismatchStatusUtils(reportLength);
        LocalDateTime actualDate = utils.getStatusStartDateTime(MismatchStatus.EXISTING, reportEndDateTime);
        LocalDateTime expectedDate = LocalDateTime.of(2017, 1, 1, 0, 0);
        assertTrue(actualDate.equals(expectedDate));
    }

    // TODO Existing status determines end date time as well!
    // Service should take status and report date?
    // Utils gives range containing report period derived from status and report date?
    @Ignore
    @Test
    public void existingReturnsStartOfSessiondd() {
        MismatchStatusUtils utils = new MismatchStatusUtils(reportLength);
        LocalDateTime actualDate = utils.getStatusStartDateTime(MismatchStatus.EXISTING, reportEndDateTime);
        LocalDateTime expectedDate = LocalDateTime.of(2017, 1, 1, 0, 0);
        assertTrue(actualDate.equals(expectedDate));
    }

    // TODO MismatchStatus also determines MismatchState.

    // TODO *** Put these util methods in MismatchQuery object? When else will they need to be used??
    //          - MismatchQuery is not a singleton, how to handle report length param??
    //              - Non singleton bean?
    //              - static final field?
}
