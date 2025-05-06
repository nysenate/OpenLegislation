package gov.nysenate.openleg.api.legislation.member.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.member.Member;

public class PartialMemberView implements ViewObject {
    protected int memberId;
    protected String chamber;
    protected boolean incumbent;
    protected String fullName;
    protected String imgName;

    public PartialMemberView(Member member) {
        if (member == null) {
            return;
        }
        this.memberId = member.getMemberId();
        this.chamber = member.getChamber() == null ? "" : member.getChamber().name();
        this.incumbent = member.isIncumbent();
        this.fullName = member.getPerson().name().fullName();
        // This is actually associated with a person, not a member.
        this.imgName = member.getPerson().imgName();
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

    public String getImgName() {
        return imgName;
    }

    @Override
    public String getViewType() {
        return "partial-member";
    }
}
