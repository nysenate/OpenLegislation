package gov.nysenate.openleg.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.processor.base.IngestCache;
import gov.nysenate.openleg.util.AsciiArt;
import gov.nysenate.openleg.util.OpenlegThreadFactory;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.net.InetSocketAddress;
import java.util.Calendar;

@Configuration
@EnableCaching
public class ApplicationConfig implements CachingConfigurer, SchedulingConfigurer, AsyncConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    /** --- Eh Cache Spring Configuration --- */

    @Value("${cache.max.size}") private String cacheMaxHeapSize;

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager pooledCacheManger() {
        // Set the upper limit when computing heap size for objects. Once it reaches the limit
        // it stops computing further. Some objects can contain many references so we set the limit
        // fairly high.
        SizeOfPolicyConfiguration sizeOfConfig = new SizeOfPolicyConfiguration();
        sizeOfConfig.setMaxDepth(100000);
        sizeOfConfig.setMaxDepthExceededBehavior("continue");

        // Configure the default cache to be used as a template for actual caches.
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.addSizeOfPolicy(sizeOfConfig);

        // Configure the cache manager.
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setMaxBytesLocalHeap(cacheMaxHeapSize + "M");
        config.addDefaultCache(cacheConfiguration);
        config.setUpdateCheck(false);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(pooledCacheManger());
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(cacheManager());
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }

    /** --- Elastic Search Configuration --- */

    @Value("${elastic.search.cluster.name:elasticsearch}") private String elasticSearchCluster;
    @Value("${elastic.search.host:localhost}") private String elasticSearchHost;
    @Value("${elastic.search.port:9300}") private int elasticSearchPort;
    @Value("${elastic.search.connection_retries:30}") private int esAllowedRetries;

    @Bean(destroyMethod = "close")
    public Client elasticSearchNode() throws InterruptedException {
        Settings settings = Settings.settingsBuilder()
            .put("cluster.name", elasticSearchCluster).build();

        int retryCount = 0;
        ElasticsearchException cause;

        do {
            if (retryCount <= esAllowedRetries) {
                Thread.sleep(1000);
            }
            logger.info("Connecting to elastic search cluster {} ...", elasticSearchCluster);
            try {
                TransportClient tc = TransportClient.builder()
                        .settings(settings)
                        .build()
                        .addTransportAddress(
                                new InetSocketTransportAddress(new InetSocketAddress(elasticSearchHost, elasticSearchPort)));
                if (tc.connectedNodes().size() == 0) {
                    tc.close();
                    throw new ElasticsearchException("Failed to connect to elastic search node!");
                }
                logger.info("Successfully connected to elastic search cluster {}", elasticSearchCluster);
                return tc;
            } catch (ElasticsearchException ex) {
                logger.warn("Could not connect to elastic search cluster {}", elasticSearchCluster);
                logger.warn("{} retries remain.", esAllowedRetries - retryCount);
                cause = ex;
                retryCount++;
            }
        } while (retryCount <= esAllowedRetries);

        logger.error("Error while initializing elasticsearch client:\n" + ExceptionUtils.getStackTrace(cause));
        logger.error("Elastic search cluster {} at host: {}:{} needs to be running prior to deployment!",
                elasticSearchCluster, elasticSearchHost, elasticSearchPort);
        logger.error(AsciiArt.START_ELASTIC_SEARCH.getText());
        return null;
    }

    /** --- Guava Event Bus Configuration --- */

    @Bean
    public EventBus eventBus() {
        SubscriberExceptionHandler errorHandler = (exception, context) -> {
            logger.error("Event Bus Exception thrown during event handling within {}: {}, {}", context.getSubscriberMethod(),
                exception, ExceptionUtils.getStackTrace(exception));
        };
        return new EventBus(errorHandler);
    }

    @Bean
    public AsyncEventBus asyncEventBus() {
        SubscriberExceptionHandler errorHandler = (exception, context) -> {
            logger.error("Async Event Bus Exception thrown during event handling within {}: {}, {}",
                    context.getSubscriberMethod(), exception, ExceptionUtils.getStackTrace(exception));
        };
        return new AsyncEventBus(getAsyncExecutor(), errorHandler);
    }

    /* --- Threadpool/Async/Scheduling Configuration --- */

    @Bean(name = "taskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadFactory(new OpenlegThreadFactory("scheduler"));
        scheduler.setPoolSize(8);
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(getTaskScheduler());
    }

    @Override
    @Bean(name = "openlegAsync", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadFactory(new OpenlegThreadFactory("spring-async"));
        executor.setCorePoolSize(10);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /** --- Object Mapper --- */

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    /** --- Processing Instances --- */

    @Value("${sobi.batch.process.size:100}")
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
}