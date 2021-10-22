package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteAttendance;

public record AgendaAttendanceView(MemberView member, int rank, String party, String attend)
        implements ViewObject {

    public AgendaAttendanceView(AgendaVoteAttendance attendance) {
        this(new MemberView(attendance.getMember()), attendance.getRank(),
                attendance.getParty(), attendance.getAttendStatus());
    }

    @Override
    public String getViewType() {
        return "agenda-attendance";
    }
}
