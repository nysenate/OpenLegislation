package gov.nysenate.openleg.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.processor.base.IngestCache;
import gov.nysenate.openleg.util.AsciiArt;
import gov.nysenate.openleg.util.OpenlegThreadFactory;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;

import static gov.nysenate.openleg.model.notification.NotificationType.EVENT_BUS_EXCEPTION;

@Configuration
@EnableCaching
public class ApplicationConfig implements CachingConfigurer, SchedulingConfigurer, AsyncConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    /** --- Eh Cache Spring Configuration --- */

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager pooledCacheManger() {
        // Set the upper limit when computing heap size for objects. Once it reaches the limit
        // a warning message will be logged. We should evaluate all heap based caches with
        // a large object dept for performance issues and convert it to an element size cache if necessary.
        SizeOfPolicyConfiguration sizeOfConfig = new SizeOfPolicyConfiguration();
        sizeOfConfig.setMaxDepth(5000);
        sizeOfConfig.setMaxDepthExceededBehavior("continue");

        // Configure the default cache to be used as a template for actual caches.
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.addSizeOfPolicy(sizeOfConfig);

        // Configure the cache manager.
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
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
    @Value("${elastic.search.port:9200}") private int elasticSearchPort;
    @Value("${elastic.search.connection_retries:30}") private int esAllowedRetries;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient elasticSearchNode() throws InterruptedException {

        int retryCount = 0;
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(elasticSearchHost, elasticSearchPort, "http")));

        while (true) {
            logger.info("Connecting to elastic search cluster {} ...", elasticSearchCluster);
            try {
                // Test the connection with a ping.
                if (!client.ping(RequestOptions.DEFAULT)) {
                    throw new ElasticsearchException("Could not ping elasticsearch cluster.");
                }
                logger.info("Successfully connected to elastic search cluster {}", elasticSearchCluster);
                return client;
            } catch (IOException | ElasticsearchException ex){
                logger.warn("Could not connect to elastic search cluster {}", elasticSearchCluster);
                logger.warn("{} retries remain.", esAllowedRetries - retryCount);
                if (retryCount >= esAllowedRetries) {
                    logger.error("Elastic search cluster {} at host: {}:{} needs to be running prior to deployment!",
                            elasticSearchCluster, elasticSearchHost, elasticSearchPort);
                    logger.error(AsciiArt.START_ELASTIC_SEARCH.getText());
                    throw new ElasticsearchException("Elasticsearch connection retries exceeded", ex);
                }
            }
            retryCount++;
            Thread.sleep(1000);
        }
    }

    /** --- Guava Event Bus Configuration --- */

    @Bean
    public EventBus eventBus() {
        return new EventBus(this::handleEventBusException);
    }

    @Bean
    public AsyncEventBus asyncEventBus() {
        return new AsyncEventBus(getAsyncExecutor(), this::handleEventBusException);
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
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(100);
        executor.setWaitForTasksToCompleteOnShutdown(false);
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

    @Value("${leg.data.batch.process.size:100}")
    private int legDataBatchSize;

    @Bean(name = "billIngestCache")
    public IngestCache<BaseBillId, Bill, LegDataFragment> billIngestCache() {
        return new IngestCache<>(legDataBatchSize);
    }

    @Bean(name = "agendaIngestCache")
    public IngestCache<AgendaId, Agenda, LegDataFragment> agendaIngestCache() {
        return new IngestCache<>(100);
    }

    @Bean(name = "calendarIngestCache")
    public IngestCache<CalendarId, Calendar, LegDataFragment> calendarIngestCache() {
        return new IngestCache<>(100);
    }

    /**
     * Handle event bus exceptions by posting a notification.
     *
     * Note that even though notifications are posted through the event bus,
     * all exceptions are caught within the notification event handling code, preventing an infinite loop.
     * @see gov.nysenate.openleg.service.notification.dispatch.NotificationDispatcher#handleNotificationEvent(Notification)
     *
     * @param exception Throwable
     * @param context SubscriberExceptionContext
     */
    private void handleEventBusException(Throwable exception, SubscriberExceptionContext context) {
        logger.error("Event Bus Exception thrown during event handling within " + context.getSubscriberMethod(), exception);

        LocalDateTime occurred = LocalDateTime.now();
        String summary = "Event Bus Exception within " + context.getSubscriberMethod() +
                " at " + occurred + " - " + ExceptionUtils.getStackFrames(exception)[0];
        String message = "\nThe following exception occurred during event handling within " +
                context.getSubscriberMethod() + " at " + occurred + ":\n" +
                ExceptionUtils.getStackTrace(exception);
        Notification notification = new Notification(EVENT_BUS_EXCEPTION, occurred, summary, message);

        eventBus().post(notification);
    }
}