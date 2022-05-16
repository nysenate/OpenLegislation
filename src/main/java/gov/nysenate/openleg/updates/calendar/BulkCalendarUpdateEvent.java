package gov.nysenate.openleg.updates.calendar;

import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public record BulkCalendarUpdateEvent(Collection<Calendar> calendars)
        implements ContentUpdateEvent {}
