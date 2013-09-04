package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.List;

public class Sequence {

    private String no;

    private String id;

    private Date actCalDate;

    private Date releaseDateTime;

    private List<CalendarEntry> calendarEntries;

    private String notes;

    private Supplemental supplemental;

    public Supplemental getSupplemental() {
        return supplemental;
    }

    public void setSupplemental(Supplemental supplemental) {
        this.supplemental = supplemental;
    }

    public Date getActCalDate() {
        return actCalDate;
    }

    public void setActCalDate(Date actCalDate) {
        this.actCalDate = actCalDate;
    }

    public Date getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(Date releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Sequence)
        {
            if ( ((Sequence)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }


}