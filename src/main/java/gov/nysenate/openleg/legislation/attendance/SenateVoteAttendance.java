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
    private List<SessionMember> remoteMembers;

    public SenateVoteAttendance() {
        this(new ArrayList<>());
    }

    public SenateVoteAttendance(List<SessionMember> remoteMembers) {
        this.remoteMembers = remoteMembers;
    }

    public List<SessionMember> getRemoteMembers() {
        return remoteMembers;
    }

    public void addRemoteMember(SessionMember member) {
        this.remoteMembers.add(member);
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
        return Objects.equals(remoteMembers, that.remoteMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteMembers);
    }
}
