package gov.nysenate.openleg.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("meeting")
@XmlRootElement
public class Meeting extends BaseObject
{
    protected Date meetingDateTime;

    protected String meetday = "";

    protected String location = "";

    protected String id;

    protected String committeeName = "";

    protected String committeeChair = "";

    protected List<Bill> bills;

    protected String notes = "";

    protected List<Addendum> addendums;

    public Meeting() {
        addendums = new ArrayList<Addendum>();
        bills = new ArrayList<Bill>();
    }

    public Meeting(String id) {
        this.setId(id);
        addendums = new ArrayList<Addendum>();
        bills = new ArrayList<Bill>();
    }

    public String getCommitteeChair() {
        return committeeChair;
    }

    public void setCommitteeChair(String committeeChair) {
        this.committeeChair = committeeChair;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(this.getMeetingDateTime());
        return cal.get(java.util.Calendar.YEAR);
    }

    @JsonIgnore
    public String getOid()
    {
        return this.getCommitteeName()+"-"+new SimpleDateFormat("MM-dd-yyyy").format(this.getMeetingDateTime());
    }

    @JsonIgnore
    public List<Addendum> getAddendums() {
        return addendums;
    }

    public void setAddendums(List<Addendum> addendums) {
        this.addendums = addendums;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetday() {
        return meetday;
    }

    public void setMeetday(String meetday) {
        this.meetday = meetday;
    }


    public Date getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(Date meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Meeting)
        {
            if ( ((Meeting)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return this.id + " : " + meetingDateTime.getTime();
    }
}