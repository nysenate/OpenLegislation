package gov.nysenate.openleg.service.calendar;

public class CalendarNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -5337097504936947862L;

    private int calendarNo;
    private int year;

    public CalendarNotFoundEx(String message, int calendarNo, int year) {
        super("Calendar No: " + calendarNo + " Year: " + year + " could not be retrieved.");
        this.calendarNo = calendarNo;
        this.year = year;
    }

    public int getCalendarNo() {
        return calendarNo;
    }

    public int getYear() {
        return year;
    }
}
