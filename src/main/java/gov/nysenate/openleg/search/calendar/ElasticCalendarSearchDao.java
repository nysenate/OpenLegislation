package gov.nysenate.openleg.search.calendar;

import co.elastic.clients.elasticsearch.indices.IndexSettings;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ElasticCalendarSearchDao extends ElasticBaseDao<CalendarId, CalendarView, Calendar> {
    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public ElasticCalendarSearchDao(CalendarViewFactory calendarViewFactory) {
        this.calendarViewFactory = calendarViewFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.CALENDAR;
    }

    @Override
    protected CalendarId getId(Calendar data) {
        return data.getId();
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
}
