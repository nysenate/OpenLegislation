package gov.nysenate.openleg.util;

import java.util.Calendar;

/*
 * Used to determine what the current session year is
 * and the exact start and end points of the session
 */
public class SessionYear {

    private static Integer year = null;
    private static Long sessionStart = null;
    private static Long sessionEnd = null;

    public static int getSessionYear(String year) {
        return getSessionYear(new Integer(year));
    }

    public static int getSessionYear(int year) {
        if(year % 2 != 1)
            year--;

        return year;
    }

    public static int getSessionYear() {
        if(year == null) {
            Calendar cal = Calendar.getInstance();
            year = getSessionYear(cal.get(Calendar.YEAR));
        }
        return year;
    }

    public static long getSessionStart(String year) {
        return getSessionStart(new Integer(year));
    }

    public static long getSessionStart(int year) {
        Calendar cal = Calendar.getInstance();
        //Jan 1st 12:00:00am of session year
        cal.set(year, 0, 1, 0, 0, 0);

        return cal.getTimeInMillis();
    }

    public static long getSessionStart() {
        if(sessionStart == null) {
            sessionStart = getSessionStart(getSessionYear());
        }
        return sessionStart;
    }

    public static long getSessionEnd(String year) {
        return getSessionEnd(new Integer(year));
    }

    public static long getSessionEnd(int year) {
        Calendar cal = Calendar.getInstance();
        //Dec 31st 11:59:59pm of sessionyear+1
        cal.set(year+1, 11, 31, 23, 59, 59);

        return cal.getTimeInMillis();
    }

    public static long getSessionEnd() {
        if(sessionEnd == null) {
            sessionEnd = getSessionEnd(getSessionYear());
        }
        return sessionEnd;
    }
}
