package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class SpotCheckSummary {

    /** The number of occurrences for each mismatch type in the report, divided by mismatch status */
    protected Table<SpotCheckMismatchType, SpotCheckMismatchStatus, Long> mismatchTypes;

    /** The number of occurrences for each ignored mismatch type in the report, divided by mismatch status */
    protected Table<SpotCheckMismatchType, SpotCheckMismatchStatus, Long> ignoredMismatchTypes;

    public SpotCheckSummary() {
        this.mismatchTypes = HashBasedTable.create();
        this.ignoredMismatchTypes = HashBasedTable.create();
    }

    /** --- Functional Getters / Setters --- */

    /** Record a type/status count */
    public void addMismatchTypeCount(SpotCheckMismatchType type, SpotCheckMismatchStatus status, long count) {
        mismatchTypes.put(type, status, count);
    }

    /** Record an ignored type/status count */
    public void addIgnoredMismatchTypeCount(SpotCheckMismatchType type, SpotCheckMismatchStatus status, long count) {
        ignoredMismatchTypes.put(type, status, count);
    }

    public Map<SpotCheckMismatchStatus, Long> getMismatchStatuses() {
        return mismatchTypes.cellSet().stream()
                .collect(Collectors.toMap(Table.Cell::getColumnKey, Table.Cell::getValue, Long::sum));
    }

    /** --- Getters --- */

    public Table<SpotCheckMismatchType, SpotCheckMismatchStatus, Long> getMismatchTypes() {
        return mismatchTypes;
    }

    public Table<SpotCheckMismatchType, SpotCheckMismatchStatus, Long> getIgnoredMismatchTypes() {
        return ignoredMismatchTypes;
    }
}
