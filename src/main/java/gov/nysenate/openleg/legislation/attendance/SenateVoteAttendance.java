package gov.nysenate.openleg.legislation.attendance;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.bill.BillVoteType;
import gov.nysenate.openleg.legislation.member.SessionMember;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SenateVoteAttendance extends BaseLegislativeContent {
    private VoteId voteId;
    private List<SessionMember> remoteMembers;

    public SenateVoteAttendance(VoteId voteId, List<SessionMember> remoteMembers) {
        this.voteId = voteId;
        this.remoteMembers = remoteMembers;
    }

    public SenateVoteAttendance(SenateVoteAttendance other) {
        super(other);
        this.voteId = other.getVoteId();
        this.remoteMembers = other.getRemoteMembers();
    }

    public VoteId getVoteId() {
        return voteId;
    }

    public LocalDate getVoteDate() {
        return voteId.getVoteDate();
    }

    public int getSequenceNo() {
        return voteId.getSequenceNo();
    }

    public BillVoteType getVoteType() {
        return voteId.getVoteType();
    }

    public List<SessionMember> getRemoteMembers() {
        return remoteMembers;
    }

    protected void setRemoteMembers(Collection<SessionMember> members) {
        this.remoteMembers = new ArrayList<>(members);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SenateVoteAttendance that = (SenateVoteAttendance) o;
        return Objects.equals(voteId, that.voteId) && Objects.equals(remoteMembers, that.remoteMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), voteId, remoteMembers);
    }
}
