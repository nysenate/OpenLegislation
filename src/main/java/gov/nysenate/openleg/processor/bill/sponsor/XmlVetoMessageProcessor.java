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
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;


/**
 * Created by uros on 3/2/17.
 */
@Service
public class XmlVetoMessageProcessor extends AbstractDataProcessor implements SobiProcessor {

    @Autowired
    XmlHelper xmlHelper;

    private static final Pattern signerPattern =
            Pattern.compile("\\d{5}\\s*(?:(?:The|This|These) bills? (?:is|are) disapproved\\.)?\\s*\\(signed\\) ([a-zA-Z.'\\- ]*[a-zA-Z.])");


    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.VETOMSG;
    }

    @Override
    public void process(SobiFragment fragment) {
        LocalDateTime date = fragment.getPublishedDateTime();
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node vetoMsgNode = xmlHelper.getNode("veto_message", doc);
            final String no = xmlHelper.getString("@no", vetoMsgNode);
            final int year = xmlHelper.getInteger("@year", vetoMsgNode);
            final String billhse = xmlHelper.getNode("veto_message/billhse", doc).getTextContent();
            final String billno = xmlHelper.getNode("veto_message/billno", doc).getTextContent();
            final String action = xmlHelper.getString("@action", vetoMsgNode);

            final String text = xmlHelper.getNode("veto_message", doc).getTextContent();


            final Version version = Version.DEFAULT;
            final Bill baseBill = getOrCreateBaseBill(date, new BillId(billhse + billno, new SessionYear(year), version), fragment);

            if (action.equals("replace")) {
            } else if (action.equals("remove")) {

            }
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new ParseError("Error While Parsing AnActXML", e);
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

    private String parseTextContent(String fullText)    {
        StringBuilder text = new StringBuilder();
        text.ensureCapacity(fullText.length());
        String content = "";

        for (String line : fullText.split("\n")) {
            fullText = parseLine(line, text, fullText);
        }

        return content;
    }

}

