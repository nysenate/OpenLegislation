package gov.nysenate.openleg.processor.bill;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.event.BillFieldUpdateEvent;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */
// TODO : figure out how to get the correct published_date_time to be stored in the bill_change_log table
@Service
public class XmlBillTextProcessor extends AbstractDataProcessor implements SobiProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlBillTextProcessor.class);
    @Autowired
    private XmlHelper xmlHelper;

    @Autowired
    private EventBus eventBus;

    public XmlBillTextProcessor() {}

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
        LocalDateTime date = sobiFragment.getPublishedDateTime();
        logger.info("Processing " + sobiFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(sobiFragment);
        try {
            final Document doc = xmlHelper.parse(sobiFragment.getText());
            final Node billTextNode = xmlHelper.getNode("billtext_html",doc);
            final int sessionYear = xmlHelper.getInteger("@sessyr",billTextNode);
            final String senhse = xmlHelper.getString("@senhse",billTextNode).replaceAll("\n","");
            final String senno = xmlHelper.getString("@senno",billTextNode).replaceAll("\n","");
            final String senamd = xmlHelper.getString("@senamd",billTextNode).replaceAll("\n","");
            final String asmhse = xmlHelper.getString("@asmhse",billTextNode).replaceAll("\n","");
            final String asmno = xmlHelper.getString("@asmno",billTextNode).replaceAll("\n","");
            final String asmamd = xmlHelper.getString("@asmamd",billTextNode).replaceAll("\n","");
            final String action = xmlHelper.getString("@action",billTextNode).replaceAll("\n","");
            final String billText = billTextNode.getTextContent();//.replaceAll("\n"," ");
            Set<BaseBillId> updatedBills = new HashSet<>();
            if (!senhse.isEmpty() && !asmhse.isEmpty()){ // uni bill
                //update senate
                final Version senVersion = Version.of(senamd);
                final Bill senateBill = getOrCreateBaseBill(
                        sobiFragment.getPublishedDateTime(),
                        new BillId(senhse + senno, new SessionYear(sessionYear), senVersion),
                        sobiFragment);
                senateBill.getAmendment(senVersion).setFullText(billText);
                billIngestCache.set(senateBill.getBaseBillId(), senateBill, sobiFragment);
                updatedBills.add(senateBill.getBaseBillId());
                //update assmbly
                final Version asmVersion = Version.of(asmamd);
                final Bill assemblyBill = getOrCreateBaseBill(
                        sobiFragment.getPublishedDateTime(),
                        new BillId(asmhse+asmno, new SessionYear(sessionYear), asmVersion),
                        sobiFragment);
                assemblyBill.getAmendment(asmVersion).setFullText(billText);
                billIngestCache.set(assemblyBill.getBaseBillId(), assemblyBill, sobiFragment);
                updatedBills.add(assemblyBill.getBaseBillId());
            }
            else {
                final Version version = Version.of(senamd.isEmpty() ? asmamd : senamd);
                final Bill baseBill = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), new BillId(senhse.isEmpty() ? asmhse + asmno : senhse + senno, new SessionYear(sessionYear), version), sobiFragment);
                baseBill.getAmendment(version).setFullText(billText);
                billIngestCache.set(baseBill.getBaseBillId(), baseBill, sobiFragment);
                updatedBills.add(baseBill.getBaseBillId());
            }
            updatedBills.forEach(baseBillId ->
                    eventBus.post(new BillFieldUpdateEvent(LocalDateTime.now(),
                            baseBillId, BillUpdateField.FULLTEXT)));
        }catch (IOException | SAXException |XPathExpressionException e) {
            throw new ParseError("Error While Parsing Bill Text XML", e);
        }
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }

}
