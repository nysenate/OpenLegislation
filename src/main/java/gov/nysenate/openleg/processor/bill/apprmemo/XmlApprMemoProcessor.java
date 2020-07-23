package gov.nysenate.openleg.processor.bill.apprmemo;

import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.AbstractMemoProcessor;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
import gov.nysenate.openleg.service.bill.data.ApprovalNotFoundException;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * This Class is responsible for Processing the Approval Memorandum.
 * <p>
 * Created by Robert Bebber on 3/6/17.
 */
@Service
public class XmlApprMemoProcessor extends AbstractMemoProcessor implements LegDataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XmlApprMemoProcessor.class);
    @Autowired
    private XmlHelper xmlHelper;

    @Override
    public void init() {
        initBase();
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.APPRMEMO;
    }

    @Override
    public void process(LegDataFragment fragment) {
        logger.info("Processing " + fragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(fragment);
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node apprMemoNode = xmlHelper.getNode("approval_memorandum", doc);
            final int apprNo = xmlHelper.getInteger("@no", apprMemoNode);
            final int year = xmlHelper.getInteger("@year", apprMemoNode);
            final String action = xmlHelper.getString("@action", apprMemoNode).trim();

            if ("remove".equals(action)) {
                removeApprovalMemo(fragment, apprNo, year);
            } else if ("replace".equals(action)) {
                replaceApprovalMemo(fragment, apprMemoNode, apprNo, year);
            } else {
                throw new ParseError("Unknown approval memo action: " + action);
            }
        } catch (IOException | SAXException | XPathExpressionException e) {
            unit.addException("XML Appr Memo parsing error", e);
            throw new ParseError("Error While Parsing ApprmemoSobiXML", e);
        }
        finally {
            postDataUnitEvent(unit);
            checkIngestCache();
        }
    }

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled() || billIngestCache.exceedsCapacity()) {
            flushBillUpdates();
        }
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    /* --- Internal Methods --- */

    private void removeApprovalMemo(LegDataFragment fragment, int apprNo, int year) {
        ApprovalId approvalId = new ApprovalId(year, apprNo);
        try {
            ApprovalMessage approvalMessage = apprDataService.getApprovalMessage(approvalId);
            BillId billId = approvalMessage.getBillId();
            Bill bill = getOrCreateBaseBill(billId, fragment);
            bill.setApprovalMessage(null);
            bill.setModifiedDateTime(fragment.getPublishedDateTime());
            billIngestCache.set(bill.getBaseBillId(), bill, fragment);
        } catch (ApprovalNotFoundException ignored) {
            // if the approval doesn't exist, we can't remove it.
            logger.warn("Attempt to remove unknown approval message: {}", approvalId);
        }
    }

    private void replaceApprovalMemo(LegDataFragment fragment, Node apprMemoNode, int apprNo, int year) {
        NodeList childNodes = apprMemoNode.getChildNodes();
        String billhse = null;
        Integer billno = null;
        String cdata = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            switch (node.getNodeName()) {
                case "billhse":
                    billhse = node.getTextContent();
                    break;
                case "billno":
                    billno = Integer.parseInt(node.getTextContent());
                    break;
                case "#cdata-section":
                    cdata = node.getTextContent();
                    cdata = parsedMemoHTML(cdata);
                    break;
                default:
                    break;
            }
        }
        BillId billId = new BillId(billhse + billno, year);
        Bill bill = getOrCreateBaseBill(billId, fragment);
        applyMemoText(cdata, bill, apprNo, year);
        bill.setModifiedDateTime(fragment.getPublishedDateTime());
        billIngestCache.set(bill.getBaseBillId(), bill, fragment);
    }

    /**
     * Constructs an approval message object by parsing a memo
     *
     * @param data
     * @param baseBill
     * @throws ParseError
     */
    private void applyMemoText(String data, Bill baseBill, int apprno, Integer year) throws ParseError {
        BillId BillId = baseBill.getActiveAmendment().getBillId();
        ApprovalMessageParser approvalMessageParser = new ApprovalMessageParser(data, BillId, apprno);
        approvalMessageParser.extractText();
        ApprovalMessage approvalMessage = approvalMessageParser.getApprovalMessage();
        approvalMessage.setBillId(BillId);
        approvalMessage.setYear(year);
        baseBill.setApprovalMessage(approvalMessage);
    }
}
