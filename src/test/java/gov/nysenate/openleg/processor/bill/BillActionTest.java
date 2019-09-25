package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.base.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(UnitTest.class)
public class BillActionTest
{
    // @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(BillActionTest.class);

    private static String actionsList1 =
        "01/28/09 referred to correction\n" +
        "03/17/09 reported referred to ways and means\n" +
        "04/28/09 reported\n" +
        "04/30/09 advanced to third reading cal.453\n" +
        "05/04/09 passed assembly\n" +
        "05/04/09 delivered to senate\n" +
        "05/04/09 REFERRED TO CODES\n" +
        "05/26/09 SUBSTITUTED FOR S4366\n" +
        "05/26/09 3RD READING CAL.391\n" +
        "06/02/09 recalled from senate\n" +
        "06/03/09 SUBSTITUTION RECONSIDERED\n" +
        "06/03/09 RECOMMITTED TO CODES\n" +
        "06/03/09 RETURNED TO ASSEMBLY\n" +
        "06/04/09 vote reconsidered - restored to third reading\n" +
        "06/04/09 amended on third reading 3664a\n" +
        "06/15/09 repassed assembly\n" +
        "06/16/09 returned to senate\n" +
        "06/16/09 COMMITTED TO RULES\n" +
        "07/17/09 SUBSTITUTED FOR S4366A\n" +
        "07/17/09 3RD READING CAL.391\n" +
        "07/16/09 RECOMMITTED TO RULES\n" +
        "01/06/10 DIED IN SENATE\n" +
        "01/06/10 RETURNED TO ASSEMBLY\n" +
        "01/06/10 ordered to third reading cal.276\n" +
        "01/19/10 committed to correction\n" +
        "01/26/10 amend and recommit to correction\n" +
        "01/26/10 print number 3664b";

    @Test
    public void billActionInequality() {
        List<BillAction> actions = BillActionParser.parseActionsList(new BillId("S3664B", 2013), actionsList1);

        // 2 different items
        assertNotEquals(actions.get(0), actions.get(1));

        // null
        assertNotEquals(actions.get(0), null);

        // bill ID
        List<BillAction> actionA = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 recalled from senate");
        List<BillAction> actionB = BillActionParser.parseActionsList(new BillId("S3665B", 2013), "06/02/09 recalled from senate");
        assertNotEquals(actionA.get(0), actionB.get(0));

        // date
        actionA = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 recalled from senate");
        actionB = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/10 recalled from senate");
        assertNotEquals(actionA.get(0), actionB.get(0));

        // session
        actionA = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 recalled from senate");
        actionB = BillActionParser.parseActionsList(new BillId("S3664B", 2011), "06/02/09 recalled from senate");
        assertNotEquals(actionA.get(0), actionB.get(0));

        // chamber
        actionA = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 RECALLED FROM SENATE");
        actionB = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 recalled from senate");
        assertNotEquals(actionA.get(0), actionB.get(0));
    }

    @Test
    public void billActionEquality() {
        List<BillAction> actionA = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 recalled from senate");
        List<BillAction> actionB = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/02/09 recalled from senate");

        // separately constructed
        assertEquals(actionA.get(0), actionB.get(0));

        // same object
        assertEquals(actionA.get(0), actionA.get(0));
        assertEquals(actionB.get(0), actionB.get(0));
    }

    @Test
    public void actionListSort() {
        List<BillAction> actions = BillActionParser.parseActionsList(new BillId("S3664B", 2013), actionsList1);

        //sort descending
        actions.sort(new BillAction.ByEventSequenceDesc());

        // ensures correct sequencing, sequence numbers
        for (int i = 0; i < actions.size(); ++i) {
            assertEquals(actions.size() - i, actions.get(i).getSequenceNo());
        }

        //sort ascending
        actions.sort(new BillAction.ByEventSequenceAsc());

        // ensures correct sequencing, sequence numbers
        for (int i = 0; i < actions.size(); ++i) {
            assertEquals(i + 1, actions.get(i).getSequenceNo());
        }
    }

}
