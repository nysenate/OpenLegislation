package gov.nysenate.openleg.dao.calendar.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

    @Autowired
    CalendarViewFactory calendarViewFactory;

    /** --- Index Names --- */

    protected static final String calIndexName = "calendars";

    /** --- Implementations --- */

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchCalendars(QueryBuilder query, FilterBuilder postFilter,
                                                     List<SortBuilder> sort, LimitOffset limitOffset) {
        SearchRequestBuilder searchBuilder = getSearchRequest(calIndexName, query, postFilter, sort, limitOffset);
        SearchResponse response = searchBuilder.execute().actionGet();
        return getSearchResults(response, limitOffset, this::getCalendarId);
    }

    /**{@inheritDoc}*/
    @Override
    public void updateCalendarIndex(Calendar calendar) {
        if (calendar != null) {
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            addCalToBulkRequest(calendar, bulkRequest);
            bulkRequest.execute().actionGet();
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void updateCalendarIndexBulk(Collection<Calendar> calendars) {
        BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
        calendars.forEach(cal -> addCalToBulkRequest(cal, bulkRequest));
        bulkRequest.execute().actionGet();
    }

    /**{@inheritDoc}*/
    @Override
    public void deleteCalendarFromIndex(CalendarId calId) {
        if (calId != null) {
            searchClient.prepareDeleteByQuery(calIndexName)
                    .setTypes(Integer.toString(calId.getYear()))
                    .setQuery(QueryBuilders.matchQuery("calendarNumber", Integer.toString(calId.getCalNo())))
                    .execute().actionGet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(calIndexName);
    }

    /**
     * --- Internal Methods ---
     */

    /**
     * Adds a calendar along with all of its floor calendars and active lists to a bulk index request
     *
     * @param calendar
     * @param bulkRequest
     */
    protected void addCalToBulkRequest(Calendar calendar, BulkRequestBuilder bulkRequest) {
        logger.info("Preparing to index {}", calendar);
        CalendarView calendarView = calendarViewFactory.getCalendarView(calendar);
        bulkRequest.add(getCalendarIndexRequest(calendarView));
    }

    /**
     * Generates an index update request from a calendar view
     *
     * @param calendarView
     * @return
     */
    protected IndexRequestBuilder getCalendarIndexRequest(CalendarView calendarView) {
        return searchClient.prepareIndex(calIndexName,
                Integer.toString(calendarView.getYear()), Integer.toString(calendarView.getCalendarNumber()))
                .setSource(OutputUtils.toJson(calendarView));
    }

    /** --- Id Mappers --- */

    /**
     * Retrieves a CalendarId from a search hit
     *
     * @param hit
     * @return
     */
    protected CalendarId getCalendarId(SearchHit hit) {
        return new CalendarId(Integer.parseInt(hit.id()), Integer.parseInt(hit.type()));
    }

}
