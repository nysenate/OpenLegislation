package gov.nysenate.openleg.dao.calendar.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.client.view.calendar.CalendarViewFactory;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Repository
public class ElasticCalendarSearchDao extends ElasticBaseDao implements CalendarSearchDao {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchDao.class);

    @Autowired
    CalendarViewFactory calendarViewFactory;

    /* --- Index Names --- */

    protected static final String calIndexName = SearchIndex.CALENDAR.getIndexName();

    /* --- Implementations --- */

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchCalendars(QueryBuilder query, QueryBuilder postFilter,
                                                     List<SortBuilder> sort, LimitOffset limitOffset) {
        SearchRequest searchRequest = getSearchRequest(calIndexName, query, postFilter, sort, limitOffset);
        SearchResponse searchResponse = new SearchResponse();
        try {
            searchResponse = searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.error("Search Calendars request failed.", ex);
        }

        return getSearchResults(searchResponse, limitOffset, this::getCalendarId);
    }

    /**{@inheritDoc}*/
    @Override
    public void updateCalendarIndex(Calendar calendar) {
        if (calendar != null) {
            BulkRequest bulkRequest = new BulkRequest();
            addCalToBulkRequest(calendar, bulkRequest);
            try {
                searchClient.bulk(bulkRequest);
            }
            catch (IOException ex){
                logger.warn("Update Calendars request failed.", ex);
            }
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void updateCalendarIndexBulk(Collection<Calendar> calendars) {
        BulkRequest bulkRequest = new BulkRequest();
        calendars.forEach(cal -> addCalToBulkRequest(cal, bulkRequest));
        try {
            searchClient.bulk(bulkRequest);
        }
        catch (IOException ex){
            logger.warn("Bulk Calendars request failed.", ex);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void deleteCalendarFromIndex(CalendarId calId) {
        DeleteRequest deleteRequest = new DeleteRequest(
                calIndexName,
                defaultType,
                Integer.toString(calId.getCalNo()) + "-" + Integer.toString(calId.getYear())
        );

        try {
            searchClient.delete(deleteRequest);
        }
        catch (IOException ex){
            logger.warn("Delete Calendars request failed.", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(calIndexName);
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
        CalendarView calendarView = calendarViewFactory.getCalendarView(calendar);
        bulkRequest.add(getCalendarIndexRequest(calendarView));
    }

    /**
     * Generates an index update request from a calendar view
     *
     * @param calendarView
     * @return
     */
    protected IndexRequest getCalendarIndexRequest(CalendarView calendarView) {
        return new IndexRequest(calIndexName,
                defaultType, Integer.toString(calendarView.getCalendarNumber()) + "-" + Integer.toString(calendarView.getYear()))
                .source(OutputUtils.toJson(calendarView), XContentType.JSON);
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

}
