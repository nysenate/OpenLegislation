package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenlegCalendarCheckService
        extends BaseSpotCheckService<CalendarEntryListId, CalendarEntryList, CalendarEntryList> {
    Logger logger = LoggerFactory.getLogger(OpenlegBillCheckService.class);


    @Override
    public SpotCheckObservation<CalendarEntryListId> check(CalendarEntryList content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(CalendarEntryList content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(CalendarEntryList content, CalendarEntryList reference) {
        if(content instanceof CalendarSupView) {
            return checkFloorCals((CalendarSupView) content,(CalendarSupView) reference);
        }
        else if (content instanceof ActiveListView) {
            return checkActiveLists( (ActiveListView) content,(ActiveListView) reference );
        }
        return null;
    }

    /**
     * Check for any mismatches between Openleg reference and Openleg source calenders
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    private SpotCheckObservation<CalendarEntryListId>  checkFloorCals(CalendarSupView content, CalendarSupView reference) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(reference.getCalendarEntryListId());
        checkFloorCalDate(content,reference, observation);
        checkFloorCalYear(content,reference, observation);
        checkFloorReleaseDateTime(content,reference,observation);
        StringBuffer referenceEntryViewsAsString = new StringBuffer();
        StringBuffer contentEntryViewsAsString = new StringBuffer();
        for(CalendarSupEntryView calendarSupEntryView: getCalendarSupEntryViews(reference)) {
            referenceEntryViewsAsString.append(calendarSupEntryView.toString());
        }
        for(CalendarSupEntryView calendarSupEntryView: getCalendarSupEntryViews(content)) {
            contentEntryViewsAsString.append(calendarSupEntryView.toString());
        }
        checkFloorCalendarSupEntryViews(contentEntryViewsAsString.toString(), referenceEntryViewsAsString.toString(), observation);
        return observation;
    }

    /**
     * Check for any mismatches between Openleg reference and Openleg source active lists
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    private SpotCheckObservation<CalendarEntryListId>  checkActiveLists(ActiveListView content, ActiveListView reference) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(reference.getCalendarEntryListId());
        checkActiveListCalDate(content,reference,observation);
        checkActiveListReleaseDateTime(content,reference,observation);
        checkActiveListNotes(content,reference,observation);
        checkActiveListViewtype(content,reference,observation);
        checkActiveListSequenceNumber(content,reference,observation);
        StringBuffer referenceEntryViewsAsString = new StringBuffer();
        StringBuffer contentEntryViewsAsString = new StringBuffer();
        for(CalendarEntryView calendarEntryView: reference.getEntries().getItems()) {
            referenceEntryViewsAsString.append(calendarEntryView.toString());
        }

        for(CalendarEntryView calendarEntryView: content.getEntries().getItems()) {
            contentEntryViewsAsString.append(calendarEntryView.toString());
        }
        checkActiveListCalendarEntryViews(contentEntryViewsAsString.toString(), referenceEntryViewsAsString.toString(),observation);
        return observation;
    }

    //*************************************************
    //METHODS TO CHECK FLOOR AND SUPPLEMENTAL CALENDARS

    protected void checkFloorCalDate(CalendarSupView content, CalendarSupView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getCalDate()),OutputUtils.toJson(reference.getCalDate()),observation,SpotCheckMismatchType.FLOOR_CAL_DATE);
    }

    protected void checkFloorCalYear(CalendarSupView content, CalendarSupView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getYear()),OutputUtils.toJson(reference.getYear()),observation,SpotCheckMismatchType.FLOOR_CAL_YEAR);
    }

    protected void checkFloorReleaseDateTime(CalendarSupView content, CalendarSupView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getReleaseDateTime()),OutputUtils.toJson(reference.getReleaseDateTime()),observation,SpotCheckMismatchType.FLOOR_RELEASE_DATE_TIME);
    }

    protected void checkFloorCalendarSupEntryViews(String content, String reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(content,reference,observation,SpotCheckMismatchType.FLOOR_ENTRY);
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

    protected void checkActiveListCalDate(ActiveListView content, ActiveListView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getCalDate()),OutputUtils.toJson(reference.getCalDate()),observation,SpotCheckMismatchType.ACTIVE_LIST_CAL_DATE);
    }

    protected void checkActiveListReleaseDateTime(ActiveListView content, ActiveListView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getReleaseDateTime()),OutputUtils.toJson(reference.getReleaseDateTime()),observation,SpotCheckMismatchType.ACTIVE_LIST_RELEASE_DATE_TIME);
    }

    protected void checkActiveListNotes(ActiveListView content, ActiveListView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getNotes()),OutputUtils.toJson(reference.getNotes()),observation,SpotCheckMismatchType.ACTIVE_LIST_NOTES);
    }

    protected void checkActiveListViewtype(ActiveListView content, ActiveListView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getViewType()),OutputUtils.toJson(reference.getViewType()),observation,SpotCheckMismatchType.ACTIVE_LIST_VIEW_TYPE);
    }

    protected void checkActiveListSequenceNumber(ActiveListView content, ActiveListView reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content.getSequenceNumber()),OutputUtils.toJson(reference.getSequenceNumber()),observation,SpotCheckMismatchType.ACTIVE_LIST_SEQUENCE_NUMBER);
    }

    protected void checkActiveListCalendarEntryViews(String content, String reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(OutputUtils.toJson(content),OutputUtils.toJson(reference),observation,SpotCheckMismatchType.ACTIVE_LIST_ENTRY);
    }

}
