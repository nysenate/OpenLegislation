package gov.nysenate.openleg.api.legislation.member.view;

import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.SessionMember;

public class MemberView extends PartialMemberView {
    // TODO: bad separation of concerns to have this here
    protected int sessionMemberId;
    protected int sessionYear;
    protected int districtCode;
    protected boolean alternate;
    protected String shortName;

    public MemberView(Member member) {
        super(member);
    }

    public MemberView(SessionMember sessionMember) {
        this(sessionMember == null ? null : sessionMember.getMember());
        if (sessionMember == null) {
            return;
        }
        this.sessionMemberId = sessionMember.getSessionMemberId();
        this.sessionYear = sessionMember.getSessionYear().year();
        this.districtCode = sessionMember.getDistrictCode();
        this.alternate = sessionMember.isAlternate();
        this.shortName = sessionMember.getLbdcShortName();
    }

    public String getShortName() {
        return shortName;
    }

    public int getSessionMemberId() {
        return sessionMemberId;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    public boolean isAlternate() {
        return alternate;
    }

    @Override
    public String getViewType() {
        return "member";
    }
}
