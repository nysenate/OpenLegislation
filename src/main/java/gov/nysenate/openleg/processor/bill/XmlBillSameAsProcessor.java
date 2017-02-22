package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by uros on 2/16/17.
 */
@Service
public class XmlBillSameAsProcessor extends AbstractDataProcessor implements SobiProcessor {

    XmlHelper xmlHelper;

    protected static final Pattern sameAsPattern =
            Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?)+)");

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.SAMEAS;
    }

    @Override
    public void process(SobiFragment fragment) {
        LocalDateTime date = fragment.getPublishedDateTime();
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node billSameAsNode = xmlHelper.getNode("sameas", doc);
            final String billno = xmlHelper.getString("@billno", billSameAsNode).replaceAll("\n", "");
            final String billhse = xmlHelper.getString("@billhse", billSameAsNode).replaceAll("\n", "");
            final String billamd = xmlHelper.getString("@billamd", billSameAsNode).replaceAll("\n","");
            final int sessionYear = xmlHelper.getInteger("@sessyr", billSameAsNode);
            final String action = xmlHelper.getString("@action", billSameAsNode).replaceAll("\n", "");
            final String sameasBillContext = xmlHelper.getNode("sameas/sameasbill",doc).getTextContent();
            final Version version = Version.of(billamd);

            final Bill baseBill = getOrCreateBaseBill(date, new BillId(billhse+billno,new SessionYear(sessionYear),version),fragment);

            BillAmendment amd = baseBill.getAmendment(version);

            if  (action.equals("remove")) {
                amd.getSameAs().clear();
                amd.setUniBill(false);
            }
            else if(action.equals("replace"))    {
                Matcher sameAsMatcher = sameAsPattern.matcher(sameasBillContext);
                if (sameAsMatcher.find())   {
                    amd.getSameAs().clear();
                    List<String> sameAsMatches = new ArrayList<>(Arrays.asList(sameAsMatcher.group(2)));
                    for (String sameAs : sameAsMatches) {
                        amd.getSameAs().add(new BillId(sameAs.replace(" ", ""), amd.getSession()));
                    }
                }
            }
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
