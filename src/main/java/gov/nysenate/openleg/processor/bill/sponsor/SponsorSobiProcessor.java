package gov.nysenate.openleg.processor.bill.sponsor;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.anact.AnActSobiProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import jdk.nashorn.internal.objects.NativeArray;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by robert on 2/22/17.
 */
@Service
public class SponsorSobiProcessor extends AbstractDataProcessor implements SobiProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SponsorSobiProcessor.class);
    @Autowired private XmlHelper xmlHelper;

    public SponsorSobiProcessor(){}

    @Override
    public void init() {
        initBase();
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.LDSPON;
    }

    @Override
    public void process(SobiFragment sobiFragment){
         logger.info("Processing Sponsor...");
         LocalDateTime date = sobiFragment.getPublishedDateTime();
         logger.info("Processing " + sobiFragment.getFragmentId() + " (xml file).");
         DataProcessUnit unit = createProcessUnit(sobiFragment);
         try {
             final Document doc = xmlHelper.parse(sobiFragment.getText());
             final Node billTextNode = xmlHelper.getNode("sponsor_data", doc);
             final Integer sessyr = xmlHelper.getInteger("@sessyr", billTextNode);
             final String sponsorhse = xmlHelper.getString("@billhse", billTextNode).trim();
             final Integer sponsorno = xmlHelper.getInteger("@billno", billTextNode);
             final String action = xmlHelper.getString("@action", billTextNode).trim(); // TODO: implement actions
             final String prime = xmlHelper.getString("prime", billTextNode).trim();
             final String coprime = xmlHelper.getString("co-prime", billTextNode).trim();
             final String multi = xmlHelper.getString("multi", billTextNode).trim();
             Bill baseBill = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), new BillId(sponsorhse+
                     sponsorno,sessyr), sobiFragment);


             Pattern rulesSponsorPattern =
                     Pattern.compile("(RULES (?:COM ))|(BUDGET BILL)?\\(?([a-zA-Z-' ]+)\\)?(.*)");
             Matcher rules = rulesSponsorPattern.matcher(prime);
             rules.find();
             if(rules.group().contains("RULES ")){
                 rules.find();
                 ruleSponsorProcess(baseBill,rules.group().trim());
                 baseBill.getAmendment(baseBill.getActiveVersion()).setCoSponsors(multicoSponsorProcess(baseBill,coprime));
                 logger.info(baseBill.getAmendment(baseBill.getActiveVersion()).getCoSponsors().toString());
             }else if(rules.group().contains("BUDGET BILL ")){
                 budgetSponsorProcess(baseBill);
             }else{
                 baseBill.getAmendment(baseBill.getActiveVersion()).setCoSponsors(multicoSponsorProcess(baseBill,coprime));
                 baseBill.getAmendment(baseBill.getActiveVersion()).setCoSponsors(multicoSponsorProcess(baseBill,multi));
                 logger.info(baseBill.getAmendment(baseBill.getActiveVersion()).getMultiSponsors().toString());
                 logger.info(baseBill.getSponsor().toString());
             }
         }catch (IOException | SAXException | XPathExpressionException e) {
             throw new ParseError("Error While Parsing AnActXML", e);
         }










/*
        // Get the chamber from the Bill
        Chamber chamber = baseBill.getBillType().getChamber();
        // New Sponsor instance
        BillSponsor billSponsor = new BillSponsor();
        // Format the sponsor line
        sponsorLine = sponsorLine.toUpperCase().trim();
        // Check for RULES sponsors
        if (sponsorLine.startsWith("RULES")) {
            billSponsor.setRules(true);
            Matcher rules = rulesSponsorPattern.matcher(sponsorLine);
            if (!"RULES COM".equals(sponsorLine) && rules.matches()) {
                sponsorLine = rules.group(1) + ((rules.group(2) != null) ? rules.group(2) : "");
                billSponsor.setMember(getMemberFromShortName(sponsorLine, baseBill.getSession(), chamber));
            }
        }
        // Budget bills don't have a specific sponsor
        else if (sponsorLine.startsWith("BUDGET")) {
            billSponsor.setBudget(true);
        }
        // Apply the sponsor by looking up the member
        else {
            // In rare cases multiple sponsors can be listed on a single line. We can handle this
            // by setting the first contact as the sponsor, and subsequent ones as additional sponsors.
            if (sponsorLine.contains(",")) {
                List<String> sponsors = Lists.newArrayList(
                        Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sponsorLine));
                if (!sponsors.isEmpty()) {
                    sponsorLine = sponsors.remove(0);
                    for (String sponsor : sponsors) {
                        baseBill.getAdditionalSponsors().add(getMemberFromShortName(sponsor, baseBill.getSession(), chamber));
                    }
                }
            }
            // Set the member into the sponsor instance
            billSponsor.setMember(getMemberFromShortName(sponsorLine, baseBill.getSession(), chamber));
        }
        baseBill.setSponsor(billSponsor);
        */
    }

    public void budgetSponsorProcess(Bill baseBill){
        BillSponsor billSponsor= new BillSponsor();
        billSponsor.setBudget(true);
    }

    public void ruleSponsorProcess(Bill baseBill,String primeName){

    }

    public List<SessionMember> multicoSponsorProcess(Bill baseBill,String sponsors) {
        Pattern shortNamePattern = Pattern.compile("\\(?([a-zA-Z-' ]+)\\)?(.*)");
        List<String> shortNames = Lists.newArrayList(
                Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sponsors.toUpperCase()));
        List<SessionMember> sessionMembers=new ArrayList<>();
        SessionYear session = baseBill.getSession();
        Chamber chamber = baseBill.getBillType().getChamber();
        for (String t : shortNames){
            BillAmendment amendment = baseBill.getAmendment(baseBill.getActiveVersion());
            sessionMembers.add(getMemberFromShortName(t, baseBill.getSession(), chamber));
        }
        return sessionMembers;
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }
}
