package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Transactional
public class XmlSenFlVotProcessorTest extends BaseXmlProcessorTest {
    @Autowired
    BillDao billDao;
    @Autowired
    XmlSenFlVotProcessor xmlSenFlVotProcessor;

    private static final Logger logger = LoggerFactory.getLogger(XmlSenFlVotProcessor.class);

    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    protected SobiProcessor getSobiProcessor() {
        return xmlSenFlVotProcessor;
    }

    @Test
    public void processedCorrectlytest() {
        //Get and Process Sample Floor Votes
//        final File testFileDir = new File(
//                getClass().getClassLoader().getResource("test/resources/processor/bill/senFlVot/2017-10-23-10.25.46.989009_SENFLVOT_S00100").getFile());

        processXmlFile("processor/bill/senFlVot/2017-10-23-10.25.46.989009_SENFLVOT_S00100.XML");
        //Get sample floor votes for this bill
        Bill baseBill = billDao.getBill(new BillId("S100", 2017));
        BillAmendment s100 = baseBill.getAmendment(Version.DEFAULT);
        Map<BillVoteId, BillVote> s100VotesMap = s100.getVotesMap();
        //Check that votes made it into the votes map
        assertNotNull(s100VotesMap);

        //Create expected BillVoteID
        LocalDate voteDate = LocalDate.from(voteDateFormat.parse("10/23/2017"));
        BillVoteId expectedBillVoteId = new BillVote(baseBill.getBaseBillId(), voteDate ,
                BillVoteType.FLOOR, 5).getVoteId();
        //Verify correct id was placed
        assertTrue(s100VotesMap.containsKey(expectedBillVoteId));

        BillVote s100Votes = s100VotesMap.get(expectedBillVoteId);
        assertTrue(s100Votes.getMemberVotes().size() > 0 );
        assertTrue(s100Votes.getVoteCounts().get(BillVoteCode.AYE) == 61 );
    }
}
