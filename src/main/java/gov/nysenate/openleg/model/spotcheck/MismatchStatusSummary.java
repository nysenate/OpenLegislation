package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;


/**
 * Daily mismatch summary information for a specified datasource.
 */
public class MismatchStatusSummary {

    private Map<SpotCheckMismatchStatus, Integer> summary;


    public MismatchStatusSummary() {
        summary = new HashMap<>();
    }

    public Map<SpotCheckMismatchStatus, Integer> getSummary() {
        return summary;
    }

    public void addSpotCheckStatusSummary(SpotCheckMismatchStatus spotCheckMismatchStatus,Integer count) {
        summary.computeIfPresent(spotCheckMismatchStatus, (k,v) -> v+count);
    }

}
