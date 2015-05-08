package gov.nysenate.openleg.service.calendar.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
public class ProdCalendarDataService {

    private static final Logger logger = LoggerFactory.getLogger(ProdCalendarDataService.class);

    private static final String BASE_URL = "http://open.nysenate.gov/legislation/2.0/calendar/";

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retrieves a list of production calendars from start and end dates.
     * @see #getCalendar(LocalDate)
     */
    public List<Calendar> getCalendarsByRange(LocalDateTime start, LocalDateTime end) {
        LocalDate startDay = start.toLocalDate();
        LocalDate endDay = end.toLocalDate();
        List<Calendar> calendars = new ArrayList<>();
        for (LocalDate date = startDay; date.isBefore(endDay.plusDays(1)); date = date.plusDays(1)) {
            calendars.add(getCalendar(date));
        }
        return calendars;
    }

    /**
     * Creates a {@link Calendar} from Open Legislation v1.9 data.
     * Used in QA process to verify v1.9's data integrity.
     *
     * @param calDate The date of the calendar to create.
     *                if no calendar exists for the given date.
     * @return A Calendar or null if an error occurred during retrieval
     */
    public Calendar getCalendar(LocalDate calDate) {
        final CalendarId calendarId;
        Calendar calendar = null;
        try {
            if (!calendarExists(calDate)) {
                return null;
            }
            calendarId = getCalendarId(calDate);
            calendar = new Calendar(calendarId);
            calendar.setSupplementalMap(getSupplementals(calDate, calendarId));
            calendar.setActiveListMap(getActiveListMap(calDate, calendarId));

            // Calendar published date not exposed in api.
            calendar.setPublishedDateTime(calDate.atTime(LocalTime.now()));
            return calendar;
        } catch (IOException e) {
            logger.error("Error loading api call", e);
        }
        return calendar;
    }

    /**
     * If data for a calendar supplemental or active list exists, the calendar exists.
     * Otherwise there is no calendar for that date.
     */
    private boolean calendarExists(LocalDate calDate) throws IOException {
        JsonNode supResults = getFirstResult(createSupplementalUrl(calDate));
        JsonNode alResults = getFirstResult(createActiveListUrl(calDate));
        return supResults != null || alResults != null;
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
        if (calendarNode != null) {
            for (JsonNode activeListNode : calendarNode.get("supplementals").get(0).get("sequences")) {
                int sequenceNo = activeListNode.get("no").asInt();
                LocalDateTime releaseDate = getDateFromMilliSecs(activeListNode.get("releaseDateTime").asLong());
                CalendarActiveList activeList = new CalendarActiveList(calendarId, sequenceNo, null, calDate, releaseDate);

                for (JsonNode listEntryNode : activeListNode.get("calendarEntries")) {
                    int billCalNo = listEntryNode.get("no").asInt();
                    BillId billId = getBillId(listEntryNode);
                    activeList.addEntry(new CalendarEntry(billCalNo, billId));
                }

                activeListMap.put(sequenceNo, activeList);
            }
        }
        return activeListMap;
    }

    private TreeMap<Version, CalendarSupplemental> getSupplementals(LocalDate calDate, CalendarId calendarId) throws IOException {
        TreeMap<Version, CalendarSupplemental> supplementals = new TreeMap<>();
        JsonNode calendarNode = traverseToCalendarData(createSupplementalUrl(calDate));
        if (calendarNode != null) {
            for (JsonNode supp : calendarNode.get("supplementals")) {
                Version version = Version.of(supp.get("supplementalId").asText());
                LocalDateTime releaseDateTime = getDateFromMilliSecs(supp.get("releaseDateTime").asLong());

                CalendarSupplemental supplemental = new CalendarSupplemental(calendarId, version, calDate, releaseDateTime);
                supplemental.setSectionEntries(getSupplementalEntryMap(supp));
                supplementals.put(version, supplemental);
            }
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
        JsonNode root = getFirstResult(url);
        if (root != null) {
            return root.get("data").get("calendar");
        }
        return null;
    }

    /**
     * Returns the JsonNode containing the first result. Or a null JsonNode if not result exists.
     */
    private JsonNode getFirstResult(URL url) throws IOException {
        String json = IOUtils.toString(url);
        return mapper.readTree(json).get("response").get("results").get(0);
    }

    private String formatCalDate(LocalDate calDate) {
        return calDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }

    private BillId getBillId(JsonNode entryNode) {
        JsonNode billNode = entryNode.get("bill");
        return new BillId(billNode.get("senateBillNo").asText().split("-")[0], SessionYear.of(billNode.get("year").asInt()));
    }
}
