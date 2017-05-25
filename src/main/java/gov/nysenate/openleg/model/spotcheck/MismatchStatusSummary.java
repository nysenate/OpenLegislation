package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;


/**
 * Daily mismatch summary information for a specified datasource.
 */
public class MismatchStatusSummary {

    private Map<MismatchStatus, Integer> summary;


    public MismatchStatusSummary() {
        summary = new HashMap<>();
    }

    public Map<MismatchStatus, Integer> getSummary() {
        return summary;
    }

    public void putSummary(MismatchStatus mismatchStatus, Integer count) {
        summary.put(mismatchStatus, count);
    }

}
