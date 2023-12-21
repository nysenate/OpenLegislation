package gov.nysenate.openleg.legislation.calendar;

import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

import java.io.Serial;
import java.io.Serializable;

public class CalendarSupplementalId extends CalendarId implements Serializable {
    @Serial
    private static final long serialVersionUID = 9080620853238190830L;

    /** The identifier for this floor calendar, typically a single character */
    private final Version version;

    /** --- Constructors --- */

    public CalendarSupplementalId(int calNo, int year, Version version) {
        super(calNo, year);
        this.version = version;
    }

    public CalendarSupplementalId(CalendarId calendarId, Version version) {
        super(calendarId);
        this.version = version;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "#" + calNo + "-" + version + " (" + year + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarSupplementalId)) return false;
        if (!super.equals(o)) return false;
        return this.version == ((CalendarSupplementalId)o).version;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    /** --- Getters / Setters --- */

    public CalendarEntryListId toCalendarEntryListId() {
        return new CalendarEntryListId(new CalendarId(this.calNo, this.year),
                this.version == Version.ORIGINAL ?
                        CalendarType.FLOOR_CALENDAR :
                        CalendarType.SUPPLEMENTAL_CALENDAR,
                this.version, 0);
    }

    public Version getVersion() {
        return version;
    }
}
