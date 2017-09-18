package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.calendar.ActiveListView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("openlegCalendarCheck")
public class OpenlegCalendarCheckService {
    Logger logger = LoggerFactory.getLogger(OpenlegBillCheckService.class);

    public SpotCheckObservation<CalendarEntryListId> check(CalendarSupEntryView content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    public SpotCheckObservation<CalendarEntryListId> check(CalendarSupEntryView content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    /**
     * Check for any mismatches between Openleg reference and Openleg source calenders
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    public SpotCheckObservation<CalendarEntryListId>  checkFloorCals(CalendarSupView reference, CalendarSupView content) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(reference.getCalendarEntryListId());
        return observation;
    }

    /**
     * Check for any mismatches between Openleg reference and Openleg source active lists
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    public SpotCheckObservation<CalendarEntryListId>  checkActiveLists(ActiveListView reference, ActiveListView content) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(reference.getCalendarEntryListId());
        return observation;
    }
}
