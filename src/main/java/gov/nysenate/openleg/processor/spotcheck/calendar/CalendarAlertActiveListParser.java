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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CalendarAlertActiveListParser extends BaseCalendarAlertParser{

    private static final Logger logger = LoggerFactory.getLogger(CalendarAlertActiveListParser.class);

    /**
     * Parses a Calendar Active List from an LBDC Alert email file.
     * We increment the sequence num for every file so that version is saved.
     * The sequence num does NOT match the sequence number of the reference data since they don't include
     * it in the alert emails.
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

    private LocalDate parseCalendarDate(File file) {
        try {
            String html = FileUtils.readFileToString(file);
            Document doc = Jsoup.parse(html);
            Element title = doc.select("h3[align]").get(0);
            String calTitle = title.text();

            Pattern calDatePattern = Pattern.compile("Active List (?<date>\\w+ \\w+ \\d{1,2}, \\d{4}).*");
            Matcher matcher = calDatePattern.matcher(calTitle);
            if (matcher.find()) {
                String dateString = matcher.group("date");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy");
                return LocalDate.from(dtf.parse(dateString));
            }
        } catch (IOException e) {
            logger.info("Unable to parse active list cal date from file: " + file.getName(), e);
        }
        return null;
    }
}
