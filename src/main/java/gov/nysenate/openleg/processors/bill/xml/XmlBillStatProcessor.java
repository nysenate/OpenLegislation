package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillAmendment;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.processors.LegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.AbstractBillProcessor;
import gov.nysenate.openleg.processors.bill.BillActionParser;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Robert Bebber on 3/20/17.
 */
@Service
public class XmlBillStatProcessor extends AbstractBillProcessor implements LegDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlBillStatProcessor.class);

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.BILLSTAT;
    }

    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing " + legDataFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            final Document doc = xmlHelper.parse(legDataFragment.getText());
            final Node billStatusNode = xmlHelper.getNode("billstatus", doc);

            // extract bill id
            final Integer sessyr = xmlHelper.getInteger("@sessyr", billStatusNode);
            final String billhse = xmlHelper.getString("@billhse", billStatusNode).trim();
            final Integer billno = xmlHelper.getInteger("@billno", billStatusNode);
            final String version = xmlHelper.getString("currentamd", billStatusNode).trim();

            BillId billId = new BillId(billhse + billno, sessyr, version);
            Bill bill = getOrCreateBaseBill(billId, legDataFragment);

            final String action = xmlHelper.getString("@action", billStatusNode).trim();
            switch (action) {
                case "remove" -> removeCase(bill, legDataFragment);
                case "replace" -> replaceCase(billStatusNode, bill, billId, legDataFragment);
                default -> throw new IllegalArgumentException(
                        "Unrecognized xml action: " + action + " in fragment: " + legDataFragment);
            }

            bill.setModifiedDateTime(legDataFragment.getPublishedDateTime());
            billIngestCache.set(bill.getBaseBillId(), bill, legDataFragment);
            postDataUnitEvent(unit);
            checkIngestCache();
        } catch (IOException | SAXException | XPathExpressionException e) {
            unit.addException("XML bill stat parsing error", e);
            throw new ParseError("Error While Parsing BillStatProcessorXML", e);
        }
    }

    /**
     * Un-publish this bill.
     *
     * @param baseBill      The Bill in alteration
     * @param fragment LegDataFragment
     */
    private void removeCase(Bill baseBill, LegDataFragment fragment) {
        baseBill.updatePublishStatus(
                Version.ORIGINAL,
                new PublishStatus(false, fragment.getPublishedDateTime()));
    }

    /**
     * Set bill fields according to BILLSTAT data
     *
     * @param billStatusNode Node
     * @param bill Bill
     * @param billId BillId
     * @param fragment LegDataFragment
     * @throws XPathExpressionException if improper x-paths are used
     */
    private void replaceCase(Node billStatusNode, Bill bill, BillId billId, LegDataFragment fragment) throws XPathExpressionException {
        BillAmendment billAmendment = bill.getAmendment(billId.getVersion());

        // Publish the base version if not already done
        if (billAmendment.isBaseVersion()) {
            Optional<PublishStatus> pubStatus = bill.getPublishStatus(billAmendment.getVersion());
            if (pubStatus.isEmpty() || !pubStatus.get().isPublished()) {
                bill.updatePublishStatus(billAmendment.getVersion(),
                        new PublishStatus(true, fragment.getPublishedDateTime(), false, ""));
            }
        }

        // Parse reprint if present
        boolean reprinted = false;
        final String reprintBillhse = xmlHelper.getString("reprint/rprtbillhse", billStatusNode);
        final Integer rprtBillno = xmlHelper.getInteger("reprint/rprtbillno", billStatusNode);
        final String rprtVersion = xmlHelper.getString("reprint/rprtbillamd", billStatusNode);
        if (!reprintBillhse.isEmpty()) {
            reprinted = true;
        }
        if (reprinted) {
            bill.setReprintOf(
                    new BillId(reprintBillhse + rprtBillno, billId.getSession().year(), rprtVersion));
        } else {
            bill.setReprintOf(null);
        }

        // Parse sponsor, law section, and title
        String sponsor = xmlHelper.getString("sponsor", billStatusNode).trim();
        handlePrimaryMemberParsing(bill, sponsor, bill.getSession());
        String lawSec = xmlHelper.getString("law", billStatusNode).trim();
        billAmendment.setLawSection(lawSec);
        String title = xmlHelper.getString("title", billStatusNode).trim();
        bill.setTitle(title);

        // Parse actions
        String billactions = xmlHelper.getString("billactions", billStatusNode).trim();
        billactions = reformatBillActions(billactions);
        Node xmlActions = xmlHelper.getNode("actions", billStatusNode);

        parseActions(billactions, bill, billAmendment, fragment, xmlActions);
        bill.setYear(BillActionParser.getCalendarYear(xmlActions, xmlHelper));
    }

    /**
     * This method is used to reformat billaction, adding the space between lines in CDATA
     *
     * @param str bill action string
     */
    private static String reformatBillActions(String str) {
        if (str.isEmpty())
            return str;
        return str.replaceAll("(\\d\\d)/(\\d\\d)/(\\d\\d)", "\n$0").substring(1);
    }
}
