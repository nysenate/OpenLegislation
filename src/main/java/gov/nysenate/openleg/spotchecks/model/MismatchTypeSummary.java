package gov.nysenate.openleg.spotchecks.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MismatchTypeSummary {

    private Map<SpotCheckMismatchType, Integer> typeCount;

    public MismatchTypeSummary(SpotCheckContentType contentType){
        typeCount = new HashMap<>();
        Arrays.stream(SpotCheckMismatchType.values())
                .filter(mt -> mt.possibleForContentType(contentType))
                .forEach(mt -> typeCount.put(mt, 0));
    }

    public Map<SpotCheckMismatchType, Integer> getSummary(){
        return typeCount;
    }

    public void addSpotCheckMismatchTypeCount(SpotCheckMismatchType spotCheckMismatchType, int count){
        typeCount.computeIfPresent(spotCheckMismatchType, (k,v) -> v+count);
    }
}
