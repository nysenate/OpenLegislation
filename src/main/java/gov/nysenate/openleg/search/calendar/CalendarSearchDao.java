package gov.nysenate.openleg.search.calendar;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

public interface CalendarSearchDao {

    /**
     * Performs a calendar search based on the given query string.
     * Results are sorted and curtailed according to the given sort string and limit offset.
     * */
    SearchResults<CalendarId> searchCalendars(Query query,
                                              List<SortOptions> sort, LimitOffset limitOffset);

    /**
     * Updates or inserts a single calendar into the index
     */
    void updateCalendarIndex(Calendar calendar);

    /**
     * Performs a bulk update/insert into the index for a collection of calendars
     */
    void updateCalendarIndexBulk(Collection<Calendar> calendars);
}
