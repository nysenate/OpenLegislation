package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class SpotCheckSummary {

    /** The number of occurrences for each mismatch type in the report, divided by mismatch status */
    protected Table<SpotCheckMismatchType, SpotCheckMismatchStatus,
            Table<SpotCheckMismatchIgnore, SpotCheckMismatchTracked, Long>> mismatchCounts;

    public SpotCheckSummary() {
        this.mismatchCounts = HashBasedTable.create();
    }

    /** --- Functional Getters / Setters --- */

    /** Record a type/status count */
    public void addMismatchTypeCount(SpotCheckMismatchType type, SpotCheckMismatchStatus status,
                                     SpotCheckMismatchIgnore ignoreStatus, boolean tracked, long count) {
        if (!mismatchCounts.contains(type, status)) {
            mismatchCounts.put(type, status, HashBasedTable.create());
        }
        mismatchCounts.get(type, status)
                .put(ignoreStatus, SpotCheckMismatchTracked.getFromBoolean(tracked), count);
    }

    public <T> void addCountsFromObservations(Collection<SpotCheckObservation<T>> observations) {
        observations.stream()
                .flatMap(obs -> obs.getMismatches().values().stream())
                .forEach(mismatch -> {
                    if (!mismatchCounts.contains(mismatch.getMismatchType(), mismatch.getStatus())) {
                        mismatchCounts.put(mismatch.getMismatchType(), mismatch.getStatus(), HashBasedTable.create());
                    }
                    Table<SpotCheckMismatchIgnore, SpotCheckMismatchTracked, Long> ignoreTrackedTable =
                            mismatchCounts.get(mismatch.getMismatchType(), mismatch.getStatus());
                    long existingValue = Optional.ofNullable(
                            ignoreTrackedTable.get(mismatch.getIgnoreStatus(), mismatch.getTracked()))
                            .orElse(0L);
                    ignoreTrackedTable.put(mismatch.getIgnoreStatus(), mismatch.getTracked(), existingValue + 1);
                });
    }

    public Map<SpotCheckMismatchStatus, Long> getMismatchStatuses() {
        return mismatchCounts.cellSet().stream()
                .map(cell -> ImmutablePair.of(cell.getColumnKey(), cell.getValue().values().stream().reduce(0L, Long::sum)))
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight, Long::sum));
    }

    /** --- Getters --- */

    public Table<SpotCheckMismatchType, SpotCheckMismatchStatus,
            Table<SpotCheckMismatchIgnore, SpotCheckMismatchTracked, Long>> getMismatchCounts() {
        return mismatchCounts;
    }
}
