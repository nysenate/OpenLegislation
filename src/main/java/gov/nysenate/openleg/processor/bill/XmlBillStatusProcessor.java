package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.BillStatus;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.time.LocalDateTime;

/**
 * Created by uros on 2/14/17.
 */
public class XmlBillStatusProcessor extends AbstractDataProcessor implements SobiProcessor {

    @Autowired
    private XmlHelper xmlHelper;


    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.BILLSTAT;
    }

    @Override
    public void process(SobiFragment fragment) {
        LocalDateTime date = fragment.getPublishedDateTime();
            try {
                final Document doc = xmlHelper.parse(fragment.getText());
                final Node billStatNode = xmlHelper.getNode("billstatus", doc);
                final int sessionYear = xmlHelper.getInteger("@sessyr", billStatNode);
                final String billno = xmlHelper.getString("@billno", billStatNode).replaceAll("\n", "");
                final String action = xmlHelper.getString("@action", billStatNode).replaceAll("\n", "");
                final String billhse = xmlHelper.getString("@billhse", billStatNode).replaceAll("\n", "");
            }
            catch (Exception e) {
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
