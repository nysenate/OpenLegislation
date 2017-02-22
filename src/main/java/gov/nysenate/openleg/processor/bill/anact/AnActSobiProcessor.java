package gov.nysenate.openleg.processor.bill.anact;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by robert on 2/15/17.
 */
@Service
public class AnActSobiProcessor extends AbstractDataProcessor implements SobiProcessor{

    private static final Logger logger = LoggerFactory.getLogger(AnActSobiProcessor.class);
    @Autowired private XmlHelper xmlHelper;
    public AnActSobiProcessor(){

    }

    @Override
    public void init() {
        initBase();
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.ANACT;
    }

    @Override
    public void process(SobiFragment sobiFragment){

        logger.info("Processing AnAct...");
        LocalDateTime date = sobiFragment.getPublishedDateTime();
        logger.info("Processing " + sobiFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(sobiFragment);
        try {
            final Document doc = xmlHelper.parse(sobiFragment.getText());
            final Node billTextNode = xmlHelper.getNode("anact",doc);
            final Integer anactno = xmlHelper.getInteger("@billno",billTextNode);
            final String anacthse = xmlHelper.getString("@billhse",billTextNode).trim();
            final String anactamd = xmlHelper.getString("@billamd",billTextNode).trim();
            final Integer sessyr = xmlHelper.getInteger("@sessyr",billTextNode);
            final String title = xmlHelper.getString("@title",billTextNode).trim();
            final String action = xmlHelper.getString("@action",billTextNode).trim(); // TODO: implement actions
            final String anactClause = billTextNode.getTextContent().trim();
            final Version version = Version.of(anactamd);
            final Bill baseAnAct = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), new BillId(new BaseBillId(anacthse+anactno, new SessionYear(sessyr)),Version.of(anactamd)), sobiFragment);
            baseAnAct.getAmendment(version).setActClause(anactClause);
            billIngestCache.set(baseAnAct.getBaseBillId(), baseAnAct, sobiFragment);
        } catch (IOException | SAXException |XPathExpressionException e) {
            throw new ParseError("Error While Parsing AnActXML", e);
        }

        if(!env.isSobiBatchEnabled()||billIngestCache.exceedsCapacity()){
            flushBillUpdates();
        }
    }

    /**
     * Best JavaDoc right here
     */
    @Override
    public void postProcess() {
        flushBillUpdates();
    }
}
