package gov.nysenate.openleg.service.calendar.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

@Service
public class ProdCalendarDataService {

    private static final String BASE_URL = "http://open.nysenate.gov/legislation/2.0/calendar/";

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a {@link Calendar} from Open Legislation v1.9 data.
     * Used in QA process to verify v1.9's data integrity.
     * @param calDate The date of the calendar to create.
     * @return
     * @throws IOException
     */
    public Calendar getCalendar(LocalDate calDate) throws IOException {
        final CalendarId calendarId = getCalendarId(calDate);
        Calendar calendar = new Calendar(calendarId);
        calendar.setSupplementalMap(getSupplementals(calDate, calendarId));
        calendar.setActiveListMap(getActiveListMap(calDate, calendarId));
        return calendar;
    }

    private CalendarId getCalendarId(LocalDate calDate) throws IOException {
        JsonNode calendarNode = traverseToCalendarData(createSupplementalUrl(calDate));
        int calNo = Integer.valueOf(calendarNode.get("no").asText());
        int year = Integer.valueOf(calendarNode.get("sessionYear").asText());
        return new CalendarId(calNo, year);
    }

    private TreeMap<Integer, CalendarActiveList> getActiveListMap(LocalDate calDate, CalendarId calendarId) throws IOException {
        TreeMap<Integer, CalendarActiveList> activeListMap = new TreeMap<>();
        JsonNode calendarNode = traverseToCalendarData(createActiveListUrl(calDate));
        for (JsonNode activeListNode : calendarNode.get("supplementals").get(0).get("sequences")) {
            int sequenceNo = activeListNode.get("no").asInt();
            LocalDateTime releaseDate = getDateFromMilliSecs(activeListNode.get("releaseDateTime").asLong());
            CalendarActiveList activeList = new CalendarActiveList(calendarId, sequenceNo, "", calDate, releaseDate);

            for (JsonNode listEntryNode : activeListNode.get("calendarEntries")) {
                int billCalNo = listEntryNode.get("no").asInt();
                BillId billId = getBillId(listEntryNode);
                activeList.addEntry(new CalendarEntry(billCalNo, billId));
            }

            activeListMap.put(sequenceNo, activeList);
        }
        return activeListMap;
    }

    private TreeMap<Version, CalendarSupplemental> getSupplementals(LocalDate calDate, CalendarId calendarId) throws IOException {
        TreeMap<Version, CalendarSupplemental> supplementals = new TreeMap<>();
        JsonNode calendarNode = traverseToCalendarData(createSupplementalUrl(calDate));
        for (JsonNode supp : calendarNode.get("supplementals")) {
            Version version = Version.of(supp.get("supplementalId").asText());
            LocalDateTime releaseDateTime = getDateFromMilliSecs(supp.get("releaseDateTime").asLong());

            CalendarSupplemental supplemental = new CalendarSupplemental(calendarId, version, calDate, releaseDateTime);
            supplemental.setSectionEntries(getSupplementalEntryMap(supp));
            supplementals.put(version, supplemental);
        }
        return supplementals;
    }

    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getSupplementalEntryMap(JsonNode suppNode) {
        LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> supplementalEntryMap = LinkedListMultimap.create();
        for (JsonNode sectionNode : suppNode.get("sections")) {
            CalendarSectionType type = CalendarSectionType.valueOflrsRepresentation(sectionNode.get("name").asText());
            for (JsonNode entryNode : sectionNode.get("calendarEntries")) {
                int billCalNo = entryNode.get("no").asInt();
                BillId billId = getBillId(entryNode);

                CalendarSupplementalEntry entry = new CalendarSupplementalEntry();
                entry.setSectionType(type);
                entry.setBillId(billId);
                entry.setBillCalNo(billCalNo);
//                BillId subBillId // TODO: find example and implement.

                supplementalEntryMap.put(type, entry);
            }
        }
        return supplementalEntryMap;
    }

    private LocalDateTime getDateFromMilliSecs(long dateTimeMilisecs) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeMilisecs), ZoneId.systemDefault());
    }

    private URL createSupplementalUrl(LocalDate calDate) throws MalformedURLException {
        return new URL(BASE_URL + "floor-" + formatCalDate(calDate) + ".json");
    }

    private URL createActiveListUrl(LocalDate calDate) throws MalformedURLException {
        return new URL(BASE_URL + "active-" + formatCalDate(calDate) + ".json");
    }

    /**
     * @return the JsonNode where calendar data begins.
     */
    private JsonNode traverseToCalendarData(URL url) throws IOException {
        String json = IOUtils.toString(url);
        JsonNode root = mapper.readTree(json);
        return root.get("response").get("results").get(0).get("data").get("calendar");
    }

    private String formatCalDate(LocalDate calDate) {
        return calDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }

    private BillId getBillId(JsonNode entryNode) {
        JsonNode billNode = entryNode.get("bill");
        return new BillId(billNode.get("senateBillNo").asText().split("-")[0], SessionYear.of(billNode.get("year").asInt()));
    }
}
