package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.service.entity.MemberService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BillVoteTests extends BaseTests
{
    @Autowired
    private MemberService memberService;

    @Test
    public void testBillVoteEquality() throws Exception {
        Date date = new Date();
        BillId billId = new BillId("S1234", 2013);

        BillVote vote1 = new BillVote(billId, date, BillVoteType.FLOOR, 1);
        vote1.addMemberVote(BillVoteCode.AYE, memberService.getMemberByLBDCName("BALL", 2013, Chamber.SENATE));
        vote1.addMemberVote(BillVoteCode.AYE, memberService.getMemberByLBDCName("LAVALLE", 2013, Chamber.SENATE));
        vote1.addMemberVote(BillVoteCode.NAY, memberService.getMemberByLBDCName("LANZA", 2013, Chamber.SENATE));

        BillVote vote2 = new BillVote(billId, date, BillVoteType.FLOOR, 1);
        vote2.addMemberVote(BillVoteCode.AYE, memberService.getMemberByLBDCName("BALL", 2013, Chamber.SENATE));
        vote2.addMemberVote(BillVoteCode.AYE, memberService.getMemberByLBDCName("LAVALLE", 2013, Chamber.SENATE));
        vote2.addMemberVote(BillVoteCode.NAY, memberService.getMemberByLBDCName("LANZA", 2013, Chamber.SENATE));

        assertEquals(vote1, vote2);

        vote2.addMemberVote(BillVoteCode.NAY, memberService.getMemberByLBDCName("BRESLIN", 2013, Chamber.SENATE));
        assertNotEquals(vote1, vote2);
    }
}
