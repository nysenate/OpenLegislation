package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;

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
    SessionMember getMemberById(int id, SessionYear session);

    /**
     * Retrieve a member by session member id
     * If the specified session member id points to an alternate alias,
     *  then the primary session member will be returned instead
     *
     * @param sessionMemberId
     * @return Member
     */
    SessionMember getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx;

    /**
     * Retrieves map of session year -> Member for a given member id.
     *
     * @param id int
     * @return Map<Integer, Member>
     */
    FullMember getMemberById(int id) throws MemberNotFoundEx;

    /**
     * Retrieve a map of session year -> Member given the LBDC short name.
     *
     * @param lbdcShortName String
     * @param chamber Chamber
     * @return Map<Integer,Member>
     */
    Map<SessionYear, SessionMember> getMembersByShortName(String lbdcShortName, Chamber chamber);

    /**
     * Retrieve the Member instance via the LBDC shortName and the session year.
     *
     * @param lbdcShortName String
     * @param sessionYear SessionYear
     * @param chamber Chamber
     * @return Member
     */
    SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear,
                                       Chamber chamber) throws MemberNotFoundEx;

    /**
     * Retrieve members from all years and both chambers.
     * @return
     */
    List<SessionMember> getAllSessionMembers(SortOrder sortOrder, LimitOffset limOff);

    /**
     * Using session member data, creates al full members.
     * @return
     */
    List<FullMember> getAllFullMembers();
}
