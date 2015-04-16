package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSectionType;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarAlertSupplementalParser extends BaseCalendarAlertParser {

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
//        boolean high;
        return new CalendarSupplementalEntry(billCalNo, calendarSectionType, billId, null, null);
    }


}
