package gov.nysenate.openleg.service.agenda.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.agenda.data.AgendaDao;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.service.agenda.event.AgendaUpdateEvent;
import gov.nysenate.openleg.service.base.data.CachingService;
import gov.nysenate.openleg.model.cache.ContentCache;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class CachedAgendaDataService implements AgendaDataService, CachingService<AgendaId>
{
    private static final Logger logger = LoggerFactory.getLogger(CachedAgendaDataService.class);

    @Autowired private CacheManager cacheManager;
    @Autowired private AgendaDao agendaDao;
    @Autowired private EventBus eventBus;

    @Value("${agenda.cache.size}") private long agendaCacheSizeMb;

    private EhCacheCache agendaCache;

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.AGENDA.name());
    }

    /** --- CachingService implementation --- */

    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(agendaCache.getNativeCache());
    }

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        Cache cache = new Cache(new CacheConfiguration().name(ContentCache.AGENDA.name())
            .eternal(true)
            .maxBytesLocalHeap(agendaCacheSizeMb, MemoryUnit.MEGABYTES)
            .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(cache);
        this.agendaCache = new EhCacheCache(cache);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.AGENDA)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<AgendaId> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.AGENDA)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    @Override
    public void evictContent(AgendaId agendaId) {
        agendaCache.evict(agendaId);
    }

    /**
     * Pre-load the agenda cache by first clearing its current contents and then requesting every agenda
     * in the past 4 years.
     */
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up agenda cache.");
        int year = LocalDate.now().getYear();
        for (int i = 3; i >= 0; i--) {
            logger.info("Fetching agendas for year {}", (year - i));
            getAgendaIds(year - i, SortOrder.ASC).forEach(a -> getAgenda(a));
        }
        logger.info("Done warming up agenda cache.");
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.AGENDA)) {
            warmCaches();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws AgendaNotFoundEx {
        if (agendaId == null) {
            throw new IllegalArgumentException("AgendaId cannot be null.");
        }
        try {
            Agenda agenda = (agendaCache.get(agendaId) != null) ? (Agenda) agendaCache.get(agendaId).get() : null;
            if (agenda == null) {
                logger.debug("Fetching agenda {}", agendaId);
                agenda = agendaDao.getAgenda(agendaId);
                agendaCache.put(agendaId, agenda);
            }
            return agenda;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new AgendaNotFoundEx(agendaId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(LocalDate weekOf) throws AgendaNotFoundEx {
        try {
            return agendaDao.getAgenda(weekOf);
        } catch (EmptyResultDataAccessException ex) {
            throw new AgendaNotFoundEx(weekOf);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<AgendaId> getAgendaIds(int year, SortOrder idOrder) {
        return agendaDao.getAgendaIds(year, idOrder);
    }

    /** {@inheritDoc} */
    @Override
    public void saveAgenda(Agenda agenda, SobiFragment sobiFragment, boolean postUpdateEvent) {
        if (agenda == null) {
            throw new IllegalArgumentException("Agenda cannot be null when saving.");
        }
        logger.debug("Persisting agenda {}", agenda.getId());
        agendaDao.updateAgenda(agenda, sobiFragment);
        agendaCache.put(agenda.getId(), agenda);
        if (postUpdateEvent) {
            eventBus.post(new AgendaUpdateEvent(agenda, LocalDateTime.now()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgenda(AgendaId agendaId) {
        agendaDao.deleteAgenda(agendaId);
        agendaCache.evict(agendaId);
    }
}