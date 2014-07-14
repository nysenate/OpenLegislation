package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;

public interface MemberService
{
    /**
     * Retrieves Member using a unique member id and session year.
     * @param memberId int
     * @param sessionYear int
     * @return Member
     * @throws MemberNotFoundEx If no matching member was found.
     */
    public Member getMemberById(int memberId, int sessionYear) throws MemberNotFoundEx;

    /**
     * Retrieve Member (which can represent either a senator or assemblymember) using the LBDC shortname,
     * the session year, and the chamber (Senate/Assembly).
     * @param lbdcShortName String
     * @param sessionYear int
     * @param chamber Chamber
     * @return Member
     * @throws MemberNotFoundEx If no matching member was found.
     */
    public Member getMemberByLBDCName(String lbdcShortName, int sessionYear, Chamber chamber) throws MemberNotFoundEx;
}
