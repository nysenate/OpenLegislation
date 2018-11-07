package gov.nysenate.openleg.processor.bill;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.event.BillFieldUpdateEvent;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.XmlHelper;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.model.bill.BillTextFormat.*;

/**
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */
@Service
public class XmlBillTextProcessor extends AbstractDataProcessor implements SobiProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlBillTextProcessor.class);

    private static final Pattern filenamePrintNoPattern = Pattern.compile(
            ".*_BILLTEXT_(?<printNo>[A-Z][0-9]+[A-Z]?)\\.XML$", Pattern.CASE_INSENSITIVE
    );

    private final XmlHelper xmlHelper;
    private final EventBus eventBus;

    @Autowired
    public XmlBillTextProcessor(XmlHelper xmlHelper, EventBus eventBus) {
        this.xmlHelper = xmlHelper;
        this.eventBus = eventBus;
    }

    @Override
    public void init() {
        initBase();
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.BILLTEXT;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing " + sobiFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(sobiFragment);
        try {
            final Document doc = xmlHelper.parse(sobiFragment.getText());
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
            final String billText = "remove".equals(action)
                    ? ""
                    : billTextNode.getTextContent();
            String strippedBillText = BillTextUtils.parseHTMLtext(billText);

            Set<BillId> updatedBills = new HashSet<>();

            BillId filenamePrintNo = getFilenamePrintNo(sessionYear, sobiFragment);

            // For Resolutions only apply to the bill from the filename
            if (filenamePrintNo.getBillType().isResolution()) {
                applyBillText(filenamePrintNo, billText, strippedBillText, sobiFragment);
                updatedBills.add(filenamePrintNo);
            } else {
                // Apply special formatting for bill text
                strippedBillText = BillTextUtils.formatHtmlExtractedBillText(strippedBillText);
                // Apply to senate and/or assembly versions if referenced
                if (!StringUtils.isBlank(senhse)) {
                    BillId senateId = new BillId(senhse + senno, sessionYear, senamd);
                    applyBillText(senateId, billText, strippedBillText, sobiFragment);
                    updatedBills.add(senateId);
                }
                if (!StringUtils.isBlank(asmhse)) {
                    BillId assemblyId = new BillId(asmhse + asmno, sessionYear, asmamd);
                    applyBillText(assemblyId, billText, strippedBillText, sobiFragment);
                    updatedBills.add(assemblyId);
                }
            }

            updatedBills.forEach(billId ->
                    eventBus.post(new BillFieldUpdateEvent(LocalDateTime.now(),
                            BaseBillId.of(billId), BillUpdateField.FULLTEXT)));
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
        if (!env.isSobiBatchEnabled() || billIngestCache.exceedsCapacity()) {
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
    private void applyBillText(BillId billId,
                               String billText, String strippedBillText,
                               SobiFragment fragment) {
        final Bill baseBill = getOrCreateBaseBill(billId, fragment);
        BillAmendment amendment = baseBill.getAmendment(billId.getVersion());
        amendment.setFullText(HTML, billText);
        amendment.setFullText(PLAIN, strippedBillText);
        billIngestCache.set(baseBill.getBaseBillId(), baseBill, fragment);
    }

    /**
     * Parse the print no from the sobi fragment's filename
     */
    private BillId getFilenamePrintNo(int session, SobiFragment fragment) {
        String filename = fragment.getParentSobiFile().getFileName();
        Matcher matcher = filenamePrintNoPattern.matcher(filename);
        if (!matcher.find()) {
            throw new ParseError("Could not parse BILLTEXT filename: " + filename);
        }
        String printNo = matcher.group("printNo");
        return new BillId(printNo, session);
    }
}
