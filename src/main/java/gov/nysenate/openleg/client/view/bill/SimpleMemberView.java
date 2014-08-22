package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.model.entity.Member;

public class SimpleMemberView
{
    protected String shortName;

    public SimpleMemberView(Member member) {
        if (member != null) {
            this.shortName = member.getLbdcShortName();
        }
    }

    public String getShortName() {
        return shortName;
    }
}
