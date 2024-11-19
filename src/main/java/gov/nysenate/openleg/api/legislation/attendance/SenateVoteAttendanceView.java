package gov.nysenate.openleg.api.legislation.attendance;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.attendance.SenateVoteAttendance;

import java.util.List;
import java.util.stream.Collectors;

public class SenateVoteAttendanceView {
    private ListView<MemberView> remote;

    public SenateVoteAttendanceView() {}

    public SenateVoteAttendanceView(SenateVoteAttendance attendance) {
        List<MemberView> remoteMembers = attendance.getRemoteMembers().stream()
                .map(MemberView::new)
                .collect(Collectors.toList());
        this.remote = ListView.of(remoteMembers);
    }

    public ListView<MemberView> getRemote() {
        return remote;
    }
}
