package gov.nysenate.openleg.api;

import org.ehcache.core.statistics.CacheStatistics;

public record CacheStatsView(String cacheName, long putCount, long removeCount, long evictedCount,
                             long expiredCount, long hitCount, long missCount, float hitRatio) implements ViewObject {

    public CacheStatsView(String cacheName, CacheStatistics stats) {
        this(cacheName, stats.getCachePuts(), stats.getCacheRemovals(), stats.getCacheEvictions(),
                stats.getCacheExpirations(), stats.getCacheHits(), stats.getCacheMisses(),
                stats.getCacheHitPercentage());
    }

    @Override
    public String getViewType() {
        return "cache-stats";
    }
}
