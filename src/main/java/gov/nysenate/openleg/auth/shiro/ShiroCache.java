package gov.nysenate.openleg.auth.shiro;

import org.apache.shiro.cache.CacheException;
import org.ehcache.Cache;

import java.util.*;

/**
 * Special Cache implementation using an ehcache.
 */
public class ShiroCache implements org.apache.shiro.cache.Cache<Object, Object> {
    private final Cache<Object, Object> cache;

    public ShiroCache(Cache<Object, Object> cache) {
        this.cache = cache;
    }

    @Override
    public Object get(Object o) throws CacheException {
        return cache.get(o);
    }

    @Override
    public Object put(Object key, Object value) throws CacheException {
        var oldValue = cache.get(key);
        cache.put(key, value);
        return oldValue;
    }

    @Override
    public Object remove(Object key) throws CacheException {
        var oldValue = cache.get(key);
        cache.remove(key);
        return oldValue;
    }

    @Override
    public void clear() throws CacheException {
        cache.clear();
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<Object> keys() {
        // Ehcaches have no methods to get keys or values directly, only an iterator.
        Iterator<Cache.Entry<Object, Object>> iter = cache.iterator();
        var keys = Collections.synchronizedSet(new HashSet<>());
        while (iter.hasNext())
            keys.add(iter.next().getKey());
        return keys;
    }

    @Override
    public Collection<Object> values() {
        Iterator<Cache.Entry<Object, Object>> iter = cache.iterator();
        var values = Collections.synchronizedCollection(new ArrayList<>());
        while (iter.hasNext())
            values.add(iter.next().getValue());
        return values;
    }
}
