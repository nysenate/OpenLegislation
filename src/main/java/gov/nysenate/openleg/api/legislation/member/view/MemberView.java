package gov.nysenate.openleg.api.legislation.member.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.SessionMember;

public class MemberView implements ViewObject {
    protected int memberId;
    protected String chamber;
    protected boolean incumbent;
    protected String fullName;
    protected String shortName;
    protected String imgName;
    // TODO: bad separation of concerns to have this here
    protected int sessionMemberId;
    protected int sessionYear;
    protected int districtCode;
    protected boolean alternate;

    public MemberView(){}

    public MemberView(SessionMember sessionMember) {
        if (sessionMember != null && sessionMember.getMember() != null) {
            Member member = sessionMember.getMember();
            this.memberId = member.getMemberId();
            this.chamber = member.getChamber() == null ? "" : member.getChamber().name();
            this.incumbent = member.isIncumbent();
            this.fullName = member.getPerson().name().fullName();
            this.shortName = sessionMember.getLbdcShortName();
            // This is actually associated with a person, not a member.
            this.imgName = member.getPerson().imgName();
            this.sessionMemberId = sessionMember.getSessionMemberId();
            this.sessionYear = sessionMember.getSessionYear().year();
            this.districtCode = sessionMember.getDistrictCode();
            this.alternate = sessionMember.isAlternate();
        }
    }

    public int getMemberId() {
        return memberId;
    }

    public String getChamber() {
        return chamber;
    }

    public boolean isIncumbent() {
        return incumbent;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getImgName() {
        return imgName;
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
