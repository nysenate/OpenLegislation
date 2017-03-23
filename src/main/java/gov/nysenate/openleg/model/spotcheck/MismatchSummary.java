package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;


/**
 * Daily mismatch summary information for a specified datasource.
 */
public class MismatchSummary {

    private Map<SpotCheckMismatchStatus, SpotCheckMismatchStatusSummary> summary;

    public MismatchSummary() {
        summary = new HashMap<>();
    }

    public Map<SpotCheckMismatchStatus, SpotCheckMismatchStatusSummary> getSummary() {
        return summary;
    }

    public void addStatusSummary(SpotCheckMismatchStatusSummary statusSummary) {
        if (summary.containsKey(statusSummary.getStatus())) {
            summary.get(statusSummary.getStatus()).add(statusSummary);
        }
        else {
            summary.put(statusSummary.getStatus(), statusSummary);
        }
    }
}
