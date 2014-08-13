package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;

import java.util.Map;

public interface MemberDao
{
    /**
     * Retrieve member by id.
     *
     * @param id int
     * @param session SessionYear
     * @return Member
     */
    public Member getMemberById(int id, SessionYear session);

    /**
     * Retrieves map of session year -> Member for a given member id. The member references
     * will point to the same person but will represent
     *
     * @param id int
     * @return Map<Integer, Member>
     */
    public Map<SessionYear, Member> getMemberById(int id);

    /**
     * Retrieve a map of session year -> Member given the LBDC short name.
     *
     * @param lbdcShortName String
     * @param chamber Chamber
     * @return Map<Integer,Member>
     */
    public Map<SessionYear, Member> getMembersByShortName(String lbdcShortName, Chamber chamber);

    /**
     * Retrieve the Member instance via the LBDC shortName and the session year.
     *
     * @param lbdcShortName String
     * @param sessionYear SessionYear
     * @param chamber Chamber
     * @return Member
     */
    public Member getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber);

    public void updateMember(Member member);

    public void deleteMember(Member member);
}
