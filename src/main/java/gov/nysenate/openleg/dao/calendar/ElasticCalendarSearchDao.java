package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Repository
public class ElasticCalendarSearchDao extends ElasticBaseDao implements CalendarSearchDao {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchDao.class);

    /** --- Index Names --- */

    protected static final String calIndexName = "calendars";
    protected static final String floorCalIndexName = "floor_calendars";
    protected static final String activeListIndexName = "active_lists";

    @PostConstruct
    public void init() {
        if (!calIndexExists()) {
            logger.warn("ElasticSearch Calendar index doesn't exist. Creating it now.");
            createCalIndex();
        }
        if (!floorIndexExists()) {
            logger.warn("ElasticSearch Floor Calendar index doesn't exist. Creating it now.");
            createFloorIndex();
        }
        if (!activeListIndexExists()) {
            logger.warn("ElasticSearch Active List index doesn't exist. Creating it now.");
            createActiveListIndex();
        }
    }

    /** --- Implementations --- */

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchCalendars(String query, String sort, LimitOffset limitOffset) {
        SearchRequestBuilder searchBuilder = getSearchRequest(calIndexName, query, , sort, limitOffset);
        return getSearchResults(searchBuilder.execute().actionGet(), limitOffset, this::getCalendarId);
    }

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarSupplementalId> searchFloorCalendars(String query, String sort, LimitOffset limitOffset) {
        SearchRequestBuilder searchBuilder = getSearchRequest(floorCalIndexName, query, , sort, limitOffset);
        return getSearchResults(searchBuilder.execute().actionGet(), limitOffset, this::getCalSupId);
    }

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarActiveListId> searchActiveLists(String query, String sort, LimitOffset limitOffset) {
        SearchRequestBuilder searchBuilder = getSearchRequest(activeListIndexName, query, , sort, limitOffset);
        return getSearchResults(searchBuilder.execute().actionGet(), limitOffset, this::getActiveListId);
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
            searchClient.prepareDeleteByQuery(calIndexName, floorCalIndexName, activeListIndexName)
                    .setTypes(Integer.toString(calId.getYear()))
                    .setQuery(QueryBuilders.matchQuery("calendarNumber", Integer.toString(calId.getCalNo())))
                    .execute().actionGet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCalendarIndex() {
        deleteCalIndex();
        deleteActiveListIndex();
        deleteFloorIndex();
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
        CalendarView calendarView = new CalendarView(calendar);
        bulkRequest.add(getCalendarIndexRequest(calendarView));
        calendarView.getActiveLists().getItems().values().stream()
                .map(this::getActiveListIndexRequest)
                .forEach(bulkRequest::add);
        calendarView.getFloorCalendars().getItems().values().stream()
                .map(this::getFloorCalendarIndexRequest)
                .forEach(bulkRequest::add);
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

    /**
     * Generates an index update request from a calendar supplemental view
     *
     * @param calendarSupView
     */
    protected IndexRequestBuilder getFloorCalendarIndexRequest(CalendarSupView calendarSupView) {
        return searchClient.prepareIndex(floorCalIndexName,
                Integer.toString(calendarSupView.getYear()), generateSupSearchId(calendarSupView))
                .setSource(OutputUtils.toJson(calendarSupView));
    }

    /**
     * Generates an index update request from a calendar active list view
     *
     * @param activeListView
     */
    protected IndexRequestBuilder getActiveListIndexRequest(ActiveListView activeListView) {
        return searchClient.prepareIndex(activeListIndexName,
                Integer.toString(activeListView.getYear()), generateActiveListSearchId(activeListView))
                .setSource(OutputUtils.toJson(activeListView));
    }

    /** --- Id Mappers --- */

    /**
     * Generates an id string for a given calendar supplemental id view
     *
     * @param supIdView
     * @return
     */
    protected String generateSupSearchId(CalendarSupIdView supIdView) {
        return supIdView.getCalendarNumber() + "-" +
                (!supIdView.getVersion().isEmpty() ? supIdView.getVersion() : "default");
    }

    /**
     * Generates an id string for a given active list id view
     *
     * @param activeListIdView
     * @return
     */
    protected String generateActiveListSearchId(CalendarActiveListIdView activeListIdView) {
        return activeListIdView.getCalendarNumber() + "-" + activeListIdView.getSequenceNumber();
    }

    /**
     * Retrieves a CalendarId from a search hit
     *
     * @param hit
     * @return
     */
    protected CalendarId getCalendarId(SearchHit hit) {
        return new CalendarId(Integer.parseInt(hit.id()), Integer.parseInt(hit.type()));
    }

    /**
     * Retrieves a CalendarSupplementalId from a search hit
     *
     * @param hit
     * @return
     */
    protected CalendarSupplementalId getCalSupId(SearchHit hit) {
        String[] idParts = hit.id().split("-");
        return new CalendarSupplementalId(Integer.parseInt(idParts[0]), Integer.parseInt(hit.type()), Version.of(idParts[1]));
    }

    /**
     * Retrieves a CalendarActiveListId from a search hit
     *
     * @param hit
     * @return
     */
    protected CalendarActiveListId getActiveListId(SearchHit hit) {
        String[] idParts = hit.id().split("-");
        return new CalendarActiveListId(Integer.parseInt(idParts[0]), Integer.parseInt(hit.type()), Integer.parseInt(idParts[1]));
    }

    /** --- Index creation/deletion --- */

    protected boolean calIndexExists() {
        return indicesExist(calIndexName);
    }

    protected boolean floorIndexExists() {
        return indicesExist(floorCalIndexName);
    }

    protected boolean activeListIndexExists() {
        return indicesExist(activeListIndexName);
    }

    protected void createCalIndex() {
        createIndex(calIndexName);
    }

    protected void createFloorIndex() {
        createIndex(floorCalIndexName);
    }

    protected void createActiveListIndex() {
        createIndex(activeListIndexName);
    }

    protected void deleteCalIndex() {
        deleteIndex(calIndexName);
    }

    protected void deleteFloorIndex() {
        deleteIndex(floorCalIndexName);
    }

    protected void deleteActiveListIndex() {
        deleteIndex(activeListIndexName);
    }
}
