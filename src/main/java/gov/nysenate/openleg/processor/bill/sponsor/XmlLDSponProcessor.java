package gov.nysenate.openleg.processor.bill.sponsor;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.ProgramInfo;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.AbstractBillProcessor;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.regex.Pattern;

/**
 * This class is responsible for Processing the Sponsor Sobi Fragments
 *
 * Created by Robert Bebber on 2/22/17.
 */
@Service
public class XmlLDSponProcessor extends AbstractBillProcessor implements LegDataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XmlLDSponProcessor.class);

    /** The format for program info lines. */
    protected static final Pattern programInfoPattern = Pattern.compile("(\\d+)\\s+(.+)");

    @Autowired
    private XmlHelper xmlHelper;

    public XmlLDSponProcessor() {
    }

    @Override
    public void init() {
        initBase();
    }

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
                amendment.setCoSponsors(getSessionMember(coprime, baseBill.getSession(), chamber, baseBill));
                amendment.setMultiSponsors(getSessionMember(multi, baseBill.getSession(), chamber, baseBill));
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

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled() || billIngestCache.exceedsCapacity()) {
            flushBillUpdates();
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

    @Override
    public void postProcess() {
        flushBillUpdates();
    }
}
