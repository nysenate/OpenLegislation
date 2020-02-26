package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.util.XmlHelper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(UnitTest.class)
public class BillActionParserTest
{
    // @Autowired BillDataService billDataService;
    private static XmlHelper xmlHelper;

    static {
        try {
            xmlHelper = new XmlHelper();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void billActionParser() {
        List<BillAction> actions = BillActionParser.parseActionsList(new BillId("S3664B", 2013), actionsList1);

        // counts items
        assertEquals(actions.size(), actionsList1.split("\n").length);

        // first two items are different
        assertNotEquals(actions.get(0), actions.get(1));
    }

    @Test
    public void testBillActionParserXML() throws IOException, SAXException, XPathExpressionException {
        Node actionsNode = xmlHelper.getNode("actions", xmlHelper.parse(actionsXml1));
        List<BillAction> actions = BillActionParser.parseActionsListXML(new BillId("S3664B", 2013), actionsNode);

        // first two items are different
        assertNotEquals(actions.get(0), actions.get(1));

        // compare to the old method of parsing
        List<BillAction> actionsOldMethod = BillActionParser.parseActionsList(new BillId("S3664B", 2013), actionsList2);

        // counts items
        assertEquals(actionsOldMethod.size(), actions.size());

        // compares each list element
        assertEquals(actionsOldMethod, actions);
    }

    @Test (expected = ParseError.class)
    public void billActionParserXMLMissingData() throws IOException, SAXException, XPathExpressionException {
        Node actionsNode = xmlHelper.getNode("actions", xmlHelper.parse(actionsXml2));
        List<BillAction> actions = BillActionParser.parseActionsListXML(new BillId("S3664B", 2013), actionsNode);// throws here
    }

    @Test (expected = Exception.class)
    public void billActionParserXMLCorrupted() throws IOException, SAXException, XPathExpressionException {
        Node actionsNode = xmlHelper.getNode("actions", xmlHelper.parse(actionsXml3));// throws here
        List<BillAction> actions = BillActionParser.parseActionsListXML(new BillId("S3664B", 2013), actionsNode);
    }

    @Test
    public void sequenceNumber() {
        List<BillAction> actions = BillActionParser.parseActionsList(new BillId("S3664B", 2013), actionsList1);

        // ensures correct sequencing, sequence numbers
        for (int i = 0; i < actions.size(); ++i) {
            assertEquals(i + 1, actions.get(i).getSequenceNo());
        }
    }


    @Test (expected = ParseError.class)
    public void invalidDate() {
        List<BillAction> actionA = BillActionParser.parseActionsList(new BillId("S3664B", 2013), "06/A2/09 recalled from senate");
    }


    @Test
    public void printAction() {
        List<BillAction> actions = BillActionParser.parseActionsList(new BillId("S3664B", 2013), actionsList1);
        assertEquals("2010-01-26 (ASSEMBLY) PRINT NUMBER 3664B", actions.get(actions.size() - 1).toString());
    }

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

    private static String actionsList2 = "05/08/18 REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS\n" +
            "06/19/18 COMMITTEE DISCHARGED AND COMMITTED TO RULES\n" +
            "06/19/18 ORDERED TO THIRD READING CAL.2008\n" +
            "06/19/18 SUBSTITUTED BY A10768\n" +
            "01/03/18 referred to governmental operations";

    private static String actionsXml1 = "<actions>" +
            "<action no=\"1\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>1</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>29</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-05-08</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-05-08</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"2\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>9</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>27</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[COMMITTEE DISCHARGED AND COMMITTED TO RULES]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"3\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>17</actioncode>" +
            "<actiontype>F</actiontype>" +
            "<actiondata>2008</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[ORDERED TO THIRD READING CAL.2008]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"4\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>23</actioncode>" +
            "<actiontype>B</actiontype>" +
            "<actiondata>10768</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[SUBSTITUTED BY A10768 ]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"5\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>A</actionhouse>" +
            "<actioncode>1</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>38</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-01-03</actiondate>" +
            "<actiondesc><![CDATA[referred to governmental operations]]></actiondesc>" +
            "<actionpostdate>2018-01-03</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"2018\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>350</actioncode>" +
            "<actiontype>B</actiontype>" +
            "<actiondata>10768</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[sub-by]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "</actions>";

    // first action is missing all fields
    private static String actionsXml2 = "<actions>" +
            "<action no=\"1\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse></actionhouse>" +
            "<actioncode></actioncode>" +
            "<actiontype></actiontype>" +
            "<actiondata></actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate></actiondate>" +
            "<actiondesc>" +
            "<![CDATA[]]>" +
            "</actiondesc>" +
            "<actionpostdate></actionpostdate>" +
            "<actionactsessyr></actionactsessyr>" +
            "</action>" +
            "<action no=\"2\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>9</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>27</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[COMMITTEE DISCHARGED AND COMMITTED TO RULES]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"3\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>17</actioncode>" +
            "<actiontype>F</actiontype>" +
            "<actiondata>2008</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[ORDERED TO THIRD READING CAL.2008]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"4\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>23</actioncode>" +
            "<actiontype>B</actiontype>" +
            "<actiondata>10768</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[SUBSTITUTED BY A10768 ]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"5\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>A</actionhouse>" +
            "<actioncode>1</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>38</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-01-03</actiondate>" +
            "<actiondesc><![CDATA[referred to governmental operations]]></actiondesc>" +
            "<actionpostdate>2018-01-03</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"2018\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>350</actioncode>" +
            "<actiontype>B</actiontype>" +
            "<actiondata>10768</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[sub-by]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "</actions>";

    // random closing tags added/removed
    private static String actionsXml3 = "<actions>" +
            "<action no=\"1\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>1</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>29</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-05-08</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-05-08</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"2\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>9</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>27</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[COMMITTEE DISCHARGED AND COMMITTED TO RULES]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"3\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>17</actioncode>" +
            "<actiontype>F</actiontype>" +
            "<actiondata>2008</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[ORDERED TO THIRD READING CAL.2008]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"4\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>23</actioncode>" +
            "<actiontype>B</actiontype>" +
            "<actiondata>10768</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[SUBSTITUTED BY A10768 ]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"5\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>A</actionhouse>" +
            "<actioncode>1</actioncode>" +
            "<actiontype>S</actiontype>" +
            "<actiondata>38</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-01-03</actiondate>" +
            "<actiondesc><![CDATA[referred to governmental operations]]></actiondesc>" +
            "<actionpostdate>2018-01-03</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<action no=\"2018\">" +
            "<actionbillamd></actionbillamd>" +
            "<actionhouse>S</actionhouse>" +
            "<actioncode>350</actioncode>" +
            "<actiontype>B</actiontype>" +
            "<actiondata>10768</actiondata>" +
            "<actiondataamd></actiondataamd>" +
            "<actiondate>2018-06-19</actiondate>" +
            "<actiondesc>" +
            "<![CDATA[sub-by]]>" +
            "</actiondesc>" +
            "<actionpostdate>2018-06-19</actionpostdate>" +
            "<actionactsessyr>2018</actionactsessyr>" +
            "</action>" +
            "<actions>";
}
