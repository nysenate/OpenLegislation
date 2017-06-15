package gov.nysenate.openleg.processor.bill.apprmemo;

import gov.nysenate.openleg.dao.bill.data.ApprovalDao;
import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.AbstractMemoProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
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
import java.time.LocalDateTime;

/**
 * This Class is responsible for Processing the Approval Memorandum.
 * <p>
 * Created by Robert Bebber on 3/6/17.
 */
@Service
public class ApprmemoProcessor extends AbstractMemoProcessor implements SobiProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApprmemoProcessor.class);
    @Autowired
    private XmlHelper xmlHelper;

    @Autowired
    private ApprovalDao approvalDao;

    public ApprmemoProcessor() {
    }

    @Override
    public void init() {
        initBase();
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.APPRMEMO;
    }

    @Override
    public void process(SobiFragment fragment) {
        logger.info("Processing Apprmemo...");
        LocalDateTime date = fragment.getPublishedDateTime();
        logger.info("Processing " + fragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(fragment);
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node apprmemoNode = xmlHelper.getNode("approval_memorandum", doc);
            final Integer apprno = xmlHelper.getInteger("@no", apprmemoNode);
            final Integer year = xmlHelper.getInteger("@year", apprmemoNode);
            final String action = xmlHelper.getString("@action", apprmemoNode).trim();

            NodeList childNodes = apprmemoNode.getChildNodes();
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
            Bill baseBill = null;
            if (action.equals("remove")) {
                ApprovalMessage add = approvalDao.getApprovalMessage(new ApprovalId(year, apprno));
                baseBill = getOrCreateBaseBill(fragment.getPublishedDateTime(), add.getBillId(), fragment);
                baseBill.setApprovalMessage(null);
            } else {
                baseBill = getOrCreateBaseBill(fragment.getPublishedDateTime(), new BillId(billhse +
                        billno, year), fragment);
                applyMemoText(cdata, baseBill, apprno, action);
            }
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, fragment);
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new ParseError("Error While Parsing ApprmemoSobiXML", e);
        }
    }

    /**
     * Constructs an approval message object by parsing a memo
     *
     * @param data
     * @param baseBill
     * @throws ParseError
     */
    private void applyMemoText(String data, Bill baseBill, int apprno, String action) throws ParseError {
        BillId BillId = baseBill.getActiveAmendment().getBillId();
        ApprovalMessageParser approvalMessageParser = new ApprovalMessageParser(data, BillId, apprno, action);
        approvalMessageParser.extractText();
        ApprovalMessage approvalMessage = approvalMessageParser.getApprovalMessage();
        approvalMessage.setBillId(BillId);
        baseBill.setApprovalMessage(approvalMessage);
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }
}
