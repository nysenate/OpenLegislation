package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.SessionMember;

public class SimpleMemberView implements ViewObject
{
    protected int memberId;
    protected int sessionMemberId;
    protected String shortName;
    protected int sessionYear;
    protected String chamber;
    protected Integer districtCode;
    protected boolean alternate;

    protected SimpleMemberView(){}

    public SimpleMemberView(SessionMember member) {
        if (member != null) {
            this.memberId = member.getMemberId();
            this.sessionMemberId = member.getSessionMemberId();
            this.shortName = member.getLbdcShortName();
            this.sessionYear = member.getSessionYear().getYear();
            this.chamber = (member.getChamber() != null) ? member.getChamber().name() : null;
            this.districtCode = member.getDistrictCode();
            this.alternate = member.isAlternate();
        }
    }

    public int getSessionMemberId() {
        return sessionMemberId;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getShortName() {
        return shortName;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public String getChamber() {
        return chamber;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public boolean isAlternate() {
        return alternate;
    }

    @Override
    public String getViewType() {
        return "member-simple";
    }
}