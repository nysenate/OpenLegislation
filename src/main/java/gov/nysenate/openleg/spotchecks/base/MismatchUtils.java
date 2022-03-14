package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.spotchecks.model.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class MismatchUtils {

    private MismatchUtils() {}

    /**
     * Updates the ignore status of the given reference.
     *
     * @param mismatch
     */
    public static void updateIgnoreStatus(DeNormSpotCheckMismatch<?> mismatch) {
        if (mismatch.getState() == MismatchState.CLOSED) {
            mismatch.setIgnoreStatus(ignoreStatusForClosed(mismatch));
        } else {
            mismatch.setIgnoreStatus(ignoreStatusForOpen(mismatch));
        }
    }

    private static SpotCheckMismatchIgnore ignoreStatusForClosed(DeNormSpotCheckMismatch<?> mismatch) {
        if (mismatch.getIgnoreStatus() == SpotCheckMismatchIgnore.IGNORE_PERMANENTLY) {
            return SpotCheckMismatchIgnore.IGNORE_PERMANENTLY;
        }
        return SpotCheckMismatchIgnore.NOT_IGNORED;
    }

    private static SpotCheckMismatchIgnore ignoreStatusForOpen(DeNormSpotCheckMismatch<?> mismatch) {
        if (mismatch.getIgnoreStatus() == SpotCheckMismatchIgnore.IGNORE_ONCE) {
            return SpotCheckMismatchIgnore.NOT_IGNORED;
        }
        return mismatch.getIgnoreStatus();
    }

    /**
     * Returns a list of mismatches that have been closed by a spotcheck report.
     * Mismatches are closed if they were checked by the report (in checkedKeys and checkedTypes),
     * are not in the report mismatches and are not already resolved.
     *
     * @param currentMismatches  All the most recent mismatches for the datasource checked by the report.
     * @param report             The report date time to set for any resolved mismatches.
     * @return A list of mismatches resolved by this report.
     */
    public static List<DeNormSpotCheckMismatch<?>> determineClosedMismatches(
            Collection<DeNormSpotCheckMismatch<?>> currentMismatches,
            SpotCheckReport<?> report) {

        Set<SpotCheckMismatchType> checkedTypes = report.getReferenceType().checkedMismatchTypes();
        Set<?> checkedKeys = report.getCheckedKeys();
        Set<?> observedMismatchKeys = report.getMismatchKeys();

        return currentMismatches.stream()
                .filter(m -> m.getState() != MismatchState.CLOSED)
                .filter(m -> checkedKeys.contains(m.getKey()))
                .filter(m -> checkedTypes.contains(m.getType()))
                .filter(m -> !observedMismatchKeys.contains(m.getMismatchKey()))
                .map(m -> closeMismatchWithReport(m, report))
                .collect(Collectors.toList());
    }

    /**
     * Closes a mismatch by the given report
     * Copy, and update the fields of the given mismatch to reflect it has been closed in this report.
     *
     * @param mm     A open mismatch that has been closed by the given report.
     * @param report
     * @return A copy of mm with fields updated to reflect it getting closed.
     */
    private static DeNormSpotCheckMismatch<?> closeMismatchWithReport(DeNormSpotCheckMismatch<?> mm, SpotCheckReport<?> report) {
        DeNormSpotCheckMismatch<?> closed = mm.copy();
        closed.setState(MismatchState.CLOSED);
        closed.setReportId(report.getId());
        closed.setReferenceId(new SpotCheckReferenceId(report.getReferenceType(), report.getReferenceDateTime()));
        closed.setReportDateTime(report.getReportDateTime());
        closed.setObservedDateTime(report.getReportDateTime());
        return closed;
    }

    /**
     * Sets the first seen date time for a mismatch.
     * Copies the firstSeenDateTime from the current mismatch to the report mismatches unless the report mismatch
     * is new or a regression, in which case the first seen date time is set to the observed date time.
     */
    public static void updateFirstSeenDateTime(DeNormSpotCheckMismatch<?> reportMismatch,
                                               Optional<DeNormSpotCheckMismatch<?>> savedMismatch) {
        if (savedMismatch.isEmpty() || savedMismatch.get().getState() == MismatchState.CLOSED) {
            reportMismatch.setFirstSeenDateTime(reportMismatch.getObservedDateTime());
        } else {
            reportMismatch.setFirstSeenDateTime(savedMismatch.get().getFirstSeenDateTime());
        }
    }
}
