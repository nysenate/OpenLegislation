package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;

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
}