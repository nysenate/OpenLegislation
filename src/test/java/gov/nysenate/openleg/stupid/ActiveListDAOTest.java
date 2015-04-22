package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.activelist.ActiveListReferenceDAO;
import gov.nysenate.openleg.dao.activelist.SqlActiveListReferenceDAO;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.ActiveListSpotcheckReference;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyle on 11/21/14.
 */
public class ActiveListDAOTest extends BaseTests {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SqlActiveListReferenceDAO.class);

    @Autowired
    ActiveListReferenceDAO activeReference;
    SqlActiveListReferenceDAO activeReferenceEntry;
    @Test
    public void test() throws Exception{

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2000-01-02 12:12:12", formatter);

        //ActiveListSpotcheckReference a = activeReference.getCalendarReference(new CalendarActiveListId(5, 2000, 0), dateTime);

        /*ActiveListSpotcheckReference a = activeReference.getMostRecentReference(new CalendarActiveListId(5, 2000, 0));


        System.out.println("\n\nSequence no:   " + a.getSequenceNo());
        System.out.println("CalendarId  YEAR:   " + a.getCalendarId().getYear());
        System.out.println("CaeldnarId CALNO:   " + a.getCalendarId().getCalNo());
        System.out.println("CalDate:   " + a.getCalDate());
        System.out.println("ReportDate:   " + a.getReferenceDate());
        System.out.println("ReleaseDateTime:   " + a.getReleaseDateTime());
        */

        //ArrayList<ActiveListSpotcheckReference> thing = new ArrayList<> (activeReference.getMostRecentEachYear(2000));

        //GGGGGGGGGGGGGGGGGGGGGGGGGGET ALL ENTRIES IN AN ACTIVE LIST

        /*ArrayList<ActiveListSpotcheckReference> thing = (ArrayList)activeReference.getMostRecentEachYear(2000);

        for (ActiveListSpotcheckReference a : thing){
            System.out.println("\nNew list ");
            System.out.println("\nSequence no:   " + a.getSequenceNo());
            System.out.println("CalendarId  YEAR:   " + a.getCalendarId().getYear());
            System.out.println("CaeldnarId CALNO:   " + a.getCalendarId().getCalNo());
            System.out.println("CalDate:   " + a.getCalDate());
            System.out.println("ReportDate:   " + a.getReferenceDate());
            System.out.println("ReleaseDateTime:   " + a.getReleaseDateTime());
        }*/

        //    public CalendarActiveListEntry(Integer calNo, BillId billId)

        List<CalendarEntry> entries = new ArrayList<CalendarEntry>();
        entries.add(new CalendarEntry(1, new BillId("bill1", 2010, "a")));
        entries.add(new CalendarEntry(2, new BillId("bill2", 2010, "b")));
        entries.add(new CalendarEntry(3, new BillId("bill3", 2010, "c")));

        DateTimeFormatter formatterCalDate = DateTimeFormatter.ofPattern("MMMM-dd-yyyy");
        //String text = ;
        LocalDate calDate = LocalDate.parse("July-24-2010", formatterCalDate);

        String releasedDT = "12/04/2010 10:50 AM";
        //LocalDateTime releasedDateTime = LocalDateTime.parse(releasedDT, DateUtils.LRS_WEBSITE_DATETIME_FORMAT);
        LocalDateTime reportDate = LocalDateTime.now();
        Thread.sleep(1000);                 //1000 milliseconds is one second.
        LocalDateTime releaseTimeNow = LocalDateTime.now();


        activeReference.addCalendarReference(new ActiveListSpotcheckReference(2, new CalendarId(2000,2000), calDate, releaseTimeNow, reportDate, entries));

        //logger.info(OutputUtils.toJson("<a>"));
    }
}







