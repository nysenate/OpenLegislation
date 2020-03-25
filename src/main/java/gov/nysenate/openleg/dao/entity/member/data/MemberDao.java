package gov.nysenate.openleg.dao.entity.member.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;

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
    SessionMember getMemberBySessionId(int sessionMemberId);

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
    SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber);

    /**
     * Retrieve members from all years and both chambers.
     * @return
     */
    List<SessionMember> getAllMembers(SortOrder sortOrder, LimitOffset limOff);

    /**
     * @return List<Member> - a list of members that were created on the fly during processing and have not yet been verified
     */
    List<SessionMember> getUnverifiedSessionMembers();

    /**
     * Updates or inserts a person into the data store
     * Sets the personId field of the given person using the newly generated id
     * @param person Person
     *
     */
    void updatePerson(Person person);

    /**
     * Updates or inserts a member into the data store
     * Sets the memberId field of the given member using the newly generated id
     * @param member Member
     */
    void updateMember(Member member);

    /**
     * Updates or inserts a session member into the data store
     * Sets the sessionMemberId field of the given member using the newly generated id
     * @param sessionMember sessionMember
     */
    void updateSessionMember(SessionMember sessionMember);


    /**
     * Links a member to a person in the data store
     * @param memberId int - member id
     * @param personId int - person id
     */
    void linkMember(int memberId, int personId);

    /**
     * Links a session member to a member in the data store
     * @param sessionMemberId int - session member id
     * @param memberId int - member id
     */
    void linkSessionMember(int sessionMemberId, int memberId);

    /**
     * Removes all persons and members that do not have an associated session member
     * These are typically persons/members/session members that were created on the fly during processing
     *      where the session
     */
    void clearOrphans();
}
