package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.entity.SessionMember;

import java.io.Serializable;
import java.util.Objects;

public class AgendaVoteAttendance implements Serializable, Comparable<AgendaVoteAttendance>
{
    private static final long serialVersionUID = -4328021920936602603L;

    /** Member in the attendance list. */
    private SessionMember member;

    /** The order in which this member is listed. */
    private int rank;

    /** Indicates the political party of the member. */
    private String party;

    /** Indicates the attendance status (e.g. Present) */
    private String attendStatus;

    /** --- Constructors --- */

    public AgendaVoteAttendance() {}

    public AgendaVoteAttendance(SessionMember member, int rank, String party, String attendStatus) {
        this();
        this.setMember(member);
        this.setRank(rank);
        this.setParty(party);
        this.setAttendStatus(attendStatus);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaVoteAttendance other = (AgendaVoteAttendance) obj;
        return Objects.equals(this.member, other.member) &&
               Objects.equals(this.rank, other.rank) &&
               Objects.equals(this.party, other.party) &&
               Objects.equals(this.attendStatus, other.attendStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, rank, party, attendStatus);
    }

    @Override
    public int compareTo(AgendaVoteAttendance o) {
        return this.getRank().compareTo(o.getRank());
    }

    /** --- Basic Getters/Setters --- */

    public SessionMember getMember() {
        return member;
    }

    public void setMember(SessionMember member) {
        this.member = member;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAttendStatus () {
        return attendStatus;
    }

    public void setAttendStatus (String attendStatus) {
        this.attendStatus = attendStatus;
    }
}
