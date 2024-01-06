package gov.nysenate.openleg.spotchecks.sensite.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.CalendarType;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDump;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteJsonParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by PKS on 2/25/16.
 */

@Service
public class CalendarJsonParser extends SenateSiteJsonParser {

    public List<SenateSiteCalendar> parseCalendars(SenateSiteDump calendarDump) throws ParseError {
        return calendarDump.getDumpFragments().stream()
                .flatMap(fragment -> extractCalendarsFromFragment(fragment).stream())
                .toList();
    }

    private List<SenateSiteCalendar> extractCalendarsFromFragment(SenateSiteDumpFragment fragment) throws ParseError {
        try {
            JsonNode calendarMap = objectMapper.readTree(fragment.getFragmentFile())
                    .path("nodes");
            if (calendarMap.isMissingNode()) {
                throw new ParseError("Could not locate \"nodes\" node in senate site calendar dump fragment file: " +
                        fragment.getFragmentFile().getAbsolutePath());
            }
            List<SenateSiteCalendar> calendars = new LinkedList<>();
            for (JsonNode calendarNode : calendarMap) {
                CalendarId calendarId = getCalendarId(calendarNode);
                JsonNode subCalList = calendarNode.path("field_ol_cal").path("und");
                for (JsonNode subCalNode : subCalList) {
                    JsonNode subCalNodeValue = subCalNode.path("value");
                    calendars.add(extractSenSiteCalendar(subCalNodeValue, calendarId, fragment));
                }
            }
            return calendars;
        } catch (IOException | NoSuchElementException ex) {
            throw new ParseError("error while reading senate site calendar dump fragment file: " +
                    fragment.getFragmentFile().getAbsolutePath(),
                    ex);
        }
    }

    private SenateSiteCalendar extractSenSiteCalendar(JsonNode subCalNode, CalendarId calendarId,
                                                      SenateSiteDumpFragment fragment) throws IOException {
        SenateSiteCalendar calendar = new SenateSiteCalendar(fragment.getDumpId().dumpTime());

        calendar.setBillCalNumbers(getIntListValue(subCalNode, "field_ol_bill_cal_number"));
        calendar.setCalendarType(getCalendarType(getValue(subCalNode,"field_ol_type")));
        calendar.setSequenceNo(getIntValue(subCalNode,"field_ol_sequence_no"));
        calendar.setVersion(getVersion(getValue(subCalNode,"field_ol_version")));
        calendar.setCalendarId(calendarId);
        List<String> printNos = getStringListValue(subCalNode, "field_ol_bill");
        calendar.setBill(getBillId(printNos, calendar.getCalendarId().getYear()));

       return calendar;
    }

    private CalendarType getCalendarType(String calendarType)
    {
        return CalendarType.valueOf(StringUtils.upperCase(calendarType));
    }

    private Version getVersion(String version)

    {
        return Version.of(version);
    }

    private List<BillId> getBillId(List<String> billNos, int year){
        List<BillId> billId = billNos.stream().map(billNo -> new BillId(billNo, SessionYear.of(year))).toList();
        return billId;
    }

    private CalendarId getCalendarId(JsonNode calendarNode){
        int calendarYear = getIntValue(calendarNode, "field_ol_year");
        int calendarNo = getIntValue(calendarNode, "field_ol_cal_no");
        return new CalendarId(calendarNo, calendarYear);
    }
}
