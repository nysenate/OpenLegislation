package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.model.entity.Member;

public class MemberView extends SimpleMemberView {

    protected String chamber;
    protected boolean incumbent;
    protected int districtCode;

    public MemberView(Member member) {
        super(member);
        if (member != null) {
            this.chamber = member.getChamber().name();
            this.incumbent = member.isIncumbent();
            this.districtCode = member.getDistrictCode();
        }
    }


    public String getChamber() {
        return chamber;
    }

    public boolean isIncumbent() {
        return incumbent;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    @Override
    public String getViewType() {
        return "member";
    }
}
