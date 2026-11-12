package gov.nysenate.openleg.legislation;

import gov.nysenate.openleg.common.util.TypeUtils;
import jakarta.annotation.PreDestroy;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains various methods to manage caches and get statistics about them.
 */
@Component
public class OpenLegCacheManager {
    private final StatisticsService statisticsService = new DefaultStatisticsService();
    private final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .using(statisticsService).build(true);
    private final EnumMap<CacheType, CachingService<?, ?>> cacheTypeMap =
            new EnumMap<>(CacheType.class);
    private final EnumMap<CacheType, Integer> cacheCapacityMap = new EnumMap<>(CacheType.class);

    @SuppressWarnings("unchecked")
    synchronized <K, V> Cache<K, V> createCache(CachingService<K, V> service, int size) {
        var classes = TypeUtils.getGenericTypes(service);
        var keyClass = (Class<K>) classes[0];
        var valueClass = (Class<V>) classes[1];
        var type = service.cacheType();

        var config = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(size))
                .withSizeOfMaxObjectGraph(100000).withExpiry(ExpiryPolicy.NO_EXPIRY)
                .withEvictionAdvisor(service.evictionAdvisor());
        Cache<K, V> cache = cacheManager.createCache(type.name(), config);
        cacheCapacityMap.put(type, size);
        cacheTypeMap.put(type, service);
        return cache;
    }

    synchronized void removeCache(CacheType type) {
        cacheManager.removeCache(type.name());
        cacheTypeMap.remove(type);
        cacheCapacityMap.remove(type);
    }

    public CacheStatistics getCacheStats(CacheType type) {
        return statisticsService.getCacheStatistics(type.name());
    }

    public int getCapacity(CacheType type) {
        return cacheCapacityMap.get(type);
    }

    public synchronized void clearCaches(Set<CacheType> types, boolean warmCaches) {
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

    @PreDestroy
    private void close() {
        cacheManager.close();
    }
}
