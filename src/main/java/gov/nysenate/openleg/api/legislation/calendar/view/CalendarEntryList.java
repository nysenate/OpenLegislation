package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

public interface CalendarEntryList {

    /**
     * @return CalendarEntryListId for a floor calendar, supplemental calendar, or active list
     */
    CalendarEntryListId getCalendarEntryListId();

}
