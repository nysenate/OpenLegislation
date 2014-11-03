package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.view.cache.CacheStatsView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/cache")
public class MemoryCacheCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(MemoryCacheCtrl.class);

    @Autowired private EventBus eventBus;

    @Autowired private CacheManager cacheManager;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /**
     * Cache Stats API
     *
     * Gets stats for all memory caches: (GET) /api/3/cache/
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getCacheStats() {
        return ListViewResponse.of(Arrays.asList(cacheManager.getCacheNames()).stream()
            .map(cn -> new CacheStatsView(cacheManager.getCache(cn).getStatistics()))
            .collect(Collectors.toList()), cacheManager.getCacheNames().length, LimitOffset.ALL);
    }

    /**
     * Cache Warming API
     *
     * This api can be used to clear out and pre-load a pre-determined subset of data into the cache
     * to boost performance of commonly used api calls.
     *
     * Warm memory caches: (PUT) /api/3/cache/{cacheType}
     * The cacheType can be either 'all' for all caches, or one of the values in the
     * {@link gov.nysenate.openleg.model.cache.ContentCache} enumeration.
     */
    @RequestMapping(value = "/{cacheType}", method = RequestMethod.PUT)
    public BaseResponse warmCache(@PathVariable String cacheType) {
        BaseResponse response;
        try {
            Set<ContentCache> targetCaches = getTargetCaches(cacheType);
            eventBus.post(new CacheWarmEvent(targetCaches));
            response = new SimpleResponse(true, "Cache warming requests completed.", "cache-warm");
        }
        catch (IllegalArgumentException ex) {
            response = new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
            response.setMessage("Invalid cacheType: " + cacheType);
        }
        return response;
    }

    /**
     * Cache Evict API
     *
     * Delete all entries in the specified cache(s): (DELETE) /api/3/cache/{cacheType}
     * @see #warmCache(String) for details about 'cacheType'
     */
    @RequestMapping(value = "/{cacheType}", method = RequestMethod.DELETE)
    public BaseResponse deleteCache(@PathVariable String cacheType) {
        BaseResponse response;
        try {
            Set<ContentCache> targetCaches = getTargetCaches(cacheType);
            eventBus.post(new CacheEvictEvent(targetCaches));
            response = new SimpleResponse(true, "Cache eviction request sent.", "cache-evict");
        }
        catch (IllegalArgumentException ex) {
            response = new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
            response.setMessage("Invalid cacheType: " + cacheType);
        }
        return response;
    }

    /** --- Internal --- */

    private Set<ContentCache> getTargetCaches(String cacheType) throws IllegalArgumentException {
        Set<ContentCache> targetCaches;
        if (cacheType.equalsIgnoreCase("all")) {
            targetCaches = Sets.newHashSet(ContentCache.values());
        }
        else {
            targetCaches = Sets.newHashSet(ContentCache.valueOf(cacheType.toUpperCase()));
        }
        return targetCaches;
    }
}