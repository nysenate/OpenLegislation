package gov.nysenate.openleg.service.calendar;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.CachingService;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CachedCalendarDataService implements CalendarDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedCalendarDataService.class);

    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    private void init() {
        setupCaches();
    }

    @Override
    public void setupCaches() {
        cacheManager.addCache("calendarData");
    }

    @Override
    public void evictCaches() {

    }

    @Override
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx {
        throw new CalendarNotFoundEx(calendarId);
    }

    @Override
    public void saveCalendar(Calendar calendar, SobiFragment sobiFragment) {

    }
}
