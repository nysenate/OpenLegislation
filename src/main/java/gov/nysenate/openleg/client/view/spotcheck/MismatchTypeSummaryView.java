package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.MismatchTypeSummary;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MismatchTypeSummaryView  implements ViewObject {

    protected MapView<SpotCheckMismatchType, Integer> typeCount;

    public MismatchTypeSummaryView(MismatchTypeSummary summary) {
        this.typeCount = MapView.ofIntMap(sortByValues(summary.getSummary()));
    }

    private <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =  new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = map.get(k2).compareTo(map.get(k1));
                if (compare == 0) return 1;
                else return compare;
            }
        };
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    public MapView<SpotCheckMismatchType, Integer> getTypeCount(){
        return typeCount;
    }

    @Override
    public String getViewType() {
        return "mismatch-type-summary";
    }
}
