package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillAmendment;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.ProgramInfo;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.AbstractBillProcessor;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * This class is responsible for Processing the Sponsor Sobi Fragments
 *
 * Created by Robert Bebber on 2/22/17.
 */
@Service
public class XmlLDSponProcessor extends AbstractBillProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XmlLDSponProcessor.class);

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.LDSPON;
    }

    /**
     * This method gets the specific identifiers for the fragment and then based on the prime tag, the method will
     * call separate methods depending on if the prime(sponsor) is a Budget Bill, Rule, or a standard sponsor.
     *
     * @param legDataFragment This is the fragment being worked on during this process
     */
    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing Sponsor...");
        LocalDateTime date = legDataFragment.getPublishedDateTime();
        logger.info("Processing " + legDataFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            final Document doc = xmlHelper.parse(legDataFragment.getText());
            final Node billTextNode = xmlHelper.getNode("sponsor_data", doc);

            final Integer sessyr = xmlHelper.getInteger("@sessyr", billTextNode);
            final String sponsorhse = xmlHelper.getString("@billhse", billTextNode).trim();
            final Integer sponsorno = xmlHelper.getInteger("@billno", billTextNode);
            final String action = xmlHelper.getString("@action", billTextNode).trim();
            final String prime = xmlHelper.getString("prime", billTextNode).trim();
            final String coprime = xmlHelper.getString("co-prime", billTextNode).trim();
            final String multi = xmlHelper.getString("multi", billTextNode).trim();

            if (sponsorno == 0) // if it is a LDBC error
                return;
            Bill baseBill = getOrCreateBaseBill(new BillId(sponsorhse +
                    sponsorno, sessyr), legDataFragment);
            Chamber chamber = baseBill.getBillType().getChamber();
            BillAmendment amendment = baseBill.getAmendment(baseBill.getActiveVersion());

            if (action.equals("remove")) {
                removeProcess(amendment, baseBill);
            } else {
                handlePrimaryMemberParsing(baseBill, prime, baseBill.getSession());
                amendment.setCoSponsors(getSessionMember(coprime, baseBill.getSession(), chamber, legDataFragment.getFragmentId()));
                amendment.setMultiSponsors(getSessionMember(multi, baseBill.getSession(), chamber, legDataFragment.getFragmentId()));
            }
            ArrayList<ProgramInfo> programInfos = new ArrayList<>();
            NodeList departdescs = doc.getElementsByTagName("departdesc");
            if (departdescs.getLength() > 0) {
                for (int index = 0; index < departdescs.getLength(); index++) {
                    Node programInfoNode = departdescs.item(index);
                    String programInfoString = programInfoNode.getFirstChild().getTextContent();
                    if (!programInfoString.isEmpty()) {
                        Matcher programMatcher = programInfoPattern.matcher(programInfoString);
                        if (programMatcher.find()) {
                            programInfos.add(new ProgramInfo(programMatcher.group(2), Integer.parseInt(programMatcher.group(1))));

                        }
                    }
                }
                if (programInfos.size() > 0) {
                    baseBill.setProgramInfo(programInfos.get(0));
                    baseBill.setModifiedDateTime(date);
                }
            }
            baseBill.setModifiedDateTime(legDataFragment.getPublishedDateTime());
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, legDataFragment);

        } catch (IOException | SAXException | XPathExpressionException e) {
            unit.addException("XML LD Spon parsing error", e);
            throw new ParseError("Error While Parsing SponsorSobiProcessorXML", e);
        } finally {
            postDataUnitEvent(unit);
            checkIngestCache();
        }
    }

    /**
     * This method is responsible for setting every element regarding the sponsor to empty.
     *
     * @param amendment the ammendment of the bill
     * @param bill the bill in respect to the sponsor fragement
     */
    public void removeProcess(BillAmendment amendment, Bill bill) {
        bill.setSponsor(null);
        List<SessionMember> empty1 = new ArrayList<>();
        amendment.setCoSponsors(empty1);
        List<SessionMember> empty2 = new ArrayList<>();
        amendment.setMultiSponsors(empty2);
    }
}
