package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.entity.Member;

import java.io.Serializable;
import java.util.Objects;

public class AgendaVoteAttendance implements Serializable, Comparable<AgendaVoteAttendance>
{
    private static final long serialVersionUID = -4328021920936602603L;

    /** Reference to the member in the attendance list. */
    private Member member;

    /** The order in which this member is listed. */
    private Integer rank;

    /** Indicates the political party of the member. */
    private String party;

    /** Indicates the attendance status (e.g. Present) */
    private String attendance;

    /** --- Constructors --- */

    public AgendaVoteAttendance() {}

    public AgendaVoteAttendance(Member member, int rank, String party, String attendance) {
        this();
        this.setMember(member);
        this.setRank(rank);
        this.setParty(party);
        this.setAttendance(attendance);
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
               Objects.equals(this.attendance, other.attendance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, rank, party, attendance);
    }

    @Override
    public int compareTo(AgendaVoteAttendance o) {
        return this.getRank().compareTo(o.getRank());
    }

    /** --- Basic Getters/Setters --- */

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
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

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
