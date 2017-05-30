package gov.nysenate.openleg.model.spotcheck;

import java.util.HashMap;
import java.util.Map;

public class MismatchTypeSummary {

    private Map<SpotCheckMismatchType, Integer> typeCount;

    public MismatchTypeSummary(){
        typeCount = new HashMap<>();
        for (SpotCheckMismatchType spotCheckMismatchType : SpotCheckMismatchType.values())
            typeCount.put(spotCheckMismatchType, 0);
    }

    public Map<SpotCheckMismatchType, Integer> getSummary(){
        return typeCount;
    }

    public void addSpotCheckMismatchTypeCount(SpotCheckMismatchType spotCheckMismatchType, int count){
        typeCount.computeIfPresent(spotCheckMismatchType, (k,v) -> v+count);
    }
}
