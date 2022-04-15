package gov.nysenate.openleg.auth.shiro;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;

@Service
public class ShiroCachingService extends CachingService<Object, Object> {
    @Override
    protected CacheType cacheType() {
        return CacheType.SHIRO;
    }

    protected Cache<Object, Object> getCache() {
        return cache;
    }
}
