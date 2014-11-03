package gov.nysenate.openleg.service.calendar.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.data.CalendarDao;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.service.base.data.CachingService;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CachedCalendarDataService implements CalendarDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedCalendarDataService.class);

    private static final String calendarDataCache = "calendarData";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CalendarDao calendarDao;

    @PostConstruct
    private void init() {
        setupCaches();
    }

    /** --- CachingService implementation --- */

    @Override
    public Ehcache getCache() {
        return null;
    }

    @Override
    public void setupCaches() {
        cacheManager.addCache(calendarDataCache);
    }

    @Override
    @CacheEvict(value = calendarDataCache, allEntries = true)
    public void evictCaches() {}

    @Override
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {

    }

    @Override
    public void warmCaches() {

    }

    @Override
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {

    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #calendarId")
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx {
        if (calendarId == null) {
            throw new IllegalArgumentException("CalendarId cannot be null.");
        }
        try {
            return calendarDao.getCalendar(calendarId);
        }
        catch (DataAccessException ex) {
            logger.debug("Error retrieving calendar " + calendarId + ":\n" + ex.getMessage());
            throw new CalendarNotFoundEx(calendarId);
        }
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #activeListId")
    public CalendarActiveList getActiveList(CalendarActiveListId activeListId) throws CalendarNotFoundEx {
        if (activeListId == null) {
            throw new IllegalArgumentException("active list id cannot be null");
        }
        try {
            return calendarDao.getActiveList(activeListId);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving active list " + activeListId + ":\n" + ex.getMessage());
            throw new CalendarNotFoundEx(activeListId);
        }
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #supplementalId")
    public CalendarSupplemental getFloorCalendar(CalendarSupplementalId supplementalId) throws CalendarNotFoundEx {
        if (supplementalId == null) {
            throw new IllegalArgumentException("active list id cannot be null");
        }
        try {
            return calendarDao.getFloorCalendar(supplementalId);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving floor calendar " + supplementalId + ":\n" + ex.getMessage());
            throw new CalendarNotFoundEx(supplementalId);
        }
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #year")
    public int getCalendarCount(int year) {
        try {
            return calendarDao.getCalendarCount(year);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving calendar id count for " + year + ":\n" + ex.getMessage());
            return 0;
        }
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #year")
    public int getActiveListCount(int year) {
        try {
            return calendarDao.getActiveListCount(year);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving active list id count for " + year + ":\n" + ex.getMessage());
            return 0;
        }
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #year")
    public int getFloorCalendarCount(int year) {
        try {
            return calendarDao.getFloorCalendarCount(year);
        }
        catch (DataAccessException ex) {
            logger.warn("Error retrieving floor calendar id count for " + year + ":\n" + ex.getMessage());
            return 0;
        }
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #year + '-' + #sortOrder + '-' + #limitOffset")
    public List<Calendar> getCalendars(int year, SortOrder sortOrder, LimitOffset limitOffset) {
            return calendarDao.getCalendars(year, sortOrder, limitOffset);
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #year + '-' + #sortOrder + '-' + #limitOffset")
    public List<CalendarActiveList> getActiveLists(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getActiveLists(year, sortOrder, limitOffset);
    }

    @Override
    @Cacheable(value = calendarDataCache, key = "#root.methodName + '-' + #year + '-' + #sortOrder + '-' + #limitOffset")
    public List<CalendarSupplemental> getFloorCalendars(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getFloorCalendars(year, sortOrder, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = calendarDataCache, allEntries = true)
    public void saveCalendar(Calendar calendar, SobiFragment sobiFragment) {
        logger.debug("Persisting {}", calendar);
        calendarDao.updateCalendar(calendar, sobiFragment);
    }
}