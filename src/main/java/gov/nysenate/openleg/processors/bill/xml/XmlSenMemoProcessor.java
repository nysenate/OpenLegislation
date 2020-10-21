package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillAmendment;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.LegDataProcessor;
import gov.nysenate.openleg.legislation.bill.utils.BillTextUtils;
import gov.nysenate.openleg.common.util.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * Created by uros on 3/21/17.
 */
@Service
public class XmlSenMemoProcessor extends AbstractMemoProcessor {

    @Autowired
    public XmlSenMemoProcessor(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.SENMEMO;
    }

    @Override
    public void process(LegDataFragment fragment) {
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node senMemoNode = xmlHelper.getNode("senate_billmemo", doc);
            final int year = xmlHelper.getInteger("@sessyr", senMemoNode);
            final String billhse = xmlHelper.getString("@billhse", senMemoNode);
            final int billno = xmlHelper.getInteger("@billno", senMemoNode);
            final String billamd = xmlHelper.getString("@billamd", senMemoNode);
            final Version version = Version.of(billamd);

            BillId billId = new BillId(billhse + billno, new SessionYear(year), version);

            Bill basebill = getOrCreateBaseBill(billId, fragment);

            String memoText = getNodeText(senMemoNode);

            BillAmendment amendment = basebill.getAmendment(version);
            amendment.setMemo(memoText);

            basebill.setModifiedDateTime(fragment.getPublishedDateTime());
            billIngestCache.set(basebill.getBaseBillId(), basebill, fragment);
            checkIngestCache();
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new ParseError("Error While Parsing senate_billMemo", e);
        }
    }

    /**
     * Gets text context from a CDATA node
     *
     * @param node - CDATA node
     * @return temp.getTextContent() - context of CDATA node
     */
    private String getNodeText(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node temp = childNodes.item(i);
            if (temp.getNodeType() == temp.CDATA_SECTION_NODE) {
                String htmlMemoText = temp.getTextContent();
                return BillTextUtils.convertHtmlToPlainText(htmlMemoText);
            }
        }
        return "";
    }
}
