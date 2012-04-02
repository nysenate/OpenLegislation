package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.LuceneField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("supplemental")
public class Supplemental {

    protected String id;

    @LuceneField("when")
    protected Date calendarDate;

    @LuceneField("releasedate")
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

    @JsonIgnore
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