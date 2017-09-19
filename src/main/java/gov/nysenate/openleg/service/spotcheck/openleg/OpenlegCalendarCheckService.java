package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.calendar.ActiveListView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.model.calendar.Calendar;
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
import java.util.Collections;
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
        checkCalDate(reference,content, observation);
        checkCalYear(reference,content, observation);
        checkReleaseDateTime(reference,content,observation);
        List<CalendarSupEntryView> referenceCalSupEntryViews = getCalendarSupEntryViews(reference);
        List<CalendarSupEntryView> contentCalSupEntryViews = getCalendarSupEntryViews(content);
        for (int index = 0; index < referenceCalSupEntryViews.size(); index++) {
            checkSectionType(referenceCalSupEntryViews.get(index), contentCalSupEntryViews.get(index), observation);
            checkIsBillHigh(referenceCalSupEntryViews.get(index), contentCalSupEntryViews.get(index),  observation);
            checkViewType(referenceCalSupEntryViews.get(index), contentCalSupEntryViews.get(index),  observation);
            checkSubBillInfoView(referenceCalSupEntryViews.get(index), contentCalSupEntryViews.get(index),  observation);
            checkBillCalNo(referenceCalSupEntryViews.get(index), contentCalSupEntryViews.get(index),  observation);
            checkSelectedVersion(referenceCalSupEntryViews.get(index), contentCalSupEntryViews.get(index), observation);
        }
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

    //*************************************************
    //METHODS TO CHECK FLOOR AND SUPPLEMENTAL CALENDARS

    protected void checkCalDate(CalendarSupView reference, CalendarSupView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getCalDate());
        String contentStr = OutputUtils.toJson(content.getCalDate());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_CAL_DATE, contentStr, referenceStr));
        }
    }

    protected void checkCalYear(CalendarSupView reference, CalendarSupView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getYear());
        String contentStr = OutputUtils.toJson(content.getYear());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_CAL_YEAR, contentStr, referenceStr));
        }
    }

    protected void checkReleaseDateTime(CalendarSupView reference, CalendarSupView content, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(reference.getReleaseDateTime());
        String contentStr = OutputUtils.toJson(content.getReleaseDateTime());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_RELEASE_DATE_TIME, contentStr, referenceStr));
        }
    }

    protected void checkSectionType(CalendarSupEntryView referenceView, CalendarSupEntryView contentView, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(referenceView.getSectionType());
        String contentStr = OutputUtils.toJson(contentView.getSectionType());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_SECTION_TYPE, contentStr, referenceStr));
        }
    }

    protected void checkIsBillHigh(CalendarSupEntryView referenceView, CalendarSupEntryView contentView, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(referenceView.getBillHigh());
        String contentStr = OutputUtils.toJson(contentView.getBillHigh());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_BILL_HIGH, contentStr, referenceStr));
        }
    }

    protected void checkViewType(CalendarSupEntryView referenceView, CalendarSupEntryView contentView, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(referenceView.getViewType());
        String contentStr = OutputUtils.toJson(contentView.getViewType());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_VIEW_TYPE, contentStr, referenceStr));
        }
    }

    protected void checkSubBillInfoView(CalendarSupEntryView referenceView, CalendarSupEntryView contentView, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(referenceView.getSubBillInfo());
        String contentStr = OutputUtils.toJson(contentView.getSubBillInfo());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_SUB_BILL_INFO_VIEW, contentStr, referenceStr));
        }
    }

    protected void checkBillCalNo(CalendarSupEntryView referenceView, CalendarSupEntryView contentView, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(referenceView.getBillCalNo());
        String contentStr = OutputUtils.toJson(contentView.getBillCalNo());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_BILL_CAL_NO, contentStr, referenceStr));
        }
    }

    protected void checkSelectedVersion(CalendarSupEntryView referenceView, CalendarSupEntryView contentView, SpotCheckObservation<CalendarEntryListId> observation) {
        String referenceStr = OutputUtils.toJson(referenceView.getSelectedVersion());
        String contentStr = OutputUtils.toJson(contentView.getSelectedVersion());
        if (!contentStr.equals(referenceStr)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_SELECTED_VERSION, contentStr, referenceStr));
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

}
