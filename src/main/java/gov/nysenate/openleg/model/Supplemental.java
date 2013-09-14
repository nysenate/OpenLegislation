package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Supplemental {

    protected String id;

    protected Date calendarDate;

    protected Date releaseDateTime;

    protected List<Section> sections;

    protected List<Sequence> sequences;

    protected String supplementalId;

    protected Calendar calendar;

    public void addSequence(Sequence sequence) {
        if(sequences == null) {
            sequences = new ArrayList<Sequence>();
        }
        else {
            sequences.remove(sequence);
        }

        sequences.add(sequence);
    }

    public List<Sequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<Sequence> sequences) {
        this.sequences = sequences;
    }

    public Date getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(Date calendarDate) {
        this.calendarDate = calendarDate;
    }

    public Date getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(Date releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    public String getSupplementalId() {
        return supplementalId;
    }

    public void setSupplementalId(String supplementalId) {
        this.supplementalId = supplementalId;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Supplemental)
        {
            if ( ((Supplemental)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }
}