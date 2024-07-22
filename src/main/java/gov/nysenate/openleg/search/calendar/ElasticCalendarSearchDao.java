package gov.nysenate.openleg.search.calendar;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarIdView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class ElasticCalendarSearchDao extends ElasticBaseDao<CalendarView> implements CalendarSearchDao {
    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public ElasticCalendarSearchDao(CalendarViewFactory calendarViewFactory) {
        this.calendarViewFactory = calendarViewFactory;
    }

    /* --- Index Names --- */

    protected static final String calIndexName = SearchIndex.CALENDAR.getName();

    /* --- Implementations --- */

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchCalendars(Query query, Query postFilter,
                                                     List<SortOptions> sort, LimitOffset limitOffset) {
        return search(calIndexName, query, postFilter, sort, limitOffset, CalendarIdView::toCalendarId);
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
        var bulkBuilder = new BulkOperation.Builder();
        calendars.stream().map(calendarViewFactory::getCalendarView)
                .map(calView -> getIndexOperationRequest(calIndexName, calView.toCalendarId().toString(), calView))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(calIndexName).operations(bulkBuilder.build())));
    }

    /**{@inheritDoc}*/
    @Override
    public void deleteCalendarFromIndex(CalendarId calId) {
        deleteEntry(calIndexName, calId.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.CALENDAR;
    }

    /**
     * Allocate additional shards for calendar index.
     *
     * @return Settings.Builder
     */
    @Override
    protected IndexSettings.Builder getIndexSettings() {
        return super.getIndexSettings().numberOfShards("2");
    }
}
