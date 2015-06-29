package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds summary information for a particular spotcheck report
 */
public class SpotCheckReportSummary {

    /** The Id of the described report */
    protected SpotCheckReportId reportId;

    /** The report notes for the described report */
    protected String notes;

    /** The number of observations that were conducted for this report */
    protected int observedCount;

    /** The number of occurrences for each mismatch status in the report */
    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;

    /** The number of occurrences for each mismatch type int the report, divided by mismatch status */
    protected Table<SpotCheckMismatchType, SpotCheckMismatchStatus, Long> mismatchTypes;

    /** --- Constructor --- */

    public SpotCheckReportSummary(SpotCheckReportId reportId, String notes, int observedCount) {
        this.reportId = reportId;
        this.notes = notes;
        this.observedCount = observedCount;
        this.mismatchStatuses = new HashMap<>();
        this.mismatchTypes = HashBasedTable.create();
    }

    /** --- Functional Getters / Setters */

    /** Record a type/status count */
    public void addMismatchTypeCount(SpotCheckMismatchType type, SpotCheckMismatchStatus status, long count) {
        mismatchTypes.put(type, status, count);
        if (!mismatchStatuses.containsKey(status)) {
            mismatchStatuses.put(status, 0L);
        }
        mismatchStatuses.put(status, mismatchStatuses.get(status) + count);
    }

    /** --- Getters / Setters --- */

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public String getNotes() {
        return notes;
    }

    public int getObservedCount() {
        return observedCount;
    }

    public Map<SpotCheckMismatchStatus, Long> getMismatchStatuses() {
        return mismatchStatuses;
    }

    public Table<SpotCheckMismatchType, SpotCheckMismatchStatus, Long> getMismatchTypes() {
        return mismatchTypes;
    }
}
