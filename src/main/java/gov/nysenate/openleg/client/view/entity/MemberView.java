package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.Person;
import gov.nysenate.openleg.model.entity.SessionMember;

public class MemberView implements ViewObject
{
    protected int memberId;
    protected String chamber;
    protected boolean incumbent;
    protected String fullName;
    protected String shortName;
    protected String imgName;
    private SessionMember relatedSessionMember;

    public MemberView(){}

    public MemberView(SessionMember sessionMember) {
        if (sessionMember != null && sessionMember.getMember() != null) {
            Member member = sessionMember.getMember();
            this.memberId = member.getMemberId();
            this.chamber = member.getChamber() == null ? "" : member.getChamber().name();
            this.incumbent = member.isIncumbent();
            this.fullName = member.getFullName();
            this.shortName = sessionMember.getLbdcShortName();
            // This is actually associated with a person, not a member.
            this.imgName = member.getImgName();
            this.relatedSessionMember = sessionMember;
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
        return relatedSessionMember.getSessionMemberId();
    }

    public int getSessionYear() {
        return relatedSessionMember.getSessionYear().getYear();
    }

    public int getDistrictCode() {
        return relatedSessionMember.getDistrictCode();
    }

    public boolean getAlternate() {
        return relatedSessionMember.isAlternate();
    }

    @Override
    public String getViewType() {
        return "member";
    }
}
