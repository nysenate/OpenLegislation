package gov.nysenate.openleg.dao.calendar.reference.openleg;

import gov.nysenate.openleg.client.view.calendar.CalendarView;

import java.util.List;

public interface OpenlegCalendarDao {
    /**
     * Get a list of calendars from the reference openleg instance for a given year
     * @param year
     * @return List of CalendarView
     */
    List<CalendarView> getCalendarViews(int year);
}
