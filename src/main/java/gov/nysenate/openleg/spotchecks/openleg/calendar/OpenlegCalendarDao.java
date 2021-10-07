package gov.nysenate.openleg.spotchecks.openleg.calendar;

import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;

import java.util.List;

public interface OpenlegCalendarDao {
    /**
     * Get a list of calendars from the reference openleg instance for a given year
     * @param year
     * @return List of CalendarView
     */
    List<CalendarView> getCalendarViews(int year);
}
