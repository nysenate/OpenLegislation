package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActiveListHTMLParser {

    public static ActiveListSpotcheckReference getSpotcheckReference(File html) throws Exception {
        List<CalendarEntry> entries = new ArrayList();
        //Get sequence number from previous page
        //todo Get CalendarId from previous page, can use yearstring for year though
        String monthString = null, dayString = null, yearString = null;
        String calNo, billPrintNo;
        int sessionNumber = 0;
        int sequenceNo = 0;


        Document doc = Jsoup.parse(html, "UTF-8");

        //get the additions to the HTML at top of file
        Elements a =  doc.select("h1");

        String releasedDT = a.get(1).text();
        LocalDateTime releasedDateTime = LocalDateTime.parse(releasedDT, DateUtils.LRS_WEBSITE_DATETIME_FORMAT);

        sessionNumber = releasedDateTime.getYear();
        //System.out.println("session number ::::::: " +sessionNumber);
        sequenceNo = Integer.parseInt(a.get(2).text());
        //System.out.println("sequenceNo:::::::::::"+sequenceNo);

        Elements h3 = doc.getElementsByTag("h3");

        String listTitle = h3.first().text();

        Pattern datePattern = Pattern.compile("(Active List) (Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)" +
                " (January|February|March|April|May|June|July|August|September|October|November|December) (\\d{1,2}), (\\d{4})");


        Matcher dateMatch = datePattern.matcher(listTitle);

        if (dateMatch.find()){
            monthString = dateMatch.group(3);
            dayString = dateMatch.group(4);
            yearString = dateMatch.group(5);
            //System.out.println(monthString + " " + dayString + " " + yearString);
        }

        Element table = doc.getElementsByTag("table").get(0); //Gets table
        Elements rows = table.getElementsByTag("tr");
        rows.remove(0);     //Removes table legend

        for (Element row : rows) {
            //System.out.println(row.getElementsByTag("td").text());
            Elements rowElements = row.getElementsByTag("td");

            calNo = rowElements.get(0).text();          //gets the calendar number as a string from table for a bill
            billPrintNo = rowElements.get(1).text();         //gets the bill printNumber

            CalendarEntry listEntry = new CalendarEntry(Integer.parseInt(calNo),
                    new BillId(billPrintNo, SessionYear.of(sessionNumber)));// BillId billId);

            entries.add(listEntry);                         //add current entry to the list
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-dd-yyyy");
        LocalDate calDate = LocalDate.parse(monthString + "-" + dayString + "-" + yearString, formatter);
        LocalDateTime reportDate = LocalDateTime.now();

        CalendarId calendarId = new CalendarId(sessionNumber, Integer.parseInt(yearString));

        return new ActiveListSpotcheckReference(sequenceNo, calendarId,  calDate, releasedDateTime, reportDate, entries);
    }
}
