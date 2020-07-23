package gov.nysenate.openleg.processor.bill.ldblurb;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
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

/**
 * Created by Robert Bebber on 3/16/17.
 */
@Service
public class XmlLDBlurbProcessor extends AbstractDataProcessor implements LegDataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XmlLDBlurbProcessor.class);
    @Autowired
    private XmlHelper xmlHelper;

    public XmlLDBlurbProcessor() {
    }

    @Override
    public void init() {
        initBase();
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.LDBLURB;
    }

    @Override
    public void process(LegDataFragment fragment) {
        logger.info("Processing LDBlurb...");
        logger.info("Processing " + fragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(fragment);
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node billTextNode = xmlHelper.getNode("sponsor_blurb", doc);
            final Integer billno = xmlHelper.getInteger("@billno", billTextNode);
            final String billhse = xmlHelper.getString("@billhse", billTextNode).trim();
            final Integer sessyr = xmlHelper.getInteger("@sessyr", billTextNode);
            final String action = xmlHelper.getString("@action", billTextNode).trim();
            final String blurb = billTextNode.getTextContent().trim();
            if (billno == 0) // if it is a LDBC error
                return;
            final Bill baseBill = getOrCreateBaseBill(new BillId(billhse +
                    billno, sessyr), fragment);
            if (action.equals("replace")) {
                baseBill.setLDBlurb(blurb);
            } else if (action.equals("remove")) {
                baseBill.setLDBlurb("");
            }
            baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, fragment);

        } catch (IOException | SAXException | XPathExpressionException e) {
            unit.addException("XML LD Blurb parsing error", e);
            throw new ParseError("Error While Parsing LDBlurbXML", e);
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
}
