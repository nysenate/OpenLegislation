package gov.nysenate.openleg.processor.base;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A generic key-value pair cache to queue updates to the persistence layer so that they can be committed
 * to the file system more efficiently. The latest reference source data is associated with the cached item
 * since the persistence layer may want to know which pieces of source data the modifications came from.
 *
 * Key - The type for the key used to lookup values
 * Value - The type of values being stored
 * Source - The type that is used to encapsulate the source data
 */
public class IngestCache<Key, Value, Source>
{
    private final Map<Key, Pair<Value, Source>> cache = new LinkedHashMap<>();

    private final int maxCapacity;

    /** Keep a reference to the first source in case of processing exceptions. */
    private Source firstSource;

    public IngestCache(int capacity) {
        this.maxCapacity = capacity;
    }

    /**
     * Retrieve the object from cache using a unique id key. Returns null
     * on a cache miss.
     *
     * @param key String - Unique Id
     * @return V
     */
    public Pair<Value, Source> get(Key key) {
        return cache.get(key);
    }

    /**
     * Checks if the key is set in the cache.
     *
     * @param key String - Unique Id
     * @return boolean - true if key is found, false otherwise.
     */
    public boolean has(Key key) {
        return cache.containsKey(key);
    }

    /**
     * Returns the number of entries currently stored in the cache.
     *
     * @return int
     */
    public int getSize() {
        return this.cache.size();
    }

    /**
     * Returns the maximum number of entries this cache should try to hold.
     * Items will still be cached if this limit is exceeded, and must be managed externally.
     *
     * @return item
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Indicates if the number of entries stored in the cache is greater than the
     * set maximum capacity.
     *
     * @return boolean
     */
    public boolean exceedsCapacity() {
        return (getSize() > getMaxCapacity());
    }

    /**
     * Puts a new entry into the cache.
     *
     * @param key Key
     * @param obj Value
     * @param ref Source
     */
    public void set(Key key, Value obj, Source ref) {
        cache.put(key, Pair.of(obj, ref));
        if (firstSource == null && ref != null) {
            firstSource = ref;
        }
    }

    /**
     * Retrieve all entries in the cache as (Value, Source) pairs.
     *
     * @return Collection<Pair<Value, Source>>
     */
    public Collection<Pair<Value, Source>> getCurrentCache() {
        return cache.values();
    }

    /**
     * Clears out all the entries in the cache.
     */
    public void clearCache() {
        cache.clear();
        firstSource = null;
    }
}