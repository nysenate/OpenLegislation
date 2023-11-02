package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class XmlSenFlVotProcessorIT extends BaseXmlProcessorTest {

    @Autowired private BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(XmlSenFlVotProcessor.class);

    @Test
    public void processedCorrectlytest() {
        //Get and Process Sample Floor Votes
        processXmlFile("processor/bill/senFlVot/2017-10-23-10.25.46.989009_SENFLVOT_S00100.XML");
        //Get sample floor votes for this bill
        Bill baseBill = billDataService.getBill(new BaseBillId("S100", 2017));
        BillAmendment amendment = baseBill.getAmendment(Version.ORIGINAL);
        Map<BillVoteId, BillVote> s100VotesMap = amendment.getVotesMap();
        //Check that votes made it into the votes map
        assertNotNull(s100VotesMap);

        //Create expected BillVoteID
        LocalDate voteDate = LocalDate.of(2017, 10, 23);
        BillVoteId expectedBillVoteId = new BillVote(amendment.getBillId(), voteDate ,
                BillVoteType.FLOOR, 0).getVoteId();
        //Verify correct id was placed
        assertTrue(s100VotesMap.containsKey(expectedBillVoteId));

        BillVote s100Votes = s100VotesMap.get(expectedBillVoteId);
        assertTrue(s100Votes.getMemberVotes().size() > 0 );
        assertTrue(s100Votes.getVoteCounts().get(BillVoteCode.AYE) == 61 );
    }

    @Test
    public void processRemoteVoteInfo() {
        //Get and Process Sample Floor Votes
        processXmlFile("processor/bill/senFlVot/2023-10-24-09.19.43.232048_SENFLVOT_S01234.XML");
        //Get sample floor votes for this bill
        Bill baseBill = billDataService.getBill(new BaseBillId("S1234", 2023));
        BillAmendment amendment = baseBill.getAmendment(Version.ORIGINAL);

        Map<BillVoteId, BillVote> voteMap = amendment.getVotesMap();
        //Check that votes made it into the votes map
        assertNotNull(voteMap);

        //Create expected BillVoteID
        LocalDate voteDate = LocalDate.of(2023, 6, 12);
        BillVoteId expectedBillVoteId = new BillVote(amendment.getBillId(), voteDate, BillVoteType.FLOOR, 1).getVoteId();
        //Verify correct id was placed
        assertTrue(voteMap.containsKey(expectedBillVoteId));

        BillVote actualVote = voteMap.get(expectedBillVoteId);
        assertTrue(actualVote.getMemberVotes().size() > 0 );
        assertTrue(actualVote.getVoteCounts().get(BillVoteCode.AYE) == 61);
        assertTrue(actualVote.getVoteCounts().get(BillVoteCode.NAY) == 2);
        assertTrue(actualVote.getAttendance().getRemoteMembers().size() == 4);

        Set<String> expectedRemoteShortNames = new HashSet<>(Arrays.asList("ASHBY", "COONEY", "MURRAY", "RYAN"));
        Set<String> actualRemoteShortNames = actualVote.getAttendance().getRemoteMembers().stream()
                .map(SessionMember::getLbdcShortName)
                .collect(Collectors.toSet());

        assertEquals(expectedRemoteShortNames, actualRemoteShortNames);
    }
}
