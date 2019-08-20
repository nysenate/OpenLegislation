package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaMeetingWeekId;

public enum SpotCheckContentType {

    AGENDA(CommitteeAgendaAddendumId.class),
    AGENDA_WEEK(AgendaMeetingWeekId.class),
    BILL(BaseBillId.class),
    BILL_AMENDMENT(BillId.class),
    CALENDAR(CalendarEntryListId.class)
    ;

    private Class contentKeyClass;

    SpotCheckContentType(Class contentKeyClass) {
        this.contentKeyClass = contentKeyClass;
    }


    public Class getContentKeyClass() {
        return contentKeyClass;
    }
}