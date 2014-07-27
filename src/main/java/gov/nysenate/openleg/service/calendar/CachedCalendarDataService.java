package gov.nysenate.openleg.service.calendar;

import gov.nysenate.openleg.dao.calendar.CalendarDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    @Override
    public void setupCaches() {
        cacheManager.addCache(calendarDataCache);
    }

    @Override
    @CacheEvict(value = calendarDataCache, allEntries = true)
    public void evictCaches() {}

    @Override
    @Cacheable(value = calendarDataCache, key = "#calendarId")
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx {
        if (calendarId == null) {
            throw new IllegalArgumentException("CalendarId cannot be null.");
        }
        try {
            return calendarDao.getCalendar(calendarId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new CalendarNotFoundEx(calendarId);
        }
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = calendarDataCache, key = "#calendar.id")
    public void saveCalendar(Calendar calendar, SobiFragment sobiFragment) {
        logger.debug("Persisting {}", calendar);
        calendarDao.updateCalendar(calendar, sobiFragment);
    }
}