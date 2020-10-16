package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;

public interface CalendarEntryList {

    /**
     * @return CalendarEntryListId for a floor calendar, supplemental calendar, or active list
     */
    CalendarEntryListId getCalendarEntryListId();

}
