package gov.nysenate.openleg.legislation;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;

import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains various methods to manage caches and get statistics about them.
 */
public final class OpenLegCacheManager {
    private static final StatisticsService statisticsService = new DefaultStatisticsService();
    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .using(statisticsService).build(true);
    private static final EnumMap<CacheType, CachingService<?, ?>> cacheTypeMap =
            new EnumMap<>(CacheType.class);
    private static final EnumMap<CacheType, Integer> cacheCapacityMap = new EnumMap<>(CacheType.class);

    private OpenLegCacheManager() {}

    @SuppressWarnings("unchecked")
    static <K, V> Cache<K, V> createCache(CachingService<K, V> service, int size) {
        var classes = ((ParameterizedType) service.getClass().getGenericSuperclass())
                .getActualTypeArguments();
        var keyClass = (Class<K>) classes[0];
        var valueClass = (Class<V>) classes[1];
        var type = service.cacheType();

        cacheCapacityMap.put(type, size);
        var config = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(size))
                .withSizeOfMaxObjectGraph(100000).withExpiry(ExpiryPolicy.NO_EXPIRY)
                .withEvictionAdvisor(service.evictionAdvisor());
        cacheTypeMap.put(type, service);
        return cacheManager.createCache(type.name(), config);
    }

    static void removeCache(CacheType type) {
        cacheManager.removeCache(type.name());
    }

    public static CacheStatistics getCacheStats(CacheType type) {
        return statisticsService.getCacheStatistics(type.name());
    }

    public static int getCapacity(CacheType type) {
        return cacheCapacityMap.get(type);
    }

    public static synchronized void clearCaches(Set<CacheType> types, boolean warmCaches) {
        if (types.contains(CacheType.BILL) || types.contains(CacheType.BILL_INFO)) {
            // Ensures the Set is mutable.
            types = new HashSet<>(types);
            types.add(CacheType.BILL);
            types.add(CacheType.BILL_INFO);
        }
        for (var cachingService : types.stream().map(cacheTypeMap::get).toList()) {
            cachingService.clearCache(warmCaches);
        }
    }
}
