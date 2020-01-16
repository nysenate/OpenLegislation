package gov.nysenate.openleg.service.shiro;

import gov.nysenate.openleg.model.cache.ContentCache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.*;

/**
 * Created by Chenguang He on 10/19/2016.
 */
public class shiroCacheManager extends AbstractCacheManager {
    public static net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(new CacheConfiguration().name(ContentCache.SHIRO.name())
            .eternal(true)
            .maxBytesLocalHeap(2, MemoryUnit.MEGABYTES));
    @Override
    protected Cache createCache(String name) throws CacheException {

        return new Cache() {
            @Override
            public Object get(Object key) throws CacheException {
                if (cache.get(key) == null)
                    return null;
                else
                    return cache.get(key).getObjectValue();
            }

            @Override
            public Object put(Object key, Object value) throws CacheException {
                Element element = cache.get(key);
                cache.put(new Element(key,value));
                return element;
            }

            @Override
            public Object remove(Object key) throws CacheException {
                return cache.remove(key);
            }

            @Override
            public void clear() throws CacheException {
                cache.removeAll();
            }

            @Override
            public int size() {
                return cache.getSize();
            }

            @Override
            public Set keys() {
                return new HashSet(cache.getKeys());
            }

            @Override
            public Collection values() {
                if (cache.getKeys() == null)
                        return null;
                Map<Object,Element> map = cache.getAll(cache.getKeys());
                Collection<Element> collection = map.values();
                Collection res = new LinkedList();
                collection.forEach(o -> {
                    res.add(o.getObjectValue());
                });
                return res;
            }
        };
    }
}
