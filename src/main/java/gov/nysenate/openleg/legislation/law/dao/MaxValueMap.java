package gov.nysenate.openleg.legislation.law.dao;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This special class, while similar to a Java Map, does not follow the contract of Map's put.
 * See this put function for details.
 */
public class MaxValueMap<K, V> {
    private final Map<K, V> internalMap = new HashMap<>();
    private final Comparator<V> comparator;

    /**
     * Initializes the function used for comparisons.
     * @param comparator returns a positive number if the 1st argument is greater than the 2nd.
     */
    public MaxValueMap(Comparator<V> comparator) {
        this.comparator = comparator;
    }

    /**
     * Replaces the old value for this key if and only if the new value is greater.
     */
    public void put(K key, V newValue) {
        V currValue = internalMap.get(key);
        if (currValue == null || comparator.compare(newValue, currValue) > 0) {
            internalMap.put(key, newValue);
        }
    }

    public V get(K key) {
        return internalMap.get(key);
    }

    public List<V> values() {
        return internalMap.values().stream().toList();
    }
}
