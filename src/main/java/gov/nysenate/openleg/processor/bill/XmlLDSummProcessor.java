package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
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
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */
@Service
public class XmlLDSummProcessor extends AbstractDataProcessor implements LegDataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XmlLDSummProcessor.class);

    @Autowired
    private XmlHelper xmlHelper;

    public XmlLDSummProcessor() {
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.LDSUMM;
    }

    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing " + legDataFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            final Document doc = xmlHelper.parse(legDataFragment.getText());
            final Node billTextNode = xmlHelper.getNode("digestsummary", doc);
            final int sessionYear = xmlHelper.getInteger("@sessyr", billTextNode);
            final String billhse = xmlHelper.getString("@billhse", billTextNode);
            final String billno = xmlHelper.getString("@billno", billTextNode);
            final String action = xmlHelper.getString("@action", billTextNode);
            final String summary = xmlHelper.getNode("digestsummary/summary", doc) == null ? "" : xmlHelper.getNode("digestsummary/summary", doc).getTextContent().replaceAll("º","§").replaceAll("\n"," ").trim();
            final String amd = xmlHelper.getString("digestsummary/summaryamendment", doc);
            final Version version = Version.of(amd);
            final String law = xmlHelper.getString("law", billTextNode).replaceAll("Â", "¶").replaceAll("º","§").replaceAll("\n"," ").replaceAll("\t", " ").replaceAll(" +"," ").trim();
            final Bill baseBill = getOrCreateBaseBill(new BillId(billhse + billno, new SessionYear(sessionYear), version), legDataFragment);
            baseBill.setSummary(summary);
            baseBill.getAmendment(version).setLaw(law);
            if (action.equals("replace")) { //replace bill
                /**
                 * add previous bills
                 */
                int totalNumsOfPreBills = xmlHelper.getNodeList("digestsummary/oldbill/oldyear", doc).getLength();
                for (int i = 1; i <= totalNumsOfPreBills; i++) {
                    int sess = xmlHelper.getInteger("digestsummary/oldbill/oldyear[" + i + "]", doc);
                    String oldhse = xmlHelper.getString("digestsummary/oldbill/oldhse[" + i + "]", doc).replaceAll("\n", "");
                    String oldno = xmlHelper.getString("digestsummary/oldbill/oldno[" + i + "]", doc).replaceAll("\n", "");
                    String oldamd = xmlHelper.getString("digestsummary/oldbill/oldamd[" + i + "]", doc).replaceAll("\n", "");
                    if (oldno.isEmpty() || oldhse.isEmpty())
                        break;
                    baseBill.setDirectPreviousVersion(new BillId(oldhse + oldno, SessionYear.of(sess), Version.of(oldamd)));
                }
            } else { //remove bill

                // clear Set<BillID> pre version
                baseBill.getAllPreviousVersions().clear();
                baseBill.setDirectPreviousVersion(null);
            }
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, legDataFragment);
            logger.info("Put base bill in the ingest cache.");

        } catch (IOException | SAXException | XPathExpressionException e) {
            unit.addException("XML LD Summ parsing error", e);
            throw new ParseError("Error While Parsing Bill Digest XML : " + legDataFragment.getFragmentId(), e);
        } finally {
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

    @Override
    public void init() {
        initBase();
    }

}
