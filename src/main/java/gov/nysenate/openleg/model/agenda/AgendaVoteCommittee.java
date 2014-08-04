package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
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
    private Date meetDateTime;

    /** The attendance list. */
    private List<AgendaVoteAttendance> attendance = new ArrayList<>();

    /** Collection of bills that have been voted on. */
    private Map<BillId, AgendaVoteBill> votedBills;

    /** --- Constructors --- */

    public AgendaVoteCommittee() {
        this.votedBills = new HashMap<>();
    }

    public AgendaVoteCommittee(CommitteeId committeeId, String chair, Date meetDateTime) {
        this();
        this.setCommitteeId(committeeId);
        this.setChair(chair);
        this.setMeetDateTime(meetDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addVoteBill(AgendaVoteBill agendaVoteBill) {
        this.votedBills.put(agendaVoteBill.getBillId(), agendaVoteBill);
    }

    public void removeVoteBill(BillId billId) {
        this.votedBills.remove(billId);
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

    public Date getMeetDateTime() {
        return meetDateTime;
    }

    public void setMeetDateTime(Date meetDateTime) {
        this.meetDateTime = meetDateTime;
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
