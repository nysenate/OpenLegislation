package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.model.agenda.AgendaVoteAttendance;

public class AgendaAttendanceView implements ViewObject
{
    private MemberView member;
    private int rank;
    private String party;
    private String attend;

    public AgendaAttendanceView(AgendaVoteAttendance attendance) {
        if (attendance != null) {
            this.member = new MemberView(attendance.getMember());
            this.rank = attendance.getRank();
            this.party = attendance.getParty();
            this.attend = attendance.getAttendStatus();
        }
    }

    public MemberView getMember() {
        return member;
    }

    public int getRank() {
        return rank;
    }

    public String getParty() {
        return party;
    }

    public String getAttend() {
        return attend;
    }

    @Override
    public String getViewType() {
        return "agenda-attendance";
    }
}
