package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;

public class CalendarSupIdView extends CalendarIdView {

    protected String version;

    public CalendarSupIdView(CalendarSupplementalId calendarSupplementalId) {
        super(calendarSupplementalId);
        if (calendarSupplementalId != null) {
            if (calendarSupplementalId.getVersion() != null) {
                if (calendarSupplementalId.getVersion().equals(Version.DEFAULT)) {
                    this.version = "floor";
                }
                else {
                    this.version = calendarSupplementalId.getVersion().getValue();
                }
            }
        }
    }

    //Added for Json deserialization
    public CalendarSupIdView() {}

    //Added for Json deserialization
    public void setVersion(String version) {
        this.version = version;
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

    public CalendarEntryListId toCalendarEntryListId() {
        return new CalendarEntryListId(this.toCalendarId(),this.version.equals("floor") ? CalendarType.FLOOR_CALENDAR : CalendarType.SUPPLEMENTAL_CALENDAR,
                this.version.equals("floor") ? Version.DEFAULT : Version.of(this.version),0);
    }
}