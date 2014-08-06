package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;

import java.util.Map;

public interface MemberDao
{
    /**
     * Retrieve member by id.
     * @param id int
     * @param session int
     * @return Member
     */
    public Member getMemberById(int id, int session);

    /**
     * Retrieves map of session year -> Member for a given member id. The member references
     * will point to the same person but will represent
     *
     * @param id int
     * @return Map<Integer, Member>
     */
    public Map<Integer, Member> getMemberById(int id);

    /**
     * Retrieve a map of session year -> Member given the LBDC short name.
     *
     * @param lbdcShortName String
     * @param chamber Chamber
     * @return Map<Integer,Member>
     */
    public Map<Integer, Member> getMembersByLBDCName(String lbdcShortName, Chamber chamber);

    /**
     * Retrieve the Member instance via the LBDC shortName and the session year.
     *
     * @param lbdcShortName String
     * @param sessionYear int
     * @param chamber Chamber
     * @return Member
     */
    public Member getMemberByLBDCName(String lbdcShortName, int sessionYear, Chamber chamber);

    public void updateMember(Member member);

    public void deleteMember(Member member);
}
