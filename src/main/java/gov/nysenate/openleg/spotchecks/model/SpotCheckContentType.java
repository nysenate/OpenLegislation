package gov.nysenate.openleg.spotchecks.model;

import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.spotchecks.alert.agenda.AgendaMeetingWeekId;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;
import gov.nysenate.openleg.spotchecks.sensite.bill.LawSpotCheckId;

public enum SpotCheckContentType {

    AGENDA(CommitteeAgendaAddendumId.class),
    AGENDA_WEEK(AgendaMeetingWeekId.class),
    BILL(BaseBillId.class),
    BILL_AMENDMENT(BillId.class),
    CALENDAR(CalendarEntryListId.class),
    LAW(LawSpotCheckId.class),
    ;

    private final Class<?> contentKeyClass;

    SpotCheckContentType(Class<?> contentKeyClass) {
        this.contentKeyClass = contentKeyClass;
    }


    public Class<?> getContentKeyClass() {
        return contentKeyClass;
    }
}