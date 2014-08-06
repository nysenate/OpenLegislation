package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents committee meeting vote information such as who was present during the vote
 * as well as the votes for one or more bills.
 */
public class AgendaVoteCommittee implements Serializable
{
    private static final long serialVersionUID = -8716962051349762641L;

    /** Reference to the id of the committee the votes are associated with. */
    private CommitteeId committeeId;

    /** Name of the committee chair. */
    private String chair;

    /** Date/time of the meeting. */
    private LocalDateTime meetingDateTime;

    /** The attendance list. */
    private List<AgendaVoteAttendance> attendance = new ArrayList<>();

    /** Collection of bills that have been voted on. */
    private Map<BillId, AgendaVoteBill> votedBills;

    /** --- Constructors --- */

    public AgendaVoteCommittee() {
        this.votedBills = new HashMap<>();
    }

    public AgendaVoteCommittee(CommitteeId committeeId, String chair, LocalDateTime meetingDateTime) {
        this();
        this.setCommitteeId(committeeId);
        this.setChair(chair);
        this.setMeetingDateTime(meetingDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addVoteBill(AgendaVoteBill agendaVoteBill) {
        this.votedBills.put(agendaVoteBill.getBillId(), agendaVoteBill);
    }

    public void removeVoteBill(BillId billId) {
        this.votedBills.remove(billId);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaVoteCommittee other = (AgendaVoteCommittee) obj;
        return Objects.equals(this.committeeId, other.committeeId) &&
               Objects.equals(this.chair, other.chair) &&
               Objects.equals(this.meetingDateTime, other.meetingDateTime) &&
               Objects.equals(this.attendance, other.attendance) &&
               Objects.equals(this.votedBills, other.votedBills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(committeeId, chair, meetingDateTime, attendance, votedBills);
    }

    @Override
    public String toString () {
        return committeeId + " meetingDateTime: " + meetingDateTime;
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

    public LocalDateTime getMeetingDateTime () {
        return meetingDateTime;
    }

    public void setMeetingDateTime (LocalDateTime meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public Map<BillId, AgendaVoteBill> getVotedBills() {
        return votedBills;
    }

    public void setVotedBills(Map<BillId, AgendaVoteBill> votedBills) {
        this.votedBills = votedBills;
    }

    public List<AgendaVoteAttendance> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<AgendaVoteAttendance> attendance) {
        this.attendance = attendance;
    }

    public void addAttendance(AgendaVoteAttendance attendance) {
        this.attendance.add(attendance);
    }
}
