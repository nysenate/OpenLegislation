package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;

public class CalendarSupIdView extends CalendarIdView {

    protected String version;

    public CalendarSupIdView(CalendarSupplementalId calendarSupplementalId) {
        super(calendarSupplementalId);
        Version calVersion = calendarSupplementalId.getVersion();
        if (calVersion != null) {
            this.version = (calVersion == Version.ORIGINAL ? "floor" : calVersion.toString());
        }
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getViewType() {
        if (this.version.equals("floor")) {
            return "calendar-floor-id";
        }
        return "calendar-supplemental-id";
    }
}
