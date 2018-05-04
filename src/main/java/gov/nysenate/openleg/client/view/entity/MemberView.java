package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.model.entity.SessionMember;

public class MemberView extends SimpleMemberView
{
    protected String fullName;
    protected String imgName;

    public MemberView(){}

    public MemberView(SessionMember member) {
        super(member);
        if (member != null) {
            this.fullName = member.getFullName();
            this.imgName = member.getImgName();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getImgName() {
        return imgName;
    }

    @Override
    public String getViewType() {
        return "member";
    }
}