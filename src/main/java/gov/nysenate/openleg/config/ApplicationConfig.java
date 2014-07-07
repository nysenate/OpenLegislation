package gov.nysenate.openleg.config;

import gov.nysenate.openleg.Environment;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ApplicationConfig implements CachingConfigurer
{
    @Value("${env.directory}")
    private String envDirectory;

    @Value("${cache.max.heap.size:2G}")
    private String cacheMaxHeapSize;

    @Bean
    public Environment defaultEnvironment() {
        return new Environment(envDirectory);
    }

    /** --- Caching Configuration --- */

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setMaxBytesLocalHeap(cacheMaxHeapSize);
        config.addDefaultCache(cacheConfiguration);
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}
