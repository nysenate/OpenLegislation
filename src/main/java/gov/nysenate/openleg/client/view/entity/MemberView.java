package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.model.entity.SessionMember;

public class MemberView extends SimpleMemberView
{
    protected String fullName;
    protected Integer districtCode;
    protected String imgName;

    public MemberView(){}

    public MemberView(SessionMember member) {
        super(member);
        if (member != null) {
            this.fullName = member.getFullName();
            this.districtCode = member.getDistrictCode();
            this.imgName = member.getImgName();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public String getImgName() {
        return imgName;
    }

    @Override
    public String getViewType() {
        return "member";
    }
}