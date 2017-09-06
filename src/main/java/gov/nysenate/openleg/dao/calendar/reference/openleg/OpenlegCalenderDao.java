package gov.nysenate.openleg.dao.calendar.reference.openleg;

import gov.nysenate.openleg.client.view.calendar.CalendarView;

import java.util.List;

public interface OpenlegCalenderDao {
    /**
     * Given a session year and apiKey, return the list of BillView from openleg.
     * @param sessionYear
     * @return List of CalendarView
     */
    public List<CalendarView> getOpenlegCalenderView(String sessionYear, String apiKey);
}
