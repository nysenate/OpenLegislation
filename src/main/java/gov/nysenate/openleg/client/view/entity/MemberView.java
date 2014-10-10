package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.model.entity.Member;

public class MemberView extends SimpleMemberView {

    protected String chamber;
    protected int districtCode;

    public MemberView(Member member) {
        super(member);
        if (member != null) {
            this.chamber = member.getChamber().name();
            this.districtCode = member.getDistrictCode();
        }
    }

    public String getChamber() {
        return chamber;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    @Override
    public String getViewType() {
        return "member";
    }
}
