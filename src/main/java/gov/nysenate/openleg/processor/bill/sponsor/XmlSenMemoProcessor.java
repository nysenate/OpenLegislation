package gov.nysenate.openleg.processor.bill.sponsor;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.AbstractMemoProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
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
 * Created by uros on 3/21/17.
 */
@Service
public class XmlSenMemoProcessor extends AbstractMemoProcessor implements SobiProcessor {

    @Autowired
    XmlHelper xmlHelper;

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.SENMEMO;    }

    @Override
    public void process(SobiFragment fragment) {
        LocalDateTime date = fragment.getPublishedDateTime();

        try{
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node senMemoNode = xmlHelper.getNode("senate_billmemo",doc);
            final int year = xmlHelper.getInteger("@sessyr", senMemoNode);
            final String billhse = xmlHelper.getString("@billhse", senMemoNode);
            final int billno = xmlHelper.getInteger("@billno", senMemoNode);
            final String billamd = xmlHelper.getString("@billamd",senMemoNode);
            final Version version = Version.of(billamd);

            Bill basebill = getOrCreateBaseBill(date, new BillId(billhse + billno, new SessionYear(year), version), fragment);

            BillAmendment amendment = basebill.getAmendment(version);
            amendment.setMemo(getNodeText(senMemoNode));


            billIngestCache.set(basebill.getBaseBillId(),basebill,fragment);
        }  catch (IOException | SAXException | XPathExpressionException e) {
        throw new ParseError("Error While Parsing senate_billMemo", e);
        }
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    @Override
    public void init() {
        initBase();
    }

    /*
    *    Gets text context from a CDATA node
    *
    *   @param node - CDATA node
    *   @return temp.getTextContent() - context of CDATA node
    */
    private String getNodeText(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node temp = childNodes.item(i);
            if (temp.getNodeType() == temp.CDATA_SECTION_NODE) {
                return temp.getTextContent();
            }
        }
        return "";
    }
}
