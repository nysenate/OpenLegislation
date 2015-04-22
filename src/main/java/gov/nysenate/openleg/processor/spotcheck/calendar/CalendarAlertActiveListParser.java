package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CalendarAlertActiveListParser extends BaseCalendarAlertParser{

    /**
     * Parses a Calendar Active List from an LBDC Alert email file.
     * @param file
     * @return
     * @throws IOException
     */
    protected CalendarActiveList parseActiveList(Calendar calendar, File file) throws IOException {
        int sequenceNum = calendar.getActiveListMap().size();
        CalendarActiveList activeList = new CalendarActiveList(
                calendar.getId(), sequenceNum, "", parseCalendarDate(file), parseReleaseDateTime(file));

        parseActiveListEntries(file, activeList);
        return activeList;
    }

    /**
     * Parses and adds Active List Entries to an Active List.
     * @param file
     * @param activeList
     * @throws IOException
     */
    private void parseActiveListEntries(File file, CalendarActiveList activeList) throws IOException {
        String html = FileUtils.readFileToString(file);
        Document doc = Jsoup.parse(html);
        Elements entries = doc.select("body table tr");

        for (Element entry : deleteHeaderRow(entries)) {
            activeList.addEntry(extractEntry(activeList, entry));
        }
    }

    private CalendarEntry extractEntry(CalendarActiveList activeList, Element entry) {
        return new CalendarEntry(extractBillCalNo(entry), extractBillId(entry, activeList));
    }

    private BillId extractBillId(Element entry, CalendarActiveList activeList) {
        String billIdString = entry.select("td").get(1).text().replaceAll("&nbsp;", "").trim();
        return new BillId(billIdString, SessionYear.of(activeList.getYear()));
    }

    private int extractBillCalNo(Element entry) {
        return Integer.valueOf(entry.select("td").get(0).text());
    }
}
