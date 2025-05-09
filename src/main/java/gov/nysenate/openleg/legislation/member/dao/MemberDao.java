package gov.nysenate.openleg.legislation.member.dao;

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
     * Retrieve members from all years and both chambers.
     * @return
     */
    List<SessionMember> getAllSessionMembers();

    /**
     * Using session member data, creates al full members.
     * @return
     */
    List<FullMember> getAllFullMembers();

    Person getPerson(int personId) throws MemberNotFoundEx;

    Member getMember(int memberId) throws MemberNotFoundEx;

    SessionMember getSessionMember(int sessionMemberId) throws MemberNotFoundEx;
}
