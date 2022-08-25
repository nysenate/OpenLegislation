package gov.nysenate.openleg.search.calendar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class ElasticCalendarSearchDao extends ElasticBaseDao implements CalendarSearchDao {
    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchDao.class);
    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public ElasticCalendarSearchDao(CalendarViewFactory calendarViewFactory) {
        this.calendarViewFactory = calendarViewFactory;
    }

    /* --- Index Names --- */

    protected static final String calIndexName = SearchIndex.CALENDAR.getIndexName();

    /* --- Implementations --- */

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchCalendars(QueryBuilder query, QueryBuilder postFilter,
                                                     List<SortBuilder<?>> sort, LimitOffset limitOffset) {
        return search(calIndexName, query, postFilter, sort, limitOffset, this::getCalendarId);
    }

    /**{@inheritDoc}*/
    @Override
    public void updateCalendarIndex(Calendar calendar) {
        if (calendar != null) {
            updateCalendarIndexBulk(ImmutableList.of(calendar));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void updateCalendarIndexBulk(Collection<Calendar> calendars) {
        BulkRequest bulkRequest = new BulkRequest();
        calendars.forEach(cal -> addCalToBulkRequest(cal, bulkRequest));
        safeBulkRequestExecute(bulkRequest);
    }

    /**{@inheritDoc}*/
    @Override
    public void deleteCalendarFromIndex(CalendarId calId) {
        deleteEntry(calIndexName, toElasticId(calId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(calIndexName);
    }

    /**
     * Allocate additional shards for calendar index.
     *
     * @return Settings.Builder
     */
    @Override
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = super.getIndexSettings();
        indexSettings.put("index.number_of_shards", 2);
        return indexSettings;
    }

    /* --- Internal Methods --- */

    /**
     * Adds a calendar along with all of its floor calendars and active lists to a bulk index request
     *
     * @param calendar
     * @param bulkRequest
     */
    protected void addCalToBulkRequest(Calendar calendar, BulkRequest bulkRequest) {
        logger.info("Preparing to index {}", calendar);
        bulkRequest.add(getCalendarIndexRequest(calendar));
    }

    /**
     * Generates an index update request from a calendar
     *
     * @param calendar
     * @return
     */
    protected IndexRequest getCalendarIndexRequest(Calendar calendar) {
        CalendarView calendarView = calendarViewFactory.getCalendarView(calendar);
        return getJsonIndexRequest(calIndexName, toElasticId(calendar.getId()), calendarView);
    }

    /* --- Id Mappers --- */

    /**
     * Retrieves a CalendarId from a search hit
     *
     * @param hit
     * @return
     */
    protected CalendarId getCalendarId(SearchHit hit) {
        String[] IDparts = hit.getId().split("-");
        return new CalendarId(Integer.parseInt(IDparts[0]), Integer.parseInt(IDparts[1]));
    }

    protected String toElasticId(CalendarId calId) {
        return calId.getCalNo() + "-" + calId.getYear();
    }

}
