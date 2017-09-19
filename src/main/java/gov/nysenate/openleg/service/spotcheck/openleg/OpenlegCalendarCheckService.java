package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.calendar.ActiveListView;
import gov.nysenate.openleg.client.view.calendar.CalendarEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        checkFloorCalDate(reference,content, observation);
        checkFloorCalYear(reference,content, observation);
        checkFloorReleaseDateTime(reference,content,observation);
        StringBuffer referenceEntryViewsAsString = new StringBuffer();
        StringBuffer contentEntryViewsAsString = new StringBuffer();
        for(CalendarSupEntryView calendarSupEntryView: getCalendarSupEntryViews(reference)) {
            referenceEntryViewsAsString.append(calendarSupEntryView.toString());
        }
        for(CalendarSupEntryView calendarSupEntryView: getCalendarSupEntryViews(content)) {
            contentEntryViewsAsString.append(calendarSupEntryView.toString());
        }
        checkFloorCalendarSupEntryViews(referenceEntryViewsAsString.toString(), contentEntryViewsAsString.toString(), observation);
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
        checkActiveListCalDate(reference,content,observation);
        checkActiveListReleaseDateTime(reference,content,observation);
        checkActiveListNotes(reference,content,observation);
        checkActiveListViewtype(reference,content,observation);
        checkActiveListSequenceNumber(reference,content,observation);
        StringBuffer referenceEntryViewsAsString = new StringBuffer();
        StringBuffer contentEntryViewsAsString = new StringBuffer();
        for(CalendarEntryView calendarEntryView: reference.getEntries().getItems()) {
            referenceEntryViewsAsString.append(calendarEntryView.toString());
        }

        for(CalendarEntryView calendarEntryView: content.getEntries().getItems()) {
            contentEntryViewsAsString.append(calendarEntryView.toString());
        }
        checkActiveListCalendarEntryViews(referenceEntryViewsAsString.toString(), contentEntryViewsAsString.toString(),observation);
        return observation;
    }

    //*************************************************
    //METHODS TO CHECK FLOOR AND SUPPLEMENTAL CALENDARS

    protected void checkFloorCalDate(CalendarSupView reference, CalendarSupView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getCalDate());
        String contentStr = OutputUtils.toJson(content.getCalDate());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_CAL_DATE, contentStr, referenceStr));
        }
    }

    protected void checkFloorCalYear(CalendarSupView reference, CalendarSupView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getYear());
        String contentStr = OutputUtils.toJson(content.getYear());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_CAL_YEAR, contentStr, referenceStr));
        }
    }

    protected void checkFloorReleaseDateTime(CalendarSupView reference, CalendarSupView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getReleaseDateTime());
        String contentStr = OutputUtils.toJson(content.getReleaseDateTime());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_RELEASE_DATE_TIME, contentStr, referenceStr));
        }
    }

    protected void checkFloorCalendarSupEntryViews(String referenceStr, String contentStr, SpotCheckObservation<CalendarEntryListId> observation) {
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_ENTRY, contentStr, referenceStr));
        }
    }

    //HELPER METHODS FOR FLOOR AND SUPPLEMENTAL CALENDARS
    private List<CalendarSupEntryView> getCalendarSupEntryViews(CalendarSupView calendarSupView) {
        return calendarSupView.getEntriesBySection()
                .getItems()
                .values()
                .stream()
                .map(calendarSupViewMap -> calendarSupViewMap.getItems())
                .flatMap(calendarSupEntryView -> calendarSupEntryView.stream())
                .collect(Collectors.toList());
    }


    //*************************************************
    //METHODS TO CHECK FLOOR AND SUPPLEMENTAL CALENDARS

    protected void checkActiveListCalDate(ActiveListView reference, ActiveListView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getCalDate());
        String contentStr = OutputUtils.toJson(content.getCalDate());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_CAL_DATE, contentStr, referenceStr));
        }
    }

    protected void checkActiveListReleaseDateTime(ActiveListView reference, ActiveListView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getReleaseDateTime());
        String contentStr = OutputUtils.toJson(content.getReleaseDateTime());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_RELEASE_DATE_TIME, contentStr, referenceStr));
        }
    }

    protected void checkActiveListNotes(ActiveListView reference, ActiveListView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getNotes());
        String contentStr = OutputUtils.toJson(content.getNotes());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_NOTES, contentStr, referenceStr));
        }
    }

    protected void checkActiveListViewtype(ActiveListView reference, ActiveListView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getViewType());
        String contentStr = OutputUtils.toJson(content.getViewType());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_VIEW_TYPE, contentStr, referenceStr));
        }
    }

    protected void checkActiveListSequenceNumber(ActiveListView reference, ActiveListView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getSequenceNumber());
        String contentStr = OutputUtils.toJson(content.getSequenceNumber());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_SEQUENCE_NUMBER, contentStr, referenceStr));
        }
    }

    protected void checkActiveListCalendarEntryViews(String referenceStr, String contentStr, SpotCheckObservation<CalendarEntryListId> observation) {
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_ENTRY, contentStr, referenceStr));
        }
    }

}
