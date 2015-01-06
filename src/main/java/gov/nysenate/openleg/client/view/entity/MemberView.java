package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.model.entity.Member;

public class MemberView extends SimpleMemberView
{
    protected String fullName;
    protected Integer districtCode;

    public MemberView(Member member) {
        super(member);
        if (member != null) {
            this.fullName = member.getFullName();
            this.districtCode = member.getDistrictCode();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    @Override
    public String getViewType() {
        return "member";
    }
}