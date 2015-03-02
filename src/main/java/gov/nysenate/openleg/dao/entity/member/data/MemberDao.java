package gov.nysenate.openleg.dao.entity.member.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;

import java.util.List;
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
     * Retrieve a member by session member id
     * If the specified session member id points to an alternate alias,
     *  then the primary session member will be returned instead
     *
     * @param sessionMemberId
     * @return Member
     */
    public Member getMemberBySessionId(int sessionMemberId);

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

    public void insertUnverifiedSessionMember(Member member);

    /**
     * Retrieve members from all years and both chambers.
     * @return
     */
    public List<Member> getAllMembers(SortOrder sortOrder, LimitOffset limOff);
}
