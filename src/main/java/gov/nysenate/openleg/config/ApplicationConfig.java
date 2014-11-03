package gov.nysenate.openleg.config;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.processor.base.IngestCache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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

import javax.annotation.PostConstruct;
import java.util.Calendar;

@Configuration
@EnableCaching
public class ApplicationConfig implements CachingConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @PostConstruct
    public void init() {
        logger.info("{}", logo());
    }

    /** --- Eh Cache Configuration --- */

    @Value("${cache.max.heap.size}") private String cacheMaxHeapSize;

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
        config.setUpdateCheck(false);

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

    @Value("${elastic.search.cluster.name:elasticsearch}") private String elasticSearchCluster;
    @Value("${elastic.search.host:localhost}") private String elasticSearchHost;
    @Value("${elastic.search.port:9300}") private int elasticSearchPort;

    @Bean(destroyMethod = "close")
    public Client elasticSearchNode() {
        logger.info("Connecting to elastic search cluster {}", elasticSearchCluster);
        Settings settings = ImmutableSettings.settingsBuilder()
            .put("cluster.name", elasticSearchCluster).build();
        return new TransportClient(settings).addTransportAddress(
                new InetSocketTransportAddress(elasticSearchHost, elasticSearchPort));
    }

    /** --- Guava Event Bus Configuration --- */

    @Bean
    public EventBus eventBus() {
        return new EventBus("openleg");
    }

    /** --- Processing Instances --- */

    @Value("${sobi.batch.size:1000}")
    private int sobiBatchSize;

    @Bean(name = "billIngestCache")
    public IngestCache<BaseBillId, Bill, SobiFragment> billIngestCache() {
        return new IngestCache<>(sobiBatchSize);
    }

    @Bean(name = "agendaIngestCache")
    public IngestCache<AgendaId, Agenda, SobiFragment> agendaIngestCache() {
        return new IngestCache<>(100);
    }

    @Bean(name = "calendarIngestCache")
    public IngestCache<CalendarId, Calendar, SobiFragment> calendarIngestCache() {
        return new IngestCache<>(100);
    }

    /** --- Misc --- */

    private String logo() {
        return
            "\n=============================================================================\n" +
            "  .oooooo.                                          .oooo.         .oooo.   \n" +
            " d8P'  `Y8b                                       .dP\"\"Y88b       d8P'`Y8b  \n" +
            "888      888 oo.ooooo.   .ooooo.  ooo. .oo.             ]8P'     888    888 \n" +
            "888      888  888' `88b d88' `88b `888P\"Y88b          .d8P'      888    888 \n" +
            "888      888  888   888 888ooo888  888   888        .dP'         888    888 \n" +
            "`88b    d88'  888   888 888    .o  888   888      .oP     .o .o. `88b  d88' \n" +
            " `Y8bood8P'   888bod8P' `Y8bod8P' o888o o888o     8888888888 Y8P  `Y8bd8P'  \n" +
            "              888                                                           \n" +
            "             o888o                                                          \n" +
            "=============================================================================\n";
    }
}