package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;

public class SpotCheckMismatchStatusSummary {

    /**
     * The status this summary represents.
     */
    private SpotCheckMismatchStatus status;

    /**
     * The total count of mismatches with this status.
     */
    private int total;

    /**
     * Initialize with a single content type with the intention of adding other content types later.
     *
     * @param status
     * @param count
     */
    public SpotCheckMismatchStatusSummary(SpotCheckMismatchStatus status, int count) {
        this.status = status;
        this.total = count;
    }

    /**
     * Add another Status summary to this one, aggregating the summary information.
     * Does not add information for content types which already exist in this summary.
     *
     * @param statusSummary
     */
    public void add(SpotCheckMismatchStatusSummary statusSummary) {
        if (this.status != statusSummary.getStatus()) {
            // cannot add summaries of different statuses.
            return;
        }
    }

    public SpotCheckMismatchStatus getStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }

}
