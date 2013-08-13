package gov.nysenate.openleg.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author GraylinKim
 */
@XStreamAlias("meeting")
@XmlRootElement
public class Meeting extends BaseObject
{
    /**
     * The date and time of the meeting.
     */
    protected Date meetingDateTime;

    /**
     * Day of the week; e.g. Wednesday.
     */
    protected String meetday = "";

    /**
     * The physical location of the meeting; e.g. 332 CAP.
     */
    protected String location = "";

    /**
     * The internal storage id for this meeting.
     */
    protected String id;
    /**
     * The unique identifier for this object.
     */
    protected String oid;

    /**
     * The name of the committee that is meeting
     */
    protected String committeeName = "";

    /**
     * The full name of the chair of the committee
     */
    protected String committeeChair = "";

    /**
     * A list of bills on the agenda for the meeting
     */
    protected List<Bill> bills;

    /**
     * The notes for the meeting. Will often detail appointments to be made or experts to appear.
     */
    protected String notes = "";

    /**
     * A list of agenda addendums that affect this meeting
     */
    @JsonIgnore
    protected List<Addendum> addendums;

    /**
     * JavaBean Constructor
     */
    public Meeting() {
        addendums = new ArrayList<Addendum>();
        bills = new ArrayList<Bill>();
    }

    /**
     *
     * @param id
     */
    public Meeting(String committeeName, Date meetingDateTime) {
        this.setCommitteeName(committeeName);
        this.setMeetingDateTime(meetingDateTime);
        this.setOid(committeeName+"-"+new SimpleDateFormat("MM-dd-yyyy").format(this.getMeetingDateTime()));

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(meetingDateTime);
        this.setSession(calendar.get(java.util.Calendar.YEAR));
        this.setYear(calendar.get(java.util.Calendar.YEAR));

        addendums = new ArrayList<Addendum>();
        bills = new ArrayList<Bill>();
    }

    /**
     * The object type of the meeting.
     */
    @JsonIgnore
    public String getOtype()
    {
        return "meeting";
    }

    /**
     * @return - The object's id.
     */
    public String getOid()
    {
        return this.oid;
    }

    /**
     * @param oid - The object's new oid
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    public String getCommitteeChair() {
        return committeeChair;
    }

    public void setCommitteeChair(String committeeChair) {
        this.committeeChair = committeeChair;
    }

    @JsonIgnore
    public int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(this.getMeetingDateTime());
        return cal.get(java.util.Calendar.YEAR);
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
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Meeting)
        {
            if ( ((Meeting)obj).getOid().equals(this.getOid()))
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return this.oid + " : " + meetingDateTime.getTime();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getId() {
        return id;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setId(String id) {
        this.id = id;
    }
}