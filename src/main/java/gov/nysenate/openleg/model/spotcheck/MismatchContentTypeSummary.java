package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;

public class MismatchContentTypeSummary {

    private Map<SpotCheckContentType, Integer> summary;

    public MismatchContentTypeSummary() {
        summary = new HashMap<>();
        for (SpotCheckContentType type : SpotCheckContentType.values()) {
            summary.put(type, 0);
        }
    }

    public Map<SpotCheckContentType, Integer> getSummary(){
        return summary;
    }

    public void addSpotCheckMismatchContentTypeCount(SpotCheckContentType spotCheckContentType, int count){
        summary.computeIfPresent(spotCheckContentType, (k,v) -> v+count);
    }
}
