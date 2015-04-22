package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kyle on 10/29/14.
 */
public class ActiveListSpotcheckReference {

    private SpotCheckReferenceId referenceId;
    /** A sequence number that identifies this active list. */
    private Integer sequenceNo;
    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** Any notes pertaining to this active list. */
    //private String notes;

    /** The calendar date associated with this Activelist. */
    private LocalDate calDate;

    /** The date time this active list was released. */
    private LocalDateTime releaseDateTime;

    /** Active list entries. */
    private List<CalendarEntry> entries;

    /** The date time that the reference is generated (current time) */
    private LocalDateTime referenceDate;

    public ActiveListSpotcheckReference(){}

    public ActiveListSpotcheckReference(Integer sequenceNo, CalendarId calendarId, LocalDate calDate,
                                        LocalDateTime releasedDateTime, LocalDateTime referenceDate,
                                        List<CalendarEntry> entries){
        this.sequenceNo = sequenceNo;
        this.calendarId = calendarId;
        //this.notes = notes;
        this.calDate = calDate;
        this.releaseDateTime = releasedDateTime;
        this.entries = entries;
        this.referenceDate = referenceDate;
    }

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, this.referenceDate);
    }


    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public CalendarId getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(CalendarId calendarId) {
        this.calendarId = calendarId;
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    public void setCalDate(LocalDate calDate) {
        this.calDate = calDate;
    }

    public LocalDateTime getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(LocalDateTime releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDateTime referenceDate) {
        this.referenceDate = referenceDate;
    }

    public List<CalendarEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CalendarEntry> entries) {
        this.entries = entries;
    }
}
