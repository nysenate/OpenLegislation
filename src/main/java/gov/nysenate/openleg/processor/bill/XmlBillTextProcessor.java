package gov.nysenate.openleg.processor.bill;

import com.google.common.base.MoreObjects;
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
import java.util.Optional;
import java.util.Set;

/**
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */
@Service
public class XmlBillTextProcessor extends AbstractDataProcessor implements SobiProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlBillTextProcessor.class);

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

            BillId senateId = null;
            BillId assemblyId = null;

            if (!StringUtils.isBlank(senhse)) {
                senateId = new BillId(senhse + senno, sessionYear, senamd);
            }
            if (!StringUtils.isBlank(asmhse)) {
                assemblyId = new BillId(asmhse + asmno, sessionYear, asmamd);
            }

            boolean isResolution = isResolution(senateId, assemblyId);

            // If remove action, set bill text to blank
            final String billText = "remove".equals(action)
                    ? ""
                    : billTextNode.getTextContent();
            String strippedBillText = BillTextUtils.parseHTMLtext(billText);
            if (!isResolution) {
                strippedBillText = BillTextUtils.formatHtmlExtractedBillText(strippedBillText);
            }

            Set<BaseBillId> updatedBills = new HashSet<>();

            if (senateId != null) {
                applyBillText(senateId, billText, strippedBillText, sobiFragment, updatedBills);
            }
            if (assemblyId != null) {
                applyBillText(assemblyId, billText, strippedBillText, sobiFragment, updatedBills);
            }

            updatedBills.forEach(baseBillId ->
                    eventBus.post(new BillFieldUpdateEvent(LocalDateTime.now(),
                            baseBillId, BillUpdateField.FULLTEXT)));
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
                               SobiFragment fragment, Set<BaseBillId> updatedBills) {
        final Bill baseBill = getOrCreateBaseBill(billId, fragment);
        BillAmendment amendment = baseBill.getAmendment(billId.getVersion());
        amendment.setFullTextHtml(billText);
        amendment.setFullText(strippedBillText);
        billIngestCache.set(baseBill.getBaseBillId(), baseBill, fragment);
        updatedBills.add(baseBill.getBaseBillId());
    }

    private boolean isResolution(BillId senateId, BillId assemblyId) {
        Optional<Boolean> senReso = Optional.ofNullable(senateId).map(billId -> billId.getBillType().isResolution());
        Optional<Boolean> asmReso = Optional.ofNullable(assemblyId).map(billId -> billId.getBillType().isResolution());
        if (senReso.isPresent() && asmReso.isPresent() && !senReso.get().equals(asmReso.get())) {
            throw new IllegalStateException("Conflicting bill types for shared bill text: " +
                    senateId + " " + assemblyId);
        }
        return MoreObjects.firstNonNull(senReso.orElse(null), asmReso.orElse(null));
    }

}
