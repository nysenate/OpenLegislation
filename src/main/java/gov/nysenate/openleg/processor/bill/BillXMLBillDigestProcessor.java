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

/**
 * Created by Chenguang He(gaoyike@gmail.com) on 2016/12/1.
 */
@Service
public class BillXMLBillDigestProcessor extends AbstractDataProcessor implements SobiProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BillXMLBillDigestProcessor.class);
    @Autowired
    private XmlHelper xmlHelper;

    public BillXMLBillDigestProcessor() {
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.LDSUMM;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        try {
            logger.info("XML Processing " + sobiFragment.getFragmentId());
            final Document doc = xmlHelper.parse(sobiFragment.getText());
            final Node billTextNode = xmlHelper.getNode("digestsummary",doc);
            final int sessionYear = xmlHelper.getInteger("@sessyr",billTextNode);
            final String billhse = xmlHelper.getString("@billhse",billTextNode);
            final String billno = xmlHelper.getString("@billno",billTextNode);
            final String action = xmlHelper.getString("@action",billTextNode); //todo wait for LDBC  explaination of action
            final String summary = xmlHelper.getNode("digestsummary/summary",doc).getTextContent();
            final String amd = "";//todo wait for LDBC explaination
            final Version version = Version.of(amd);
            final Bill baseBill = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), new BillId(billhse+billno, new SessionYear(sessionYear),version) ,sobiFragment);
            baseBill.setSummary(summary);
            /**
             * add previous bills
             */
            int totalNumsOfPreBills = xmlHelper.getNodeList("digestsummary/oldbill/oldyear",doc).getLength();
            for (int i = 1; i <= totalNumsOfPreBills; i++) {
                int  sess = xmlHelper.getInteger("digestsummary/oldbill/oldyear["+i+"]",doc);
                String oldhse = xmlHelper.getString("digestsummary/oldbill/oldhse["+i+"]",doc).replaceAll("\n","");
                String oldno = xmlHelper.getString("digestsummary/oldbill/oldno["+i+"]",doc).replaceAll("\n","");;
                String oldamd = xmlHelper.getString("digestsummary/oldbill/oldamd["+i+"]",doc).replaceAll("\n","");;
                baseBill.addPreviousVersion(new BillId(oldhse+oldno, SessionYear.of(sess),Version.of(oldamd)));
            }
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

    @Override
    public void init() {
        initBase();
    }
}
