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
     * Map of content types to their mismatch count with this status.
     */
    private Map<SpotCheckContentType, Integer> contentTypeCounts;

    /**
     * Initialize with a single content type with the intention of adding other content types later.
     *
     * @param status
     * @param contentType
     * @param count
     */
    public SpotCheckMismatchStatusSummary(SpotCheckMismatchStatus status, SpotCheckContentType contentType, int count) {
        this.status = status;
        this.total = count;
        this.contentTypeCounts = new HashMap<>();
        this.contentTypeCounts.put(contentType, count);
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
        for (Map.Entry<SpotCheckContentType, Integer> entry : statusSummary.getContentTypeCounts().entrySet()) {
            if (!this.contentTypeCounts.containsKey(entry.getKey())) {
                this.contentTypeCounts.put(entry.getKey(), entry.getValue());
                this.total += entry.getValue();
            }
        }
    }

    public SpotCheckMismatchStatus getStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }

    public Map<SpotCheckContentType, Integer> getContentTypeCounts() {
        return contentTypeCounts;
    }
}
