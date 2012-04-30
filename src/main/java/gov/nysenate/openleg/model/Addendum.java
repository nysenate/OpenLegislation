package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.LuceneField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("addendum")
public class Addendum {

    @LuceneField
    private String id;

    @LuceneField
    private String addendumId;

    @LuceneField
    private String weekOf;

    @LuceneField
    private Date publicationDateTime;

    private List<Meeting> meetings;

    @LuceneField
    private Agenda agenda;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public Addendum ()
    {
        meetings = new ArrayList<Meeting>();
    }

    public Addendum(String id) {
        this.setId(id);
        meetings = new ArrayList<Meeting>();
    }


    public String getAddendumId() {
        return addendumId;
    }

    public void setAddendumId(String addendumId) {
        this.addendumId = addendumId;
    }

    public String getWeekOf() {
        return weekOf;
    }

    public void setWeekOf(String weekOf) {
        this.weekOf = weekOf;
    }

    public Date getPublicationDateTime() {
        return publicationDateTime;
    }

    public void setPublicationDateTime(Date publicationDateTime) {
        this.publicationDateTime = publicationDateTime;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public void addMeeting(Meeting meeting) {
        this.meetings.add(meeting);
    }

    public void removeMeeting(Meeting meeting) {
        this.meetings.remove(meeting);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Addendum)
        {
            if ( ((Addendum)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return this.getId() + "-" + this.getPublicationDateTime() + "-" + this.getMeetings();
    }

}
