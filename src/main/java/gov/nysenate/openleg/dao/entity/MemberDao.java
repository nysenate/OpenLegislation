package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.model.entity.Member;

public interface MemberDao
{
    public Member getMemberByLBDCShortName(String lbdcShortName);

    public Member getMemberByLBDCShortName(String lbdcShortName, int sessionYear);

    public void updateMember(Member member);

    public void deleteMember(Member member);
}
