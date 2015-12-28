package gov.nysenate.openleg.service.calendar.data;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.data.CalendarDao;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.service.base.data.CachingService;
import gov.nysenate.openleg.service.calendar.event.CalendarUpdateEvent;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CachedCalendarDataService implements CalendarDataService, CachingService<CalendarId>
{
    private static final Logger logger = LoggerFactory.getLogger(CachedCalendarDataService.class);

    @Autowired private CacheManager cacheManager;
    @Autowired private CalendarDao calendarDao;
    @Autowired private EventBus eventBus;

    @Value("${calendar.cache.size}") private long calendarCacheSizeMb;

    private Cache calendarCache;

    @PostConstruct
    private void init() {
        setupCaches();
        eventBus.register(this);
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.CALENDAR.name());
    }

    /** --- CachingService implementation --- */

    /** {@inheritDoc} */
    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(calendarCache);
    }

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        calendarCache = new Cache(new CacheConfiguration().name(ContentCache.CALENDAR.name())
                .eternal(true)
                .maxBytesLocalHeap(calendarCacheSizeMb, MemoryUnit.MEGABYTES)
                .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(calendarCache);
    }

    /** {@inheritDoc} */
    @Override
    public void evictCaches() {
        logger.info("clearing calendar cache");
        calendarCache.removeAll();
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(CalendarId calendarId) {
        calendarCache.remove(calendarId);
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.CALENDAR)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<CalendarId> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.CALENDAR)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void warmCaches() {
        evictCaches();
        getCalendars(LocalDate.now().getYear(), SortOrder.ASC, LimitOffset.ALL);
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.CALENDAR)) {
            warmCaches();
        }
    }

    /** --- CalendarDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx {
        if (calendarId == null) {
            throw new IllegalArgumentException("CalendarId cannot be null.");
        }

        Element element = calendarCache.get(calendarId);
        if (element != null) {
            logger.debug("Calendar Cache HIT !! {}", calendarId);
            return (Calendar) element.getObjectValue();
        }
        try {
            Calendar calendar = calendarDao.getCalendar(calendarId);
            calendarCache.put(new Element(calendarId, calendar));
            return calendar;
        }
        catch (DataAccessException ex) {
            logger.debug("Error retrieving calendar " + calendarId + ":\n" + ex.getMessage());
            throw new CalendarNotFoundEx(calendarId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CalendarActiveList getActiveList(CalendarActiveListId activeListId) throws CalendarNotFoundEx {
        if (activeListId == null) {
            throw new IllegalArgumentException("active list id cannot be null");
        }
        CalendarActiveList activeList = getCalendar(activeListId).getActiveList(activeListId.getSequenceNo());
        if (activeList != null) {
            return activeList;
        }
        throw new CalendarNotFoundEx(activeListId);
    }

    /** {@inheritDoc} */
    @Override
    public CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId supplementalId) throws CalendarNotFoundEx {
        if (supplementalId == null) {
            throw new IllegalArgumentException("active list id cannot be null");
        }
        CalendarSupplemental calSup = getCalendar(supplementalId).getSupplemental(supplementalId.getVersion());
        if (calSup != null ) {
            return calSup;
        }
        throw new CalendarNotFoundEx(supplementalId);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Range<Integer>> getCalendarYearRange() {
        try {
            return Optional.of(calendarDao.getActiveYearRange());
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getCalendarCount() {
        return calendarDao.getCalendarCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getCalendarCount(int year) {
        try {
            return calendarDao.getCalendarCount(year);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving calendar id count for " + year + ":\n" + ex.getMessage());
            return 0;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getActiveListCount(int year) {
        try {
            return calendarDao.getActiveListCount(year);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving active list id count for " + year + ":\n" + ex.getMessage());
            return 0;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getSupplementalCount(int year) {
        try {
            return calendarDao.getCalendarSupplementalCount(year);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving floor calendar id count for " + year + ":\n" + ex.getMessage());
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Calendar> getCalendars(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getCalendarIds(year, sortOrder, limitOffset).stream()
                .map(this::getCalendar)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarActiveList> getActiveLists(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getActiveListIds(year, sortOrder, limitOffset).stream()
                .map(this::getActiveList)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarSupplemental> getCalendarSupplementals(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getCalendarSupplementalIds(year, sortOrder, limitOffset).stream()
                .map(this::getCalendarSupplemental)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public void saveCalendar(Calendar calendar, SobiFragment sobiFragment, boolean postUpdateEvent) {
        logger.debug("Persisting {}", calendar);
        calendarDao.updateCalendar(calendar, sobiFragment);
        calendarCache.put(new Element(calendar.getId(), calendar));
        if (postUpdateEvent) {
            eventBus.post(new CalendarUpdateEvent(calendar));
        }
    }
}