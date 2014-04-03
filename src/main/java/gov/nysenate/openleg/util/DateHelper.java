package gov.nysenate.openleg.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateHelper
{
    private static Logger logger = Logger.getLogger(DateHelper.class);
    public final static DateFormat LRS_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static DateFormat LRS_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss'Z'");
    public static SimpleDateFormat SOBI_FILE_DATE_FORMAT = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    public static Date getDate(String lbdcDate)
    {
        try {
            return LRS_DATE_ONLY_FORMAT.parse(lbdcDate);
        }
        catch (ParseException e) {
            logger.error("Error parsing date: "+lbdcDate, e);
            return null;
        }
    }

    public static Date getDateTime(String lbdcDateTime)
    {
        try {
            return LRS_DATETIME_FORMAT.parse(lbdcDateTime);
        }
        catch (ParseException e) {
            logger.error("Error parsing datetime: "+lbdcDateTime, e);
            return null;
        }
    }

    public static Date getFileDate(String sobiFileName)
    {
        try {
            return SOBI_FILE_DATE_FORMAT.parse(sobiFileName);
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
            return null;
        }
    }

    public static Integer getYear(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
