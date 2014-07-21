package gov.nysenate.openleg.model.calendar;

/**
 * CalendarId is a simple wrapper used to uniquely identify a Calendar instance.
 */
public class CalendarId
{
    /** The calendar id which is scoped to a single year. */
    private int calNo;

    /** The year in which this calendar belongs to.
     *  Does not have to be the session year. */
    private int year;

    /** --- Constructors --- */

    public CalendarId(int calNo, int year) {
        this.calNo = calNo;
        this.year = year;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "CalendarId {" + "calNo=" + calNo + ", year=" + year + '}';
    }

    /** --- Basic Getters/Setters --- */

    public int getCalNo() {
        return calNo;
    }

    public int getYear() {
        return year;
    }
}
