package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.MismatchTypeSummary;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MismatchTypeSummaryView  implements ViewObject {

    protected MapView<SpotCheckMismatchType, Integer> typeCount;

    public MismatchTypeSummaryView(MismatchTypeSummary summary) {
        if (summary.getSummary().get(SpotCheckMismatchType.All) == 0){ // if there is no mismatch, sort by alphabet
            this.typeCount = MapView.ofIntMap(sortByKeys(summary.getSummary()));
        }
        else // else, sort by number of mismatch
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
    private <K extends Comparable<K>, V> Map<K, V> sortByKeys(final Map<K, V> map) {
        Comparator<K> keyComparator =  new Comparator<K>() {
            public int compare(K k1, K k2) {
                if (k1.equals(SpotCheckMismatchType.All))
                    return 1;
                else if (k2.equals(SpotCheckMismatchType.All))
                    return -1;
                else
                    return k2.toString().compareTo(k1.toString());
            }
        };
        Map<K, V> sortedByKeys = new TreeMap<K, V>(keyComparator);
        sortedByKeys.putAll(map);
        return sortedByKeys;
    }

    public MapView<SpotCheckMismatchType, Integer> getTypeCount(){
        return typeCount;
    }

    @Override
    public String getViewType() {
        return "mismatch-type-summary";
    }
}
