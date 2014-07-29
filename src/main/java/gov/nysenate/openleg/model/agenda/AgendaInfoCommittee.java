package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class AgendaInfoCommittee implements Serializable
{
    private static final long serialVersionUID = 6788116636375650193L;

    /** Reference to the id of the committee this info is associated with. */
    private CommitteeId committeeId;

    /** Name of the committee chair. */
    private String chair;

    /** Location of the committee meeting. */
    private String location;

    /** Date/time of the meeting. */
    private Date meetDateTime;

    /** Any notes associated with this addendum. */
    private String notes;

    /** */
    private Map<String, AgendaInfoCommitteeItem> items;

    /** --- Constructors --- */

    public AgendaInfoCommittee() {
        this.setItems(new HashMap<String, AgendaInfoCommitteeItem>());
    }

    public AgendaInfoCommittee(CommitteeId committeeId, String chair, String location, String notes, Date meetDateTime) {
        this();
        this.setCommitteeId(committeeId);
        this.setChair(chair);
        this.setLocation(location);
        this.setNotes(notes);
        this.setMeetDateTime(meetDateTime);
    }

    /** --- Functional Getters/Setters --- */

    /** --- Basic Getters/Setters --- */

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(CommitteeId committeeId) {
        this.committeeId = committeeId;
    }

    public String getChair() {
        return chair;
    }

    public void setChair(String chair) {
        this.chair = chair;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getMeetDateTime() {
        return meetDateTime;
    }

    public void setMeetDateTime(Date meetDateTime) {
        this.meetDateTime = meetDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Map<String, AgendaInfoCommitteeItem> getItems() {
        return items;
    }

    public void setItems(Map<String, AgendaInfoCommitteeItem> items) {
        this.items = items;
    }
}
