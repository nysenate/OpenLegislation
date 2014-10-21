package gov.nysenate.openleg.config;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.base.Environment;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
public class ApplicationConfig implements CachingConfigurer
{
    @Value("${env.directory}")
    private String envDirectory;

    @Value("${cache.max.heap.size:100M}")
    private String cacheMaxHeapSize;

    @Value("${elastic.search.cluster.name:elasticsearch}")
    private String elasticSearchCluster;

    @Value("${elastic.search.host:localhost}")
    private String elasticSearchHost;

    @Value("${elastic.search.port:9300}")
    private int elasticSearchPort;

    @Bean
    public Environment defaultEnvironment() {
        return new Environment(envDirectory);
    }

    /** --- Eh Cache Configuration --- */

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager pooledCacheManger() {
        // Set the upper limit when computing heap size for objects. Once it reaches the limit
        // it stops computing further. Some objects can contain many references so we set the limit
        // fairly high.
        SizeOfPolicyConfiguration sizeOfConfig = new SizeOfPolicyConfiguration();
        sizeOfConfig.setMaxDepth(50000);
        sizeOfConfig.setMaxDepthExceededBehavior("abort");

        // Configure the default cache to be used as a template for actual caches.
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.addSizeOfPolicy(sizeOfConfig);

        // Configure the cache manager.
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setMaxBytesLocalHeap(cacheMaxHeapSize);
        config.addDefaultCache(cacheConfiguration);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(pooledCacheManger());
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    /** --- Elastic Search Configuration --- */

    @Bean(destroyMethod = "close")
    public Client elasticSearchNode() {
        try {
            Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", elasticSearchCluster).build();

            return new TransportClient(settings).addTransportAddress(
                new InetSocketTransportAddress(elasticSearchHost, elasticSearchPort));
        }
        catch (ElasticsearchException ex) {
            System.err.println("Failed to join Elastic Search cluster!\n" + ex.getMessage());
        }
        return NodeBuilder.nodeBuilder().build().client();
    }

    /** --- Guava Event Bus Configuration --- */

    @Bean
    public EventBus eventBus() {
        return new EventBus("openleg");
    }
}
