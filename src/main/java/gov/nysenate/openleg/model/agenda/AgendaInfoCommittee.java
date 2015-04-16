package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /** Reference to the parent agenda */
    private AgendaId agendaId;

    /** The version of this committee meeting */
    private Version addendum;

    /** Name of the committee chair. */
    private String chair;

    /** Location of the committee meeting. */
    private String location;

    /** Date/time of the meeting. */
    private LocalDateTime meetingDateTime;

    /** Any notes associated with this addendum. */
    private String notes;

    /** A list of committee items (i.e. bills) that are up for consideration. */
    private List<AgendaInfoCommitteeItem> items = new ArrayList<>();

    /** --- Constructors --- */

    public AgendaInfoCommittee() {}

    public AgendaInfoCommittee(CommitteeId committeeId, AgendaId agendaId, Version addendum,
                               String chair, String location, String notes, LocalDateTime meetDateTime) {
        this();
        this.setAgendaId(agendaId);
        this.setAddendum(addendum);
        this.setCommitteeId(committeeId);
        this.setChair(chair);
        this.setLocation(location);
        this.setNotes(notes);
        this.setMeetingDateTime(meetDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addCommitteeItem(AgendaInfoCommitteeItem item) {
        items.add(item);
    }

    public CommitteeAgendaAddendumId getId() {
        return new CommitteeAgendaAddendumId(agendaId, committeeId, addendum);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaInfoCommittee other = (AgendaInfoCommittee) obj;
        return Objects.equals(this.committeeId, other.committeeId) &&
               Objects.equals(this.chair, other.chair) &&
               Objects.equals(this.location, other.location) &&
               Objects.equals(this.meetingDateTime, other.meetingDateTime) &&
               Objects.equals(this.notes, other.notes) &&
               Objects.equals(this.items, other.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(committeeId, chair, location, meetingDateTime, notes, items);
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

    public LocalDateTime getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(LocalDateTime meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
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

    public AgendaId getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(AgendaId agendaId) {
        this.agendaId = agendaId;
    }

    public Version getAddendum() {
        return addendum;
    }

    public void setAddendum(Version addendum) {
        this.addendum = addendum;
    }
}
