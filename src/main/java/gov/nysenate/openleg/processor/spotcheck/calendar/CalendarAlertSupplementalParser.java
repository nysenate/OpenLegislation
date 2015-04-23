package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSectionType;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CalendarAlertSupplementalParser extends BaseCalendarAlertParser {

    private static final Logger logger = LoggerFactory.getLogger(CalendarAlertSupplementalParser.class);

    protected CalendarSupplemental parseSupplemental(CalendarId calendarId, File file) throws IOException {
        CalendarSupplemental supplemental = new CalendarSupplemental(
                calendarId, parseVersion(file), parseCalendarDate(file), parseReleaseDateTime(file));
        parseSupplementalEntries(file, supplemental);
        return supplemental;
    }

    private void parseSupplementalEntries(File file, CalendarSupplemental supplemental) throws IOException {
        String html = FileUtils.readFileToString(file);
        Document doc = Jsoup.parse(html);
        Elements sectionTypes = doc.select("a[name]");
        Elements entryTables = doc.select("table");
        Map<Element, Elements> sectionTypeToEntryRows = mapSectionTypeToEntryRows(sectionTypes, entryTables);

        for (Element sectionType : sectionTypeToEntryRows.keySet()) {
            CalendarSectionType calendarSectionType = extractSectionType(sectionType);

            for (Element entryRow : sectionTypeToEntryRows.get(sectionType)) {
                supplemental.addEntry(createSupplementalEntry(supplemental, calendarSectionType, entryRow));
            }
        }
    }

    private CalendarSectionType extractSectionType(Element sectionType) {
        CalendarSectionType calendarSectionType;
        switch (sectionType.text()) {
            case "BILLS ON ORDER OF FIRST REPORT":
                calendarSectionType = CalendarSectionType.ORDER_OF_THE_FIRST_REPORT;
                break;
            case "BILLS ON ORDER OF SECOND REPORT":
                calendarSectionType = CalendarSectionType.ORDER_OF_THE_SECOND_REPORT;
                break;
            case "BILLS ON THIRD READING":
                calendarSectionType = CalendarSectionType.THIRD_READING;
                break;
            case "BILLS ON THIRD READING FROM SPECIAL REPORT":
                calendarSectionType = CalendarSectionType.THIRD_READING_FROM_SPECIAL_REPORT;
                break;
            default:
                calendarSectionType = null; // TODO handle other types.
        }
        return calendarSectionType;
    }

    private Map<Element, Elements> mapSectionTypeToEntryRows(Elements sectionTypes, Elements sectionEntryTables) {
        Map<Element, Elements> sectionTypeToEntryRows = new HashMap<>();
        for (int i = 0; i < sectionTypes.size(); i++) {
            Elements entryRows = sectionEntryTables.get(i).select("tr");
            Elements entryRowsWithoutHeaders = deleteHeaderRow(entryRows);
            sectionTypeToEntryRows.put(sectionTypes.get(i), entryRowsWithoutHeaders);
        }
        return sectionTypeToEntryRows;
    }

    private CalendarSupplementalEntry createSupplementalEntry(CalendarSupplemental supplemental, CalendarSectionType calendarSectionType, Element entryTable) {
        Elements columns = entryTable.select("td");
        int billCalNo = Integer.valueOf(columns.get(0).text());
        String printNo = "S" + columns.get(2).text();
        BillId billId = new BillId(printNo, supplemental.getSession());
        // TODO find examples of these
//        BillId subBillId;
//        boolean high; // high status is not available in alert emails.
        return new CalendarSupplementalEntry(billCalNo, calendarSectionType, billId, null, null);
    }

    private LocalDate parseCalendarDate(File file) {
        try {
            String html = FileUtils.readFileToString(file);
            Document doc = Jsoup.parse(html);
            Element title = doc.select("div.center").get(0);
            String calTitle = title.text();

            Pattern calDatePattern = Pattern.compile("STATE OF NEW YORK SENATE CALENDAR (?<date>\\w+, \\w+ \\d{1,2}, \\d{4}) .*");
            Matcher matcher = calDatePattern.matcher(calTitle);
            if (matcher.find()) {
                String dateString = capitalizeAllWords(matcher.group("date"));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
                return LocalDate.from(dtf.parse(dateString));
            }
        } catch (IOException e) {
            logger.info("Unable to parse supplemental cal date for file: " + file.getName(), e);
        }
        return null;
    }

    private String capitalizeAllWords(String dateString) {
        String newDateString = "";
        for (String word : dateString.split(" ")) {
            newDateString += StringUtils.capitalize(word.toLowerCase()) + " ";
        }
        return newDateString.trim();
    }
}
