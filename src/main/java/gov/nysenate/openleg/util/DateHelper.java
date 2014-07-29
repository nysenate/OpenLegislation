package gov.nysenate.openleg.util;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper
{
    private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

    /** --- Date Formats --- */

    public final static DateFormat LRS_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static DateFormat LRS_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss'Z'");
    public static SimpleDateFormat SOBI_FILE_DATE_FORMAT = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    /** --- Static Methods --- */

    /**
     * Retrieve the year of the given date.
     *
     * @param date Date
     * @return Integer
     */
    public static Integer getYear(Date date) {
        return new LocalDate(date).getYear();
    }

    /**
     * A session year refers to that start of a 2 year legislative session period.
     * This method ensures that any given year will resolve to the correct session start year.
     *
     * @param year int
     * @return int
     */
    public static int resolveSession(int year) {
        return (year % 2 == 0) ? year - 1 : year;
    }

    /**
     * Extract the Date value from the LRS formatted date string.
     *
     * @param lbdcDate String
     * @return Date
     */
    public static Date getLrsDate(String lbdcDate) {
        try {
            return LRS_DATE_ONLY_FORMAT.parse(lbdcDate);
        }
        catch (ParseException e) {
            logger.error("Error parsing date: "+ lbdcDate, e);
            return null;
        }
    }

    /**
     * Extract the date and time from the LRS formatted date/time string
     *
     * @param lbdcDateTime String
     * @return Date
     */
    public static Date getLrsDateTime(String lbdcDateTime) {
        try {
            return LRS_DATETIME_FORMAT.parse(lbdcDateTime);
        }
        catch (ParseException e) {
            logger.error("Error parsing datetime: "+lbdcDateTime, e);
            return null;
        }
    }

    /**
     * Extract the date from the filename of a Sobi file.
     *
     * @param sobiFileName - Filename of the sobi file.
     * @return Date
     */
    public static Date getSobiFileDate(String sobiFileName) {
        try {
            return SOBI_FILE_DATE_FORMAT.parse(sobiFileName);
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
            return null;
        }
    }
}