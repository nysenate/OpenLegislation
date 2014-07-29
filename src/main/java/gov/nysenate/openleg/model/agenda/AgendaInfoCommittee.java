package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The AgendaInfoCommittee models the agenda addenda which indicate committee meeting(s)
 * and the bills associated with those meetings. Although this class exposes details
 * pertaining to a meeting, the actual meeting info must be derived via any preceding/subsequent
 * addenda and especially though the data contained within the 'notes' field.
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

    /** A list of committee items (i.e. bills) that are up for consideration. */
    private List<AgendaInfoCommitteeItem> items = new ArrayList<>();

    /** --- Constructors --- */

    public AgendaInfoCommittee() {}

    public AgendaInfoCommittee(CommitteeId committeeId, String chair, String location, String notes, Date meetDateTime) {
        this();
        this.setCommitteeId(committeeId);
        this.setChair(chair);
        this.setLocation(location);
        this.setNotes(notes);
        this.setMeetDateTime(meetDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addCommitteeItem(AgendaInfoCommitteeItem item) {
        items.add(item);
    }

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

    public List<AgendaInfoCommitteeItem> getItems() {
        return items;
    }

    public void setItems(List<AgendaInfoCommitteeItem> items) {
        this.items = items;
    }
}
