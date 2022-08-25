package gov.nysenate.openleg.legislation.calendar.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.updates.calendar.CalendarUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CachedCalendarDataService extends CachingService<CalendarId, Calendar> implements CalendarDataService {
    private static final Logger logger = LoggerFactory.getLogger(CachedCalendarDataService.class);
    private final CalendarDao calendarDao;

    @Autowired
    public CachedCalendarDataService(CalendarDao calendarDao) {
        this.calendarDao = calendarDao;
    }

    /** --- CachingService implementation --- */

    @Override
    protected CacheType cacheType() {
        return CacheType.CALENDAR;
    }

    @Override
    public Map<CalendarId, Calendar> initialEntries() {
        return calendarDao.getCalendarIds(LocalDate.now().getYear(), SortOrder.ASC, LimitOffset.ALL)
                .stream().collect(Collectors.toMap(id -> id, id -> calendarDao.getCalendar(id)));
    }

    /** --- CalendarDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx {
        if (calendarId == null)
            throw new IllegalArgumentException("CalendarId cannot be null.");

        Calendar cal = cache.get(calendarId);
        if (cal != null) {
            logger.debug("Calendar Cache HIT !! {}", calendarId);
            return cal;
        }
        try {
            Calendar calendar = calendarDao.getCalendar(calendarId);
            cache.put(calendarId, calendar);
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
        if (activeListId == null)
            throw new IllegalArgumentException("active list id cannot be null");
        CalendarActiveList activeList = getCalendar(activeListId).getActiveList(activeListId.getSequenceNo());
        if (activeList != null)
            return activeList;
        throw new CalendarNotFoundEx(activeListId);
    }

    /** {@inheritDoc} */
    @Override
    public CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId supplementalId) throws CalendarNotFoundEx {
        if (supplementalId == null)
            throw new IllegalArgumentException("active list id cannot be null");
        CalendarSupplemental calSup = getCalendar(supplementalId).getSupplemental(supplementalId.getVersion());
        if (calSup != null)
            return calSup;
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
                .map(this::getCalendar).toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarActiveList> getActiveLists(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getActiveListIds(year, sortOrder, limitOffset).stream()
                .map(this::getActiveList).toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarSupplemental> getCalendarSupplementals(int year, SortOrder sortOrder, LimitOffset limitOffset) {
        return calendarDao.getCalendarSupplementalIds(year, sortOrder, limitOffset).stream()
                .map(this::getCalendarSupplemental).toList();
    }

    /** {@inheritDoc} */
    @Override
    public void saveCalendar(Calendar calendar, LegDataFragment legDataFragment, boolean postUpdateEvent) {
        logger.debug("Persisting {}", calendar);
        calendarDao.updateCalendar(calendar, legDataFragment);
        cache.put(calendar.getId(), calendar);
        if (postUpdateEvent) {
            eventBus.post(new CalendarUpdateEvent(calendar));
        }
    }
}
