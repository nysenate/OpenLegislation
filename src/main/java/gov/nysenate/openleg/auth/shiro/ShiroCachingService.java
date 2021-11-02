package gov.nysenate.openleg.auth.shiro;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import org.ehcache.Cache;
import org.springframework.stereotype.Component;

import static gov.nysenate.openleg.legislation.CacheType.SHIRO;

/**
 * Created by Chenguang He on 10/19/2016.
 */
@Component
public class ShiroCachingService extends CachingService<Object, Object> {

    @Override
    protected CacheType cacheType() {
        return SHIRO;
    }

    @Override
    protected Class<Object> keyClass() {
        return Object.class;
    }

    @Override
    protected Class<Object> valueClass() {
        return Object.class;
    }

    @Override
    public void warmCaches() {}

    protected Cache<Object, Object> getEhcache() {
        return cache;
    }
}
