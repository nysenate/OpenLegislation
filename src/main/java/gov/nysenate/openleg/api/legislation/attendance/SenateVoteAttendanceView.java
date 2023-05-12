package gov.nysenate.openleg.api.legislation.attendance;

import gov.nysenate.openleg.api.legislation.member.view.SessionMemberView;
import gov.nysenate.openleg.legislation.attendance.SenateVoteAttendance;

import java.util.List;
import java.util.stream.Collectors;

public class SenateVoteAttendanceView {

    private List<SessionMemberView> remote;

    public SenateVoteAttendanceView(SenateVoteAttendance attendance) {
        this.remote = attendance.getRemoteMembers().stream()
                .map(SessionMemberView::new)
                .collect(Collectors.toList());
    }

    public List<SessionMemberView> getRemote() {
        return remote;
    }
}
