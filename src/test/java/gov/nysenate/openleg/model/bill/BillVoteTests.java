package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BillVoteTests extends BaseTests
{
    @Autowired
    private MemberService memberService;

    @Test
    public void testBillVoteEquality() throws Exception {
        LocalDate date = LocalDate.now();
        BillId billId = new BillId("S1234", 2013);

        BillVote vote1 = new BillVote(billId, date, BillVoteType.FLOOR, 1);
        vote1.addMemberVote(BillVoteCode.AYE, memberService.getMemberByShortName("BALL", SessionYear.current(), Chamber.SENATE));
        vote1.addMemberVote(BillVoteCode.AYE, memberService.getMemberByShortName("LAVALLE", SessionYear.current(), Chamber.SENATE));
        vote1.addMemberVote(BillVoteCode.NAY, memberService.getMemberByShortName("LANZA", SessionYear.current(), Chamber.SENATE));

        BillVote vote2 = new BillVote(billId, date, BillVoteType.FLOOR, 1);
        vote2.addMemberVote(BillVoteCode.AYE, memberService.getMemberByShortName("BALL", SessionYear.current(), Chamber.SENATE));
        vote2.addMemberVote(BillVoteCode.AYE, memberService.getMemberByShortName("LAVALLE", SessionYear.current(), Chamber.SENATE));
        vote2.addMemberVote(BillVoteCode.NAY, memberService.getMemberByShortName("LANZA", SessionYear.current(), Chamber.SENATE));

        assertEquals(vote1, vote2);

        vote2.addMemberVote(BillVoteCode.NAY, memberService.getMemberByShortName("BRESLIN", SessionYear.current(), Chamber.SENATE));
        assertNotEquals(vote1, vote2);
    }
}