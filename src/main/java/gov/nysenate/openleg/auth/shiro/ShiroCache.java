package gov.nysenate.openleg.auth.shiro;

import org.apache.shiro.cache.CacheException;
import org.ehcache.Cache;

import java.util.*;

/**
 * Special Cache implementation using an ehcache.
 */
public class ShiroCache implements org.apache.shiro.cache.Cache<Object, Object> {

    private final org.ehcache.Cache<Object, Object> ehcache;

    public ShiroCache(Cache<Object, Object> ehcache) {
        this.ehcache = ehcache;
    }

    @Override
    public Object get(Object o) throws CacheException {
        return ehcache.get(o);
    }

    @Override
    public Object put(Object key, Object value) throws CacheException {
        var oldValue = ehcache.get(key);
        ehcache.put(key, value);
        return oldValue;
    }

    @Override
    public Object remove(Object key) throws CacheException {
        var oldValue = ehcache.get(key);
        ehcache.remove(key);
        return oldValue;
    }

    @Override
    public void clear() throws CacheException {
        ehcache.clear();
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<Object> keys() {
        // Ehcaches have no methods to get keys or values directly, only an iterator.
        Iterator<Cache.Entry<Object, Object>> iter = ehcache.iterator();
        var keys = Collections.synchronizedSet(new HashSet<>());
        while (iter.hasNext())
            keys.add(iter.next().getKey());
        return keys;
    }

    @Override
    public Collection<Object> values() {
        Iterator<Cache.Entry<Object, Object>> iter = ehcache.iterator();
        var values = Collections.synchronizedCollection(new ArrayList<>());
        while (iter.hasNext())
            values.add(iter.next().getValue());
        return values;
    }
}
