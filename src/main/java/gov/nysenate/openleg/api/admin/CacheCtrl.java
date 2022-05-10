package gov.nysenate.openleg.api.admin;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.CacheStatsView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/cache")
public class CacheCtrl extends BaseCtrl {
    @Autowired
    private EventBus eventBus;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /**
     * Cache Stats API
     * ---------------
     *
     * Gets stats for all memory caches: (GET) /api/3/admin/cache/
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getCacheStats() {
        List<CacheStatsView> views = new ArrayList<>();
        for (var type : CacheType.values())
            views.add(new CacheStatsView(type));
        return ListViewResponse.of(views);
    }

    /**
     * Cache Stats API
     * ---------------
     *
     * Get stats for a single cache: (GET) /api/3/admin/cache/{cacheType}
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "/{cacheType}", method = RequestMethod.GET)
    public BaseResponse getSingleCacheStats(@PathVariable String cacheType) {
        var type = CacheType.valueOf(cacheType.toUpperCase());
        return new ViewObjectResponse<>(new CacheStatsView(type));
    }

    /**
     * Cache Warming API
     * -----------------
     *
     * This api can be used to clear out and pre-load a pre-determined subset of data into the cache
     * to boost performance of commonly used api calls.
     *
     * Warm memory caches: (PUT) /api/3/admin/cache/{cacheType}
     * The cacheType can be either 'all' for all caches, or one of the values in the
     * {@link CacheType} enumeration.
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "/{cacheType}", method = RequestMethod.PUT)
    public BaseResponse warmCache(@PathVariable String cacheType) {
        return clearCache(cacheType, true);
    }

    /**
     * Cache Evict API
     * ---------------
     *
     * Delete all entries in the specified cache(s): (DELETE) /api/3/admin/cache/{cacheType}
     * @see #warmCache(String) for details about 'cacheType'
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "/{cacheType}", method = {RequestMethod.DELETE})
    public BaseResponse deleteCache(@PathVariable String cacheType) {
        return clearCache(cacheType, false);
    }

    private static SimpleResponse clearCache(String cacheType, boolean warmCache) {
        String eventType = warmCache ? "warm" : "evict";
        CachingService.clearCaches(getTargetCaches(cacheType), warmCache);
        return new SimpleResponse(true, "Cache " + eventType + " request sent for " + cacheType,
                "cache-" + eventType);
    }

    private static Set<CacheType> getTargetCaches(String cacheType) {
        if (cacheType.equalsIgnoreCase("all")) {
            return Set.of(CacheType.values());
        }
        return Set.of(getEnumParameter("cacheType", cacheType, CacheType.class));
    }
}
