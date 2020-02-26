package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.processor.base.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

@Category(UnitTest.class)
public class BillActionTest
{
    // @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(BillActionTest.class);

    /** Date format found in SobiBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

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

    private final BillAction actionA = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                                              "recalled from senate",
                                              Chamber.ASSEMBLY,
                                              1,
                                              new BillId("S3664B", 2013));
    private final BillAction actionB = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                                              "recalled from senate",
                                              Chamber.ASSEMBLY,
                                              1,
                                              new BillId("S3665B", 2013));// different
    private final BillAction actionC = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/10")),// different
                                              "recalled from senate",
                                              Chamber.ASSEMBLY,
                                              1,
                                              new BillId("S3664B", 2013));
    private final BillAction actionD = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                                              "recalled from senate",
                                              Chamber.ASSEMBLY,
                                              1,
                                              new BillId("S3664B", 2011));// different
    private final BillAction actionE = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                                              "RECALLED FROM SENATE",// different
                                              Chamber.SENATE,// different
                                              1,
                                              new BillId("S3664B", 2013));
    private final BillAction actionF = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                                              "recalled from senate",
                                              Chamber.ASSEMBLY,
                                              2,// different
                                              new BillId("S3664B", 2013));
    @Test
    public void billActionInequality() {
        // null
        assertNotEquals(actionA, null);

        // bill ID
        assertNotEquals(actionA, actionB);

        // date
        assertNotEquals(actionA, actionC);

        // session
        assertNotEquals(actionA, actionD);

        // chamber
        assertNotEquals(actionA, actionE);

        // sequence number
        assertNotEquals(actionA, actionF);
    }

    @Test
    public void billActionEquality() {
        final BillAction action1 = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                "recalled from senate",
                Chamber.ASSEMBLY,
                1,
                new BillId("S3664B", 2013));
        final BillAction action2 = new BillAction(LocalDate.from(eventDateFormat.parse("06/02/09")),
                "recalled from senate",
                Chamber.ASSEMBLY,
                1,
                new BillId("S3664B", 2013));

        // separately constructed
        assertEquals(action1, action2);

        // same object
        assertEquals(action1, action1);
        assertEquals(action2, action2);
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

    @Test
    public void getSet() {
        BillAction a = new BillAction();
        a.setDate(LocalDate.from(eventDateFormat.parse("01/02/19")));
        a.setText("abcdefg");
        a.setBillId(new BillId("S3664B", 2013));
        a.setChamber(Chamber.ASSEMBLY);
        a.setSequenceNo(1);
        assertEquals("2019-01-02 (ASSEMBLY) abcdefg", a.toString());
    }

    /*
    @Test
    public void xmlGetSet() {
        BillAction a = new BillAction();
        a.setDate(LocalDate.from(eventDateFormat.parse("01/02/19")));
        a.setText("abcdefg");
        a.setBillId(new BillId("S3664B", 2013));
        a.setChamber(Chamber.ASSEMBLY);
        a.setSequenceNo(1);
        assertEquals("2019-01-02 (ASSEMBLY) abcdefg", a.toString());

        a.setBillAmd("A");
        assertEquals("A", a.getBillAmd());
        a.setCode(23);
        assertEquals(23, a.getCode());
        a.setData(87);
        assertEquals(87, a.getData());
        a.setDataAmd("V");
        assertEquals("V", a.getDataAmd());
        a.setPostDate(LocalDate.from(eventDateFormat.parse("01/30/19")));
        assertEquals(LocalDate.from(eventDateFormat.parse("01/30/19")), a.getPostDate());
        a.setActSessionYear(2018);
        assertEquals(2018, a.getActSessionYear());
        a.setFromXML(true);
        assertTrue(a.getFromXML());
    }

    @Test
    public void xmlConstructorAndEquals() {
        BillAction a = new BillAction(LocalDate.from(eventDateFormat.parse("01/02/19")),
                "abcdefg",
                Chamber.ASSEMBLY,
                1,
                "A",
                23,
                87,
                "V",
                LocalDate.from(eventDateFormat.parse("01/30/19")),
                2018,
                true,
                new BillId("S3664B", 2013));
        BillAction b = new BillAction(LocalDate.from(eventDateFormat.parse("01/02/19")),
                "ABCDEFG",//capitalization is different
                Chamber.ASSEMBLY,
                1,
                "A",
                23,
                87,
                "V",
                LocalDate.from(eventDateFormat.parse("01/30/19")),
                2018,
                true,
                new BillId("S3664B", 2013));
        BillAction c = new BillAction(LocalDate.from(eventDateFormat.parse("01/02/19")),
                "ABCDEFG",//capitalization is different
                Chamber.SENATE,
                1,
                "A",
                23,
                87,
                "V",
                LocalDate.from(eventDateFormat.parse("01/30/19")),
                2018,
                true,
                new BillId("S3664B", 2013));

        assertEquals("2019-01-02 (ASSEMBLY) abcdefg", a.toString());

        assertEquals("A", a.getBillAmd());
        assertEquals(23, a.getCode());
        assertEquals(87, a.getData());
        assertEquals("V", a.getDataAmd());
        assertEquals(LocalDate.from(eventDateFormat.parse("01/30/19")), a.getPostDate());
        assertEquals(2018, a.getActSessionYear());
        assertTrue(a.getFromXML());

        // equality tests
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
        assertNotEquals(b, c);
        assertNotEquals(b.hashCode(), c.hashCode());
        assertEquals(c, c);
    }*/

    @Test
    public void compareTo() {
        BillAction a = new BillAction();
        BillAction b = new BillAction();
        BillAction c = new BillAction();
        a.setSequenceNo(1);
        b.setSequenceNo(2);
        c.setSequenceNo(1);
        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
        assertEquals(0, c.compareTo(c));
    }
}
