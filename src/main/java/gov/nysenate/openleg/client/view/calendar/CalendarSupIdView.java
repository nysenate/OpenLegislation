package gov.nysenate.openleg.client.view.calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;

public class CalendarSupIdView extends CalendarIdView {

    protected String version;

    public CalendarSupIdView(CalendarSupplementalId calendarSupplementalId) {
        super(calendarSupplementalId);
        if (calendarSupplementalId != null) {
            if (calendarSupplementalId.getVersion() != null) {
                if (calendarSupplementalId.getVersion().equals(Version.DEFAULT)) {
                    this.version = "floor";
                } else {
                    this.version = calendarSupplementalId.getVersion().getValue();
                }
            }
        }
    }

    //Added for Json deserialization
    protected CalendarSupIdView() {
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

    @JsonIgnore
    public CalendarEntryListId toCalendarEntryListId() {
        return this.toCalendarSupplementalId().toCalendarEntryListId();
    }

    @JsonIgnore
    public CalendarSupplementalId toCalendarSupplementalId() {
        return new CalendarSupplementalId(this.calendarNumber, this.year, this.version.equals("floor") ? Version.DEFAULT : Version.of(this.version));
    }
}