package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.OpenLegCacheManager;
import org.ehcache.core.statistics.CacheStatistics;

public record CacheStatsView(String cacheName, long size, int capacity, long putCount, long removeCount,
                             long evictedCount, long expiredCount, long hitCount,
                             long missCount, float hitRatio) implements ViewObject {
    public CacheStatsView(CacheType type) {
        this(type.name(), OpenLegCacheManager.getCapacity(type),
                OpenLegCacheManager.getCacheStats(type));
    }

    private CacheStatsView(String cacheName, int capacity, CacheStatistics stats) {
        this(cacheName, stats.getTierStatistics().get("OnHeap").getMappings(), capacity,
                stats.getCachePuts(), stats.getCacheRemovals(),
                stats.getCacheEvictions(), stats.getCacheExpirations(), stats.getCacheHits(),
                stats.getCacheMisses(), stats.getCacheHitPercentage());
    }

    @Override
    public String getViewType() {
        return "cache-stats";
    }
}
