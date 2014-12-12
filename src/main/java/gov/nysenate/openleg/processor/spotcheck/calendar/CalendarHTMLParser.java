package gov.nysenate.openleg.processor.spotcheck.calendar;

import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSectionType;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
import gov.nysenate.openleg.model.spotcheck.calendar.FloorCalendarSpotcheckReference;
import gov.nysenate.openleg.processor.base.ParseError;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kyle on 10/6/14.
 */
public class CalendarHTMLParser {
    private static final Logger logger = LoggerFactory.getLogger(CalendarHTMLParser.class);

    public static FloorCalendarSpotcheckReference getSpotcheckReference(File html) throws Exception{
        String billCalNo = null;
        File input = new File("/home/kyle/Tests/FloorCalendarTest");
        String monthString = null, dayString = null, yearString = null, monthNum = null, verCharacter = null;
        LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> map = LinkedListMultimap.create();


        Document doc = Jsoup.parse(input, "UTF-8");
        Elements center = doc.getElementsByTag("center");
        String head = center.first().text();
        Pattern dateMatch = Pattern.compile("(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY), (\\w{1,10}) (\\d{1,2}), (\\d{4})");  // dayname, month, dayNo, year
        Pattern calendar = Pattern.compile("(NO.) (\\d+)(\\w?)");

        Matcher headMatch = dateMatch.matcher(head);

        if (headMatch.find()) {
            System.out.println("headMatch   " + headMatch.group());          //Date of Calendar

            monthString = headMatch.group(2);
            dayString = headMatch.group(3);
            yearString = headMatch.group(4);


            System.out.println("MonthString : "+ monthString);

            switch (monthString) {                              //could have just used a DateUtil
                case "JANUARY":  monthNum = "01";
                    break;
                case "FEBRUARY":  monthNum = "02";
                    break;
                case "MARCH":  monthNum = "03";
                    break;
                case "APRIL":  monthNum = "04";
                    break;
                case "MAY":  monthNum = "05";
                    break;
                case "JUNE":  monthNum = "06";
                    break;
                case "JULY":  monthNum = "07";
                    break;
                case "AUGUST":  monthNum = "08";
                    break;
                case "SEPTEMBER":  monthNum = "08";
                    break;
                case "OCTOBER": monthNum = "10";
                    break;
                case "NOVEMBER": monthNum = "11";
                    break;
                case "DECEMBER": monthNum = "12";
                    break;
                default: monthNum = "0";
                    break;
            }
            System.out.println("monthNum:  " + monthNum);
            System.out.println("dayString:  " + dayString);
            System.out.println("yearString:  " + yearString);
            headMatch.usePattern(calendar);                                 //switching pattern
            if (headMatch.find()){
                System.out.println("billCalNo:  " + headMatch.group(2));      //Calendar Number
                billCalNo = (headMatch.group(2));
                verCharacter = headMatch.group(3);                                 //Version Character
                System.out.println("VERSION NUMBER    " + verCharacter);
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Bill Reading Get
        for (int i = 0; i < center.size()-1; i++) {
            System.out.println("CENTER SIZE::::::::::::: "+ center.size());
            String reading = null;
            //parseException
            try {
                reading = center.get(i + 1).text();
                System.out.println("reading section:: " + i + "  : " + reading);
                if (reading == null){
                    throw new ParseError("Cannot find bill reading type");
                }
            }catch (ParseError ex){}

            String readingNo = null;
            /*
            Pattern billReading = Pattern.compile("(((BILLS ON )ORDER OF THE) (FIRST|SECOND|SPECIAL) (REPORT))|((BILLS ON )THIRD READING FROM SPECIAL REPORT)|((BILLS ON )THIRD READING)|((BILLS) (STARRED ON THIRD READING))");
            */
            //Match bill reading, just String

            //Get Elements in The Table
            Element elementsByTag = doc.getElementsByTag("table").get(i);      //WAS 0         //gets the table
            Elements rows = elementsByTag.getElementsByTag("tr");              //gets all the rows from the table
            rows.remove(0);
            for (Element row : rows) {
                boolean highBill = false;
                //System.out.println(row.getElementsByTag("td").text());                  //gets first element in td column
                Elements rowElements = row.getElementsByTag("td");

                String calendarNo = rowElements.get(0).text();

                String sponsor = row.getElementsByTag("td").get(1).text();
                Pattern subBillPattern = Pattern.compile("\\d+|\\[a-zA-Z]\\d+|\\[a-zA-Z]\\d+\\[a-zA-Z]|\\d+\\[a-zA-Z]D");     //finds all types of bill numbers
                Matcher subBillMatch = subBillPattern.matcher(sponsor);

                String subBillNo = null;
                if (subBillMatch.find()){
                    subBillNo = subBillMatch.group();
                    subBillNo = "S" + subBillNo;
                    //System.out.println("sub bill number::     " + subBillNo);
                }

                Pattern highBillPattern = Pattern.compile("[(]H[)]");
                Matcher highBillMatch = highBillPattern.matcher(sponsor);

                if (highBillMatch.find()){
                    highBill = true;
                }else{
                    highBill = false;
                }

                String printedNo = row.getElementsByTag("td").get(2).text();
                if (printedNo.charAt(0) != 'A') {
                    printedNo = "S" + printedNo;
                }
                String title = row.getElementsByTag("td").get(3).text();
                //System.out.println("printedNo            " + printedNo);
                //section type is the reading number (bills on first reading)    bill id is print number put s
                // before the number too unless it starts with a
                //substituted bill id is number in parenthases with name still add s
                // create different SalendarSupplementalEntry for if there is a subBillInfo
                CalendarSupplementalEntry calEntry;
                if ((subBillNo != null) & (reading != null)) {                                                           //uses current calendar year for both billId and subBillInfo
                    calEntry = new CalendarSupplementalEntry(Integer.parseInt(billCalNo),
                            CalendarSectionType.valueOflrsRepresentation(reading), new BillId(printedNo, Integer.parseInt(yearString)),
                            new BillId(subBillNo, Integer.parseInt(yearString)), highBill);
                } else {
                    calEntry = new CalendarSupplementalEntry(Integer.parseInt(billCalNo),
                            CalendarSectionType.valueOflrsRepresentation(reading), new BillId(printedNo, Integer.parseInt(yearString)),
                            null, highBill);
                    //CalendarSupplementalEntry calEntry = new CalendarSupplementalEntry(billCalNo, sectType, new BillId(printedNo, Integer.parseInt(yearString)),
                    //        null, false);

                }
                map.put(CalendarSectionType.valueOflrsRepresentation(reading), calEntry);
                //System.out.println(calendarNo + " " + sponsor + " " + printedNo + " " + title);
                //System.out.println(rowElements.isEmpty());
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        System.out.println("REPORT TIME :::::::::  "+ sdf.format(cal.getTime()));
*/



        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        //LocalDate reportDate = LocalDate.parse(sdf.format(cal.getTime()) ,formatter);
        LocalDateTime reportDate = LocalDateTime.now();
        LocalDate calDate = LocalDate.parse(monthNum + "-" + dayString + "-" + yearString, formatter);


        CalendarId calendarId = new CalendarId(Integer.parseInt(billCalNo), Integer.parseInt(yearString));
        System.out.println();
        //new FloorCalendarSpotcheckReference(reportDate, Version.of(verCharacter), calendarId, calDate, map);
        //FloorCalendarSpotcheckReference(referenceDate, version, calendarId, calDate,  sectionEntries)
        return new FloorCalendarSpotcheckReference(reportDate, Version.of(verCharacter), calendarId, calDate, map);
    }
}