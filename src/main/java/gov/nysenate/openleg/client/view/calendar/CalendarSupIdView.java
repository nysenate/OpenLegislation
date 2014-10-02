package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;

public class CalendarSupIdView extends CalendarIdView {

    protected String version;

    public CalendarSupIdView(CalendarSupplementalId calendarSupplementalId) {
        super(calendarSupplementalId);
        this.version = calendarSupplementalId.getVersion().getValue();
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getViewType() {
        return "calendar-floor-id";
    }
}
