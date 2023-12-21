package gov.nysenate.openleg.api.legislation.member.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.SessionMember;

public class SessionMemberView implements ViewObject
{
    protected int sessionMemberId;
    private Member member;
    protected String shortName;
    protected int sessionYear;
    protected Integer districtCode;
    protected boolean alternate;

    protected SessionMemberView(){}

    public SessionMemberView(SessionMember sessionMember) {
        if (sessionMember != null) {
            this.sessionMemberId = sessionMember.getSessionMemberId();
            this.member = sessionMember.getMember();
            this.shortName = sessionMember.getLbdcShortName();
            this.sessionYear = sessionMember.getSessionYear().year();
            this.districtCode = sessionMember.getDistrictCode();
            this.alternate = sessionMember.isAlternate();
        }
    }

    @JsonIgnore
    public SessionMember toSessionMember() {
        SessionMember ret = new SessionMember();
        ret.setSessionMemberId(this.sessionMemberId);
        ret.setMember(this.member);
        ret.setLbdcShortName(this.shortName);
        ret.setSessionYear(SessionYear.of(this.sessionYear));
        ret.setDistrictCode(this.districtCode);
        ret.setAlternate(this.alternate);
        return ret;
    }

    public int getSessionMemberId() {
        return sessionMemberId;
    }

    public int getMemberId() {
        return member.getMemberId();
    }

    public String getShortName() {
        return shortName;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public boolean isAlternate() {
        return alternate;
    }

    @Override
    public String getViewType() {
        return "member-session";
    }
}