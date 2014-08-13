package gov.nysenate.openleg.util;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CollectionUtils
{
    /**
     * Given the input list return a map that will have the ordinal of the element (basically
     * the index number within the list) as the key and the element itself as the value.
     *
     * For example given the {@code list}: ['A', 'B', 'C'] and a {@code startOrdinal} of 1,
     * the returned map will be: {1 ->'A', 2 -> 'B', 3 -> 'C'}
     *
     * @param list List<T> - The input list
     * @param startOrdinal int - The ordinal keys will start from this number
     * @return Map<Integer, T>
     */
    public static <T> Map<Integer, T> mapOrdinalsToElements(List<T> list, int startOrdinal) {
        Map<Integer, T> ordinalMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            ordinalMap.put((startOrdinal + i), list.get(i));
        }
        return ordinalMap;
    }

    /**
     * Given the input list return a map that will have the element as the key and the ordinal
     * of that element (basically it's index number within the list) as the value.
     *
     * For example given the {@code list}: ['A', 'B', 'C'] and a {@code startOrdinal} of 1,
     * the returned map will be: {'A' -> 1, 'B' -> 2, 'C' -> 3}
     *
     * @param list List<T> - The input list
     * @param startOrdinal int - The ordinal values will start from this number
     * @return Map<Integer, T>
     */
    public static <T> Map<T, Integer> mapElementsToOrdinals(List<T> list, int startOrdinal) {
        Map<T, Integer> ordinalMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            ordinalMap.put(list.get(i), (startOrdinal + i));
        }
        return ordinalMap;
    }

    /**
     * Return the differences between two lists where the elements are the keys and the values are
     * the ordinals for those elements. This is useful for distinguishing which elements are actually
     * unique against the elements that were just reordered.
     *
     * @see com.google.common.collect.MapDifference
     *
     * @param list1 List<T> - Original list
     * @param list2 List<T> - New list to compare against original
     * @param startOrdinal int - The ordinal values will start from this number
     * @return MapDifference<T, Integer>
     */
    public static <T> MapDifference<T, Integer> difference(List<T> list1, List<T> list2, int startOrdinal) {
        return Maps.difference(mapElementsToOrdinals(list1, startOrdinal),
                               mapElementsToOrdinals(list2, startOrdinal));
    }
}
