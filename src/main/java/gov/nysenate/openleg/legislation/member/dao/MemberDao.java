package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.Person;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberDao {
    int handlePersonChange(MemberChangeType dataType, Person person);

    int handleMemberChange(MemberChangeType dataType, Member member);

    int handleSessionMemberChange(MemberChangeType dataType, SessionMember sessionMember);

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

    Person getPerson(int personId) throws MemberNotFoundEx;
    Member getMember(int memberId) throws MemberNotFoundEx;
}
