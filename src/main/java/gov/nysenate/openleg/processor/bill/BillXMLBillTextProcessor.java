package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */

@Service
public class BillXMLBillTextProcessor extends AbstractDataProcessor implements SobiProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BillXMLBillTextProcessor.class);
    @Autowired
    private XmlHelper xmlHelper;

    public BillXMLBillTextProcessor() {
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
        try {
            logger.info("XML Processing " + sobiFragment.getFragmentId());
            final Document doc = xmlHelper.parse(sobiFragment.getText());
            final Node billTextNode = xmlHelper.getNode("billtext_html",doc);
            final int sessionYear = xmlHelper.getInteger("@sessyr",billTextNode);
            final String senhse = xmlHelper.getString("@senhse",billTextNode).replaceAll("\n","");;
            final String senno = xmlHelper.getString("@senno",billTextNode).replaceAll("\n","");;
            final String senamd = xmlHelper.getString("@senamd",billTextNode).replaceAll("\n","");;
            final String asmhse = xmlHelper.getString("@asmhse",billTextNode).replaceAll("\n","");;
            final String asmno = xmlHelper.getString("@asmno",billTextNode).replaceAll("\n","");;
            final String asmamd = xmlHelper.getString("@asmamd",billTextNode).replaceAll("\n","");;
            final String action = xmlHelper.getString("@action",billTextNode).replaceAll("\n","");; //todo wait for LDBC for explaination of action
            final String billText = billTextNode.getTextContent().replaceAll("\n","");;
            final Version version = Version.of(senamd.isEmpty() ? asmamd:senamd);
            final Bill baseBill = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), new BillId(senhse.isEmpty() ? asmhse+asmno : senhse+senno, new SessionYear(sessionYear),version) ,sobiFragment);
            baseBill.getAmendment(version).setFullText(billText);
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, sobiFragment);
            System.out.println("abc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }


}
