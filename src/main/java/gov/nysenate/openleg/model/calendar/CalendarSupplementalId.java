package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.base.Version;

import java.io.Serializable;

public class CalendarSupplementalId extends CalendarId implements Serializable{

    private static final long serialVersionUID = 9080620853238190830L;

    /** The identifier for this floor calendar, typically a single character */
    private Version version;

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

        CalendarSupplementalId that = (CalendarSupplementalId) o;

        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    /** --- Getters / Setters --- */

    public Version getVersion() {
        return version;
    }
}
