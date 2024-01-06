package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.legislation.calendar.CalendarEntry;
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
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CalendarAlertActiveListParser extends BaseCalendarAlertParser {
    private static final Logger logger = LoggerFactory.getLogger(CalendarAlertActiveListParser.class);

    /**
     * Parses a Calendar Active List from an LBDC Alert email file.
     * @param calFile
     * @return
     * @throws IOException
     */
    protected CalendarActiveList parseActiveList(CalendarAlertFile calFile) throws IOException {
        CalendarActiveList activeList = new CalendarActiveList(calFile.getCalendarId(), calFile.getActiveListSeqNum(),
                "", parseCalendarDate(calFile.getFile()), calFile.getPublishedDateTime());
        parseActiveListEntries(calFile.getFile(), activeList);
        return activeList;
    }

    /**
     * Parses and adds Active List Entries to an Active List.
     * @param file
     * @param activeList
     * @throws IOException
     */
    private void parseActiveListEntries(File file, CalendarActiveList activeList) throws IOException {
        String html = FileUtils.readFileToString(file, Charset.defaultCharset());
        Document doc = Jsoup.parse(html);
        Elements entries = doc.select("body table tr");
        for (Element entry : deleteHeaderRow(entries)) {
            int calNo = Integer.parseInt(entry.select("td").get(0).text());
            String billIdString = entry.select("td").get(1).text().replaceAll("&nbsp;", "").trim();
            var billId = new BillId(billIdString, SessionYear.of(activeList.getYear()));
            activeList.addEntry(new CalendarEntry(calNo, billId));
        }
    }

    private static LocalDate parseCalendarDate(File file) {
        String html;
        try {
            html = FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            logger.info("Unable to parse active list cal date from file: " + file.getName(), e);
            return null;
        }
        Document doc = Jsoup.parse(html);
        Element title = doc.select("h3[align]").get(0);
        String calTitle = title.text();

        Pattern calDatePattern = Pattern.compile("Active List (?<date>\\w+ \\w+ \\d{1,2}, \\d{4}).*");
        Matcher matcher = calDatePattern.matcher(calTitle);
        if (!matcher.find()) {
            return null;
        }
        String dateString = matcher.group("date");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy");
        return LocalDate.from(dtf.parse(dateString));
    }
}
