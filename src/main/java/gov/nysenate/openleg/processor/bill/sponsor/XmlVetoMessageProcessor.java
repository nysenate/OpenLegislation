package gov.nysenate.openleg.processor.bill.sponsor;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.swing.text.Document;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;


/**
 * Created by uros on 3/2/17.
 */
public class XmlVetoMessageProcessor extends AbstractDataProcessor implements SobiProcessor {

    @Autowired
    XmlHelper xmlHelper;

    @Override
    public SobiFragmentType getSupportedType() { return SobiFragmentType.VETOMSG;
    }

    @Override
    public void process(SobiFragment fragment) {
        LocalDateTime date = fragment.getPublishedDateTime();
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node vetoMsgNode = xmlHelper.getNode("veto_message",doc);
            final String no = xmlHelper.getString("no",vetoMsgNode);
            final int year = xmlHelper.getInteger("year",vetoMsgNode);
            final String billhse = xmlHelper.getNode("veto_message/billhse",doc).getTextContent();
            final String billno = xmlHelper.getNode("veto_message/billno",doc).getTextContent();
            final String action = xmlHelper.getString("action", vetoMsgNode);


            final Version version = version.of("");
            final Bill baseBill = getOrCreateBaseBill(date, new BillId(billhse + billno, new SessionYear(year),version), fragment);

            if (action.equals("replace")) {
                final String text = xmlHelper.getNode("veto_message/pre", doc).getTextContent();
            }
            else if(action.equals("remove"))    {

            }
        }
        catch (IOException | SAXException |XPathExpressionException e) {
            throw new ParseError("Error While Parsing AnActXML", e);
        }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    @Override
    public void init() {
        initBase();
    }
}
