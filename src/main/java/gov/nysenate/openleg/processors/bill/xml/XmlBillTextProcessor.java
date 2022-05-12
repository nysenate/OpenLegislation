package gov.nysenate.openleg.processors.bill.xml;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import gov.nysenate.openleg.updates.bill.BillFieldUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */
@Service
public class XmlBillTextProcessor extends AbstractLegDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlBillTextProcessor.class);

    private static final Pattern filenamePrintNoPattern = Pattern.compile(
            ".*_BILLTEXT_(?<printNo>[A-Z][0-9]+[A-Z]?)\\.XML$", Pattern.CASE_INSENSITIVE
    );

    private final EventBus eventBus;
    private final BillTextDiffProcessor textDiffProcessor;

    @Autowired
    public XmlBillTextProcessor(XmlHelper xmlHelper, EventBus eventBus, BillTextDiffProcessor textDiffProcessor) {
        this.xmlHelper = xmlHelper;
        this.eventBus = eventBus;
        this.textDiffProcessor = textDiffProcessor;
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.BILLTEXT;
    }

    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing " + legDataFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            final Document doc = xmlHelper.parse(legDataFragment.getText());
            final Node billTextNode = xmlHelper.getNode("billtext_html", doc);

            final int sessionYear = xmlHelper.getInteger("@sessyr", billTextNode);
            final String senhse = xmlHelper.getString("@senhse", billTextNode);
            final String senno = xmlHelper.getString("@senno", billTextNode);
            final String senamd = xmlHelper.getString("@senamd", billTextNode);
            final String asmhse = xmlHelper.getString("@asmhse", billTextNode);
            final String asmno = xmlHelper.getString("@asmno", billTextNode);
            final String asmamd = xmlHelper.getString("@asmamd", billTextNode);
            final String action = xmlHelper.getString("@action", billTextNode);

            // If remove action, set bill text to blank
            final String text = "remove".equals(action)
                    ? ""
                    : billTextNode.getTextContent();

            BillText billText = textDiffProcessor.processBillText(text);

            Set<BillId> updatedBills = new HashSet<>();
            BillId filenamePrintNo = getFilenamePrintNo(sessionYear, legDataFragment);

            // For Resolutions only apply to the bill from the filename
            if (filenamePrintNo.getBillType().isResolution()) {
                applyBillText(filenamePrintNo, billText, legDataFragment);
                updatedBills.add(filenamePrintNo);
            } else {
                // Apply to senate and/or assembly versions if referenced
                if (!StringUtils.isBlank(senhse)) {
                    BillId senateId = new BillId(senhse + senno, sessionYear, senamd);
                    applyBillText(senateId, billText, legDataFragment);
                    updatedBills.add(senateId);
                }
                if (!StringUtils.isBlank(asmhse)) {
                    BillId assemblyId = new BillId(asmhse + asmno, sessionYear, asmamd);
                    applyBillText(assemblyId, billText, legDataFragment);
                    updatedBills.add(assemblyId);
                }
            }

            updatedBills.forEach(billId ->
                    eventBus.post(new BillFieldUpdateEvent(BaseBillId.of(billId),
                            BillUpdateField.FULLTEXT)));
        } catch (IOException | SAXException | XPathExpressionException e) {
            unit.addException("XML bill text parsing error", e);
            throw new ParseError("Error While Parsing Bill Text XML", e);
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

    /* --- Internal Methods --- */

    /**
     * Applies bill text to a single bill using the values parsed from the billtext html tag
     */
    private void applyBillText(BillId billId, BillText billText, LegDataFragment fragment) {
        final Bill baseBill = getOrCreateBaseBill(billId, fragment);
        BillAmendment amendment = baseBill.getAmendment(billId.getVersion());
        amendment.setBillText(billText);
        baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
        billIngestCache.set(baseBill.getBaseBillId(), baseBill, fragment);
    }

    /**
     * Parse the print no from the sobi fragment's filename
     */
    private BillId getFilenamePrintNo(int session, LegDataFragment fragment) {
        String filename = fragment.getParentLegDataFile().getFileName();
        Matcher matcher = filenamePrintNoPattern.matcher(filename);
        if (!matcher.find()) {
            throw new ParseError("Could not parse BILLTEXT filename: " + filename);
        }
        String printNo = matcher.group("printNo");
        return new BillId(printNo, session);
    }
}
