package gov.nysenate.openleg.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import gov.nysenate.openleg.common.util.OpenlegThreadFactory;
import gov.nysenate.openleg.common.util.OutputUtils;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.notifications.NotificationDispatcher;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.processors.IngestCache;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Enumeration;

import static gov.nysenate.openleg.notifications.model.NotificationType.EVENT_BUS_EXCEPTION;

@Configuration
public class ApplicationConfig implements SchedulingConfigurer, AsyncConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    /**
     * Used to prevent Tomcat having to forcibly unregister the JDBC driver.
     * Code taken from <a href="https://stackoverflow.com/a/23912257">here</a>
     */
    @PreDestroy
    private void destroyContext() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                // This driver was registered by the webapp's ClassLoader, so deregister it:
                try {
                    logger.info("De-registering JDBC driver {}", driver);
                    DriverManager.deregisterDriver(driver);
                }
                catch (SQLException ex) {
                    logger.error("Error de-registering JDBC driver {}", driver, ex);
                }
            } else
                logger.trace("JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
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
        var scheduler = new ThreadPoolTaskScheduler();
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
    @Nonnull
    @Bean(name = "openlegAsync", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadFactory(new OpenlegThreadFactory("spring-async"));
        executor.setCorePoolSize(8);
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
        return OutputUtils.failOnUnknownMapper;
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
     * Note that even though notifications are posted through the event bus,
     * all exceptions are caught within the notification event handling code, preventing an infinite loop.
     * @see NotificationDispatcher#handleNotificationEvent(Notification)
     *
     * @param exception Throwable
     * @param context SubscriberExceptionContext
     */
    private void handleEventBusException(Throwable exception, SubscriberExceptionContext context) {
        logger.error("Event Bus Exception thrown during event handling within {}",
                context.getSubscriberMethod(), exception);

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