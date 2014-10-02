package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.Member;

public class SimpleMemberView implements ViewObject
{
    protected int memberId;
    protected String shortName;

    public SimpleMemberView(Member member) {
        if (member != null) {
            this.memberId = member.getMemberId();
            this.shortName = member.getLbdcShortName();
        }
    }

    public int getMemberId() {
        return memberId;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String getViewType() {
        return "member-simple";
    }
}
