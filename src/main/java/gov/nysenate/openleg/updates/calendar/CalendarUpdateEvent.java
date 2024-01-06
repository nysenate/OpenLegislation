package gov.nysenate.openleg.updates.calendar;

import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record CalendarUpdateEvent(Calendar calendar) implements ContentUpdateEvent {}
