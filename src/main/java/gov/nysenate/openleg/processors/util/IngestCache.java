package gov.nysenate.openleg.processors.util;

import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * A generic key-value pair cache to map objects that are being generated during the ingest
 * processing step to the SOBIFragments that serve as the data source. The purpose of this
 * cache is to queue updates to the persistence layer so that they can be committed to the
 * file system at a later time in the processing stage.
 * @param <T>
 */
public class IngestCache<T>
{
    /** A cache of unique id strings to DataFragmentPair mappings */
    private Map<String, T> cache = new HashMap<>();

    /** Maintain an ordered list of DataFragmentPairs so that we can apply the changes to the
     *  persistence layer in the order that they occurred. */
    private List<Pair<SOBIFragment, T>> orderedCache = new ArrayList<>();

    /**
     * Retrieve the object from cache using a unique id key. Returns null
     * on a cache miss.
     * @param key String - Unique Id
     * @return T
     */
    public T get(String key) {
        return cache.get(key);
    }

    /**
     * Checks if the key is set in the cache.
     * @param key String - Unique Id
     * @return boolean - true if key is found, false otherwise.
     */
    public boolean has(String key) {
        return cache.containsKey(key);
    }

    /**
     *
     * @param key String - Unique Id
     * @param obj T
     */
    public void set(String key, T obj) {
        cache.put(key, obj);
    }

    public Collection<T> getCurrentCache() {
        return cache.values();
    }

    public void flushCache() {
        cache.clear();
    }
}
