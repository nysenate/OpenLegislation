package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.calendar.CalendarIdView;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.spotcheck.senatesite.base.JsonParser;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by PKS on 2/25/16.
 */

@Service
public class CalendarJsonParser extends JsonParser {
    @Autowired
    ObjectMapper objectMapper;

    public List<SenateSiteCalendar> parseCalendars(SenateSiteDump billDump) throws ParseError {
        return billDump.getDumpFragments().stream()
                .flatMap(fragment -> extractCalendarsFromFragment(fragment).stream())
                .collect(Collectors.toList());
    }

    private List<SenateSiteCalendar> extractCalendarsFromFragment(SenateSiteDumpFragment fragment) throws ParseError{
        try {
            JsonNode calendarMap = objectMapper.readTree(fragment.getFragmentFile())
                    .path("calendars");
            if (calendarMap.isMissingNode()) {
                throw new ParseError("Could not locate \"calendars\" node in senate site bill dump fragment file: " +
                        fragment.getFragmentFile().getAbsolutePath());
            }
            List<SenateSiteCalendar> calendars = new LinkedList<>();
            for (JsonNode calendarNode : calendarMap) {
                calendars.add(extractSenSiteCalendar(calendarNode, fragment));
            }
            return calendars;
        } catch (IOException | NoSuchElementException ex) {
            throw new ParseError("error while reading senate site bill dump fragment file: " +
                    fragment.getFragmentFile().getAbsolutePath(),
                    ex);
        }
    }

    private SenateSiteCalendar extractSenSiteCalendar(JsonNode calendarNode, SenateSiteDumpFragment fragment) throws IOException {
        SenateSiteCalendar calendar = new SenateSiteCalendar(DateUtils.endOfDateTimeRange(fragment.getDumpId().getRange()));

        calendar.setBillCalNumbers(getIntListValue(calendarNode, "field_ol_bill_cal_number"));
        calendar.setCalendarType(getCalendarType(getValue(calendarNode,"field_ol_type")));
        calendar.setSequenceNo(getIntValue(calendarNode,"field_ol_sequence_no"));
        calendar.setVersion(getVersion(getValue(calendarNode,"field_ol_version")));
        calendar.setCalendarId(getCalendarId(calendarNode,"calendar_id"));
        calendar.setBill(getBillId(getListValue(calendarNode,"field_ol_bill"),calendar.getCalendarId().getYear()));

       return calendar;
    }

    private CalendarType getCalendarType(String calendarType){
        return CalendarType.valueOf(calendarType);
    }

    private Version getVersion(String version)
    {
        return Version.valueOf(version.toUpperCase());
    }

    private List<BillId> getBillId(List<String> billNos, int year){
        List<BillId> billId = new ArrayList<BillId>();
        for (String billNo:billNos) {
            billId.add(new BillId(billNo, SessionYear.of(year)));
        }
        return billId;
    }

    private CalendarId getCalendarId(JsonNode calendarNode, String fieldName){
        TypeReference<CalendarIdView> calendarIdType = new TypeReference<CalendarIdView>() {};
        Optional<CalendarIdView> calendarIdViews = deserializeValue(calendarNode, fieldName, calendarIdType);
        return calendarIdViews.map(CalendarIdView::toCalendarId).orElseThrow(() -> new ParseError("no calendarID"));
    }
}
