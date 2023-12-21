package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillAmendment;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.AbstractBillProcessor;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by uros on 2/16/17.
 */
@Service
public class XmlSameAsProcessor extends AbstractBillProcessor {
    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.SAMEAS;
    }

    @Override
    public void process(LegDataFragment fragment) {
        DataProcessUnit unit = createProcessUnit(fragment);
        try {
            final Document doc = xmlHelper.parse(fragment.getText());
            final Node billSameAsNode = xmlHelper.getNode("sameas", doc);
            final String billno = xmlHelper.getString("@billno", billSameAsNode).replaceAll("\n", "");
            final String billhse = xmlHelper.getString("@billhse", billSameAsNode).replaceAll("\n", "");
            final String billamd = xmlHelper.getString("@billamd", billSameAsNode).replaceAll("\n", "");
            final int sessionYear = xmlHelper.getInteger("@sessyr", billSameAsNode);
            final String action = xmlHelper.getString("@action", billSameAsNode).replaceAll("\n", "");

            final Version version = Version.of(billamd);
            final Bill baseBill = getOrCreateBaseBill(new BillId(billhse + billno, new SessionYear(sessionYear), version), fragment);

            BillAmendment amendment = baseBill.getAmendment(version);

            amendment.getSameAs().clear();

            if (action.equals("remove")) {
                amendment.setUniBill(false);
            }
            else if (action.equals("replace")) {
                final NodeList nList = xmlHelper.getNodeList("sameas/sameasbill", doc);
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node node = nList.item(temp);
                    String sameasBillContext = node.getTextContent();

                    Matcher sameAsMatcher = sameAsPattern.matcher(sameasBillContext);
                    if (sameAsMatcher.find()) {

                        List<String> sameAsMatchesBeforeSplit = new ArrayList<>(Collections.singletonList(sameAsMatcher.group(2)));
                        for (String sameAs : sameAsMatchesBeforeSplit) {
                            if (sameAs.contains(",")) {
                                for( String splitSameAs: sameAs.split(",") ) {
                                    amendment.getSameAs().add(createBillId(splitSameAs, amendment.getSession()));
                                }
                            }
                            else if (sameAs.contains("/")) {
                                for( String splitSameAs: sameAs.split("/") ) {
                                    amendment.getSameAs().add(createBillId(splitSameAs, amendment.getSession()));
                                }                            }
                            else {
                                amendment.getSameAs().add(createBillId(sameAs, amendment.getSession()));
                            }
                        }

                        // Check for uni-bill
                        if (sameAsMatcher.group(1) != null && !sameAsMatcher.group(1).isEmpty()) {
                            amendment.setUniBill(true);
                        }
                    }
                }
            }
            baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
            billIngestCache.set(baseBill.getBaseBillId(),baseBill,fragment);

        } catch (IOException | SAXException |XPathExpressionException e) {
            unit.addException("XML SameAs parsing error", e);
            throw new ParseError("Error While Parsing sameAsXML", e);
        } finally {
            postDataUnitEvent(unit);
            checkIngestCache();
        }
    }

    private BillId createBillId(String line, SessionYear sessionYear) {
        return new BillId(line.replace(" ", "").replace("-",""), sessionYear);
    }
}
