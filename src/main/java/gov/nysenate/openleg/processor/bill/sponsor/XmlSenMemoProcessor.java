package gov.nysenate.openleg.processor.bill.sponsor;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.AbstractMemoProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by uros on 3/21/17.
 */
public class XmlSenMemoProcessor extends AbstractMemoProcessor implements SobiProcessor {

    @Autowired
    XmlHelper xmlHelper;

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.SENMEMO;    }

    @Override
    public void process(SobiFragment fragment) {
        LocalDateTime data = fragment.getPublishedDateTime();

        try{
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node senMemoNode = xmlHelper.getNode("senate_billmemon",doc);
            final int sessyr = xmlHelper.getInteger("@sessyr", senMemoNode);
            final String billhse = xmlHelper.getString("@billhse", senMemoNode);
            final int billno = xmlHelper.getInteger("@billno", senMemoNode);
            final String billamd = xmlHelper.getString("@billamd",senMemoNode);
            final String action = xmlHelper.getString("@action",senMemoNode);

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
}
