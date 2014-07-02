package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;

import java.util.Map;

public interface MemberDao
{
    /**
     * Retrieve a map of session year -> Member given the LBDC short name.
     * @param lbdcShortName String
     * @param chamber Chamber
     * @return Map<Integer,Member>
     */
    public Map<Integer, Member> getMembersByLBDCShortName(String lbdcShortName, Chamber chamber);

    /**
     * Retrieve the Member instance via the LBDC shortName and the session year.
     * @param lbdcShortName String
     * @param sessionYear int
     * @param chamber Chamber
     * @return Member
     */
    public Member getMemberByLBDCShortName(String lbdcShortName, int sessionYear, Chamber chamber);

    public void updateMember(Member member);

    public void deleteMember(Member member);
}
