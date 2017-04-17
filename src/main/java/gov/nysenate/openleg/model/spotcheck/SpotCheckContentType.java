package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;

public enum SpotCheckContentType {

    AGENDA(CommitteeAgendaAddendumId.class),
    BILL(BillId.class),
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