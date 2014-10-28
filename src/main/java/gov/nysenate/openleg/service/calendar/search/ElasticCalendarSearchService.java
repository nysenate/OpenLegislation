package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.calendar.CalendarSearchDao;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticCalendarSearchService implements CalendarSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchService.class);

    private static LimitOffset defaultLimitOffset = LimitOffset.HUNDRED;

    @Autowired
    CalendarSearchDao calendarSearchDao;

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchForCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        if (limitOffset == null) {
            limitOffset = defaultLimitOffset;
        }
        try {
            return calendarSearchDao.searchCalendars(query, sort, limitOffset);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarActiveListId> searchForActiveLists(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        if (limitOffset == null) {
            limitOffset = defaultLimitOffset;
        }
        try {
            return calendarSearchDao.searchActiveLists(query, sort, limitOffset);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarSupplementalId> searchForFloorCalendars(String query, String sort,
                                                                         LimitOffset limitOffset)
            throws SearchException {
        if (limitOffset == null) {
            limitOffset = defaultLimitOffset;
        }
        try {
            return calendarSearchDao.searchFloorCalendars(query, sort, limitOffset);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }
}
