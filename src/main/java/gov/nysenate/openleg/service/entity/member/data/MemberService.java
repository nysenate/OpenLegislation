package gov.nysenate.openleg.service.entity.member.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
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
     * @throws gov.nysenate.openleg.model.entity.MemberNotFoundEx If no matching member was found.
     */
    public Member getMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx;

    /**
     * Retrieve a member by session member id
     * If the specified session member id points to an alternate alias,
     *  then the primary session member will be returned instead
     *
     * @param sessionMemberId
     * @return Member
     * @throws MemberNotFoundEx if no session member exists with sessionMemberId
     */
    public Member getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx;

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
    public Member getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx;

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
    public Member getMemberByShortNameEnsured(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws ParseError;

    /**
     * Retrieves all members from all years and both chambers.
     * Useful for rebuilding the search index.
     * @return
     */
    public List<Member> getAllMembers(SortOrder sortOrder, LimitOffset limOff);
}