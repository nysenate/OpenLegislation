package gov.nysenate.openleg.auth.shiro;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.legislation.CacheEvictEvent;
import gov.nysenate.openleg.legislation.CacheEvictIdEvent;
import gov.nysenate.openleg.legislation.CacheWarmEvent;
import gov.nysenate.openleg.legislation.ContentCache;
import gov.nysenate.openleg.legislation.CachingService;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chenguang He on 10/19/2016.
 */
@Service
public class shiroCacheService implements CachingService<Object> {
    private static final Logger logger = LoggerFactory.getLogger(shiroCacheService.class);
    @Autowired private CacheManager cacheManager;
    @Autowired private EventBus eventBus;
    @Autowired private shiroCacheManager shiroCacheManager;

    @PostConstruct
    private void init() {
        setupCaches();
        eventBus.register(this);
    }

    @Override
    public void setupCaches() {
        cacheManager.addCache(shiroCacheManager.cache);
    }

    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(shiroCacheManager.cache);
    }

    @Override
    public void evictContent(Object object) {
        logger.debug("evicting {}", object);
        shiroCacheManager.cache.remove(object);
    }

    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.SHIRO)) {
            evictCaches();
        }
    }

    @Override
    @Subscribe
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Object> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.SHIRO)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    @Override
    public void warmCaches() {

    }

    @Override
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {

    }
}
