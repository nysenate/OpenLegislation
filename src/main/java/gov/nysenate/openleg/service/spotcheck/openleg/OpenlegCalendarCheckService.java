package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.calendar.CalendarEntryView;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class OpenlegCalendarCheckService {
    Logger logger = LoggerFactory.getLogger(OpenlegBillCheckService.class);

    public SpotCheckObservation<CalendarEntryListId> check(CalendarEntryView content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    public SpotCheckObservation<CalendarEntryListId> check(CalendarEntryView content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    /**
     * Check the mismatch between openleg sobi-processing and xml-data-processing Calenders
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    public SpotCheckObservation<CalendarEntryListId>  check(CalendarEntryView content, CalendarEntryView reference) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>();
        return observation;
    }
}
