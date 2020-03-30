package gov.nysenate.openleg.client.view.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.SessionMember;

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
            this.sessionYear = sessionMember.getSessionYear().getYear();
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