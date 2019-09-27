package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.bill.SimpleBillInfoView;
import gov.nysenate.openleg.client.view.calendar.*;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

@Service
public class OpenlegCalendarCheckService
        implements SpotCheckService<CalendarEntryListId, CalendarEntryList, CalendarEntryList> {

    @Autowired private SpotCheckUtils spotCheckUtils;

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(CalendarEntryList content, CalendarEntryList reference) {
        if (content instanceof CalendarSupView) {
            return checkFloorCals((CalendarSupView) content, (CalendarSupView) reference);
        } else if (content instanceof ActiveListView) {
            return checkActiveLists((ActiveListView) content, (ActiveListView) reference);
        }
        throw new IllegalStateException("Unhandled calendar entry list type" + content.getClass().getSimpleName());
    }

    /**
     * Check for any mismatches between Openleg reference and Openleg source calendars
     *
     * @param content   ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    private SpotCheckObservation<CalendarEntryListId> checkFloorCals(CalendarSupView content, CalendarSupView reference) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(
                new SpotCheckReferenceId(SpotCheckRefType.OPENLEG_CAL, LocalDateTime.now()),
                reference.getCalendarEntryListId());
        checkFloorCalDate(content, reference, observation);
        checkFloorCalYear(content, reference, observation);
        checkFloorReleaseDateTime(content, reference, observation);
        checkFloorCalendarSupEntryViews(content, reference, observation);
        return observation;
    }

    /**
     * Check for any mismatches between Openleg reference and Openleg source active lists
     *
     * @param content   ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    private SpotCheckObservation<CalendarEntryListId> checkActiveLists(ActiveListView content, ActiveListView reference) {
        final SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(
                new SpotCheckReferenceId(SpotCheckRefType.OPENLEG_CAL, LocalDateTime.now()),
                reference.getCalendarEntryListId());
        checkActiveListCalDate(content, reference, observation);
        checkActiveListReleaseDateTime(content, reference, observation);
        checkActiveListNotes(content, reference, observation);
        checkActiveListCalendarEntryViews(content, reference, observation);
        return observation;
    }

    /* --- Floor Cal Check Methods --- */

    protected void checkFloorCalDate(CalendarSupView content, CalendarSupView reference,
                                     SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkObject(content.getCalDate(), reference.getCalDate(), observation, FLOOR_CAL_DATE);
    }

    protected void checkFloorCalYear(CalendarSupView content, CalendarSupView reference,
                                     SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkObject(content.getYear(), reference.getYear(), observation, FLOOR_CAL_YEAR);
    }

    protected void checkFloorReleaseDateTime(CalendarSupView content, CalendarSupView reference,
                                             SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkObject(content.getReleaseDateTime(), reference.getReleaseDateTime(), observation, FLOOR_RELEASE_DATE_TIME);
    }

    protected void checkFloorCalendarSupEntryViews(CalendarSupView content, CalendarSupView reference,
                                                   SpotCheckObservation<CalendarEntryListId> observation) {
        List<CalendarSupEntryView> contentEntries = getCalendarSupEntryViews(content);
        List<CalendarSupEntryView> supEntries = getCalendarSupEntryViews(reference);
        spotCheckUtils.checkCollection(contentEntries, supEntries, observation, FLOOR_ENTRY, this::getSupEntryString, "\n");
    }

    private List<CalendarSupEntryView> getCalendarSupEntryViews(CalendarSupView calendarSupView) {
        return calendarSupView.getEntriesBySection().getItems().values().stream()
                .map(ListView::getItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private StringBuilder getSupEntryString(CalendarSupEntryView entry) {
        return new StringBuilder()
                .append(entry.getSectionType()).append(" ")
                .append(getEntryString(entry))
                .append(" ")
                .append("high:")
                .append(entry.getBillHigh()).append(" ")
                .append("sub:")
                .append(Optional.ofNullable(entry.getSubBillInfo())
                        .map(SimpleBillInfoView::getPrintNo));
    }


    /* --- Active List Check Methods --- */

    protected void checkActiveListCalDate(ActiveListView content, ActiveListView reference,
                                          SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkObject(content.getCalDate(), reference.getCalDate(), observation, ACTIVE_LIST_CAL_DATE);
    }

    protected void checkActiveListReleaseDateTime(ActiveListView content, ActiveListView reference,
                                                  SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkObject(content.getReleaseDateTime(), reference.getReleaseDateTime(),
                observation, ACTIVE_LIST_RELEASE_DATE_TIME);
    }

    protected void checkActiveListNotes(ActiveListView content, ActiveListView reference,
                                        SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkObject(
                StringUtils.normalizeSpace(content.getNotes()),
                StringUtils.normalizeSpace(reference.getNotes()),
                observation, ACTIVE_LIST_NOTES);
    }

    protected void checkActiveListCalendarEntryViews(ActiveListView content, ActiveListView reference,
                                                     SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkCollection(getActiveListEntries(content), getActiveListEntries(reference),
                observation, ACTIVE_LIST_ENTRY, this::getEntryString, "\n");
    }

    private List<CalendarEntryView> getActiveListEntries(ActiveListView activeListView) {
        return Optional.ofNullable(activeListView.getEntries()).map(ListView::getItems).orElse(null);
    }

    private StringBuilder getEntryString(CalendarEntryView entry) {
        return new StringBuilder()
                .append(entry.getBasePrintNoStr())
                .append(" amend:")
                .append(entry.getSelectedVersion()).append(" ")
                .append("calNo:")
                .append(entry.getBillCalNo());
    }

}
