package gov.nysenate.openleg.service.entity.member.data;

import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.processor.base.ParseError;

import java.util.List;

public interface MemberService
{
    /**
     * Retrieves Member using a unique member id and session year.
     *
     * @param memberId int
     * @param sessionYear SessionYear
     * @return Member
     * @throws MemberNotFoundEx If no matching member was found.
     */
    public SessionMember getMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx;

    /**
     * Retrieves map of session year -> Member for a given member id.
     *
     * @param id int
     * @return Map<Integer, Member>
     */
    public TreeMultimap<SessionYear, SessionMember> getMemberById(int id) throws MemberNotFoundEx;

    /**
     * Retrieve a member by session member id
     * If the specified session member id points to an alternate alias,
     *  then the primary session member will be returned instead
     *
     * @param sessionMemberId
     * @return Member
     * @throws MemberNotFoundEx if no session member exists with sessionMemberId
     */
    public SessionMember getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx;

    /**
     * Retrieve Member (which can represent either a senator or assemblymember) using the LBDC shortname,
     * the session year, and the chamber (Senate/Assembly).
     *
     * @param lbdcShortName String - The short name of the member as represented in the source data.
     * @param sessionYear SessionYear - The session year in which this member was active.
     * @param chamber Chamber
     * @return Member
     * @throws MemberNotFoundEx If no matching member was found.
     */
    public SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx;

    /**
     * This functions in the same way as {@link #getMemberByShortName(String, gov.nysenate.openleg.model.base.SessionYear, gov.nysenate.openleg.model.entity.Chamber)}
     * with the exception that, instead of throwing an exception when a member is not found,
     * this method creates a new Member in storage and returns that
     * This should only be used in the processor layer
     *
     * @param lbdcShortName String - The short name of the member as represented in the source data.
     * @param sessionYear SessionYear - The session year in which this member was active.
     * @param chamber Chamber
     * @return Member
     * @throws ParseError - if the provided short name does not match specification
     */
    public SessionMember getMemberByShortNameEnsured(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws ParseError;

    /**
     * Retrieves all members from all years and both chambers.
     * Useful for rebuilding the search index.
     * @return
     */
    public List<SessionMember> getAllMembers(SortOrder sortOrder, LimitOffset limOff);

    /**
     * @return List<FullMember> - a list of all members containing all linked session members
     */
    public List<FullMember> getAllFullMembers();

    /**
     * Adds the given members to the data store
     * This method should only be used for administrative purposes
     *  because it will trigger cache and search index rebuilds
     * @param members List<Member>
     */
    public void updateMembers(List<SessionMember> members);
}