package gov.nysenate.openleg.config;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class StartupHousekeeper {

    @Autowired private EventBus eventBus;

    /**
     * Code in here will run before the application is deployed but after spring has completed initialization.
     *
     * Warm all caches before the app is deployed.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshedEvent() {
    }
}
