package gov.nysenate.openleg.search.calendar;

import co.elastic.clients.elasticsearch._types.mapping.FlattenedProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ElasticCalendarSearchDao extends ElasticBaseDao<CalendarId, CalendarView, Calendar> {
    /**
     * Calendar documents are large, and a year of them in one bulk request can exceed
     * Elasticsearch's request size limit.
     */
    private static final int CALENDARS_PER_BULK_REQUEST = 10;

    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public ElasticCalendarSearchDao(CalendarViewFactory calendarViewFactory) {
        this.calendarViewFactory = calendarViewFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchIndex indexType() {
        return SearchIndex.CALENDAR;
    }

    @Override
    public void updateIndex(Collection<Calendar> calendars) {
        Iterables.partition(calendars, CALENDARS_PER_BULK_REQUEST).forEach(super::updateIndex);
    }

    @Override
    protected String getId(Calendar data) {
        return data.getId().toString();
    }

    @Override
    protected CalendarView getDoc(Calendar data) {
        return calendarViewFactory.getCalendarView(data);
    }

    @Override
    protected CalendarId toId(String idStr) {
        String[] parts = idStr.split("[#() ]+");
        return new CalendarId(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
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

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of(
                "supplementalCalendars", FlattenedProperty.of(b -> b)._toProperty(),
                "activeLists", FlattenedProperty.of(b -> b)._toProperty()
        );
    }
}
