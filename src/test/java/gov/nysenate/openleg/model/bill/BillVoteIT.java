package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(IntegrationTest.class)
public class BillVoteIT extends BaseTests
{
    @Autowired
    private MemberService memberService;

    @Test
    public void testBillVoteEquality() {
        LocalDate date = LocalDate.now();
        BillId billId = new BillId("S1234", 2013);
        SessionYear sessionYear = SessionYear.of(2013);

        SessionMember ball = memberService.getSessionMemberByShortName("BALL", sessionYear, Chamber.SENATE);
        SessionMember lavalle = memberService.getSessionMemberByShortName("LAVALLE", sessionYear, Chamber.SENATE);
        SessionMember lanza = memberService.getSessionMemberByShortName("LANZA", sessionYear, Chamber.SENATE);
        SessionMember breslin = memberService.getSessionMemberByShortName("BRESLIN", sessionYear, Chamber.SENATE);

        BillVote vote1 = new BillVote(billId, date, BillVoteType.FLOOR, 1);
        vote1.addMemberVote(BillVoteCode.AYE, ball);
        vote1.addMemberVote(BillVoteCode.AYE, lavalle);
        vote1.addMemberVote(BillVoteCode.NAY, lanza);

        BillVote vote2 = new BillVote(billId, date, BillVoteType.FLOOR, 1);
        vote2.addMemberVote(BillVoteCode.AYE, ball);
        vote2.addMemberVote(BillVoteCode.AYE, lavalle);
        vote2.addMemberVote(BillVoteCode.NAY, lanza);

        assertEquals(vote1, vote2);

        vote2.addMemberVote(BillVoteCode.NAY, breslin);
        assertNotEquals(vote1, vote2);
    }
}