package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarAlertParser {

    public Calendar parse(File file) {
        CalendarId id = parseCalendarId(file);
        CalendarSupplemental supplemental = parseSupplemental(id, file);
        try {
            parseSupplementalEntries(file, supplemental);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Calendar calendar = new Calendar(id);
        calendar.putSupplemental(supplemental);
        return calendar;
    }


    private CalendarId parseCalendarId(File file) {
        int year = Integer.valueOf(splitFileName(file)[1]);
        String calNo = parseCalNo(file);
        return new CalendarId(Integer.valueOf(calNo), year);
    }

    private CalendarSupplemental parseSupplemental(CalendarId calId, File file) {
        Version version = parseVersion(file);
        LocalDateTime dateTime = parseDateTime(file);
        LocalDate date = dateTime.toLocalDate();
        return new CalendarSupplemental(calId, version, date, dateTime);
    }

    private String parseCalNo(File file) {
        String calNoAndVersion = parseCalNoAndVersion(file);
        if (!isDefaultVersion(calNoAndVersion)) {
            calNoAndVersion = calNoAndVersion.substring(0, calNoAndVersion.length() - 1);
        }
        return calNoAndVersion;
    }

    private Version parseVersion(File file) {
        String calNoAndVersion = parseCalNoAndVersion(file);
        if (!isDefaultVersion(calNoAndVersion)) {
            return Version.of(calNoAndVersion.substring(calNoAndVersion.length() - 1));
        }
        return Version.DEFAULT;
    }

    private LocalDateTime parseDateTime(File file) {
        String dateTimeString = file.getName().split("-")[3];
        dateTimeString = dateTimeString.split("\\.")[0];
        return LocalDateTime.parse(dateTimeString, DateUtils.MINIMAL_ISO_DATE_TIME);
    }

    private boolean isDefaultVersion(String calNoAndVersion) {
        return !calNoAndVersion.matches("\\d+[A-Z]");
    }

    private String parseCalNoAndVersion(File file) {
        return splitFileName(file)[2];
    }

    private String[] splitFileName(File file) {
        return file.getName().split("-");
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
            default:
                calendarSectionType = null; // TODO throw exception?
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

    private Elements deleteHeaderRow(Elements entryRows) {
        return new Elements(entryRows.subList(1, entryRows.size()));
    }


}
