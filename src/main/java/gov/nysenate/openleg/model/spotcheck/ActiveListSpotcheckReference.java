package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kyle on 10/29/14.
 */
public class ActiveListSpotcheckReference {

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

    /** The date time that the reference is generated (current time) */
    private LocalDateTime referenceDate;                //////////////////////////// not sure if needed

    /** Active list entries. */
    private List<CalendarActiveListEntry> entries;

    /** Date when the calendar is relseased*/
    private LocalDateTime reportDate;

    public ActiveListSpotcheckReference(){}

    public ActiveListSpotcheckReference(Integer sequenceNo, CalendarId calendarId, /*String notes,*/
                                        LocalDate calDate, LocalDateTime releasedDateTime, LocalDateTime reportDate,
                                        List<CalendarActiveListEntry> entries){
        this.sequenceNo = sequenceNo;
        this.calendarId = calendarId;
        //this.notes = notes;
        this.calDate = calDate;
        this.releaseDateTime = releasedDateTime;
        this.entries = entries;
        this.reportDate = reportDate;
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

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public List<CalendarActiveListEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CalendarActiveListEntry> entries) {
        this.entries = entries;
    }
}
