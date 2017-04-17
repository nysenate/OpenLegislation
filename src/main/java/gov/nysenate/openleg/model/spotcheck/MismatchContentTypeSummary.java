package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by senateuser on 2017/4/13.
 */
public class MismatchContentTypeSummary {
    private Map<SpotCheckContentType, Integer> summary;

    public MismatchContentTypeSummary() {
        summary = new HashMap<>();
    }

    public Map<SpotCheckContentType, Integer> getSummary(){
        return summary;
    }

    public void addSpotCheckMismatchContentTypeCount(SpotCheckContentType spotCheckContentType, int count){
        summary.computeIfPresent(spotCheckContentType, (k,v) -> v+count);
    }
}
