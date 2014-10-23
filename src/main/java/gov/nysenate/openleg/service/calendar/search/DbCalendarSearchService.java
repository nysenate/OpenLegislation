package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.CalendarSearchDao;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.service.base.InvalidParametersSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DbCalendarSearchService implements CalendarSearchService {

    private static final Logger logger = LoggerFactory.getLogger(DbCalendarSearchService.class);

    @Resource(name = "sqlCalendarSearchDao")
    private CalendarSearchDao calendarSearchDao;

    /** {@inheritDoc} */
//    @Override
    public int getCalenderSearchResultCount(CalendarSearchParameters searchParams) throws InvalidParametersSearchException {
        if (!searchParams.isValid()) {
            throw new InvalidParametersSearchException(searchParams);
        }
        return calendarSearchDao.getCalendarCountforQuery(searchParams);
    }

    /** {@inheritDoc} */
//    @Override
    public List<CalendarId> searchForCalendars(CalendarSearchParameters searchParams,
                                               SortOrder sortOrder, LimitOffset limitOffset)
                                                throws InvalidParametersSearchException {
        if (searchParams == null || !searchParams.isValid()) {
            throw new InvalidParametersSearchException(searchParams);
        }
        return calendarSearchDao.getCalendars(searchParams, sortOrder, limitOffset);
    }

    /** {@inheritDoc} */
//    @Override
    public List<CalendarActiveListId> searchForActiveLists(CalendarSearchParameters searchParams,
                                                           SortOrder sortOrder, LimitOffset limitOffset)
                                                            throws InvalidParametersSearchException {
        if (searchParams == null || !searchParams.isValid() || searchParams.getCalendarType() != CalendarType.ACTIVE_LIST) {
            throw new InvalidParametersSearchException(searchParams);
        }
        return calendarSearchDao.getActiveLists(searchParams, sortOrder, limitOffset);
    }

    /** {@inheritDoc} */
//    @Override
    public List<CalendarSupplementalId> searchForFloorCalendars(CalendarSearchParameters searchParams,
                                                                SortOrder sortOrder, LimitOffset limitOffset)
                                                                throws InvalidParametersSearchException {
        if (searchParams == null || !searchParams.isValid() || searchParams.getCalendarType() != CalendarType.FLOOR) {
            throw new InvalidParametersSearchException(searchParams);
        }
        return calendarSearchDao.getFloorCalendars(searchParams, sortOrder, limitOffset);
    }
}
