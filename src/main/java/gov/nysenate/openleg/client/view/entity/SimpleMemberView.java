package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.Member;

public class SimpleMemberView implements ViewObject
{
    protected int memberId;
    protected String shortName;
    protected int sessionYear;
    protected String chamber;

    public SimpleMemberView(Member member) {
        if (member != null) {
            this.memberId = member.getMemberId();
            this.shortName = member.getLbdcShortName();
            this.sessionYear = member.getSessionYear().getYear();
            this.chamber = (member.getChamber() != null) ? member.getChamber().name() : null;
        }
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

    @Override
    public String getViewType() {
        return "member-simple";
    }
}