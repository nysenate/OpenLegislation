package gov.nysenate.openleg.processors.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.xml.XmlBillActionAnalyzer;
import gov.nysenate.openleg.updates.bill.BillFieldUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.legislation.bill.BillTextFormat.PLAIN;

/**
 * The AbstractBillProcessor serves as a base class for actual bill processor implementations to provide unified
 * helper methods to address some of the quirks that are present when processing bill data.
 */
public abstract class AbstractBillProcessor extends AbstractLegDataProcessor
{
    /* --- Patterns --- */

    /** RULES Sponsors are formatted as RULES COM followed by the name of the sponsor that requested passage. */
    protected static final Pattern rulesSponsorPattern =
        Pattern.compile("^RULES (?:COM)? *\\(?([A-Z-_']+(?: [A-Z]+)?)\\)?", Pattern.CASE_INSENSITIVE);

    /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
    protected static final Pattern sameAsPattern =
        Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?(?: / )?)+)");

    /** The format for program info lines. */
    protected static final Pattern programInfoPattern = Pattern.compile("(\\d+)\\s+(.+)");

    /* --- Processing Methods --- */

    /**
     * Handles parsing a Session member out of a sobi or xml file
     */
    protected void handlePrimaryMemberParsing(Bill baseBill, String sponsorLine, SessionYear sessionYear) {
        if (sponsorLine.trim().isEmpty()) {
            return;
        }
        // Get the chamber from the Bill
        Chamber chamber = baseBill.getBillType().getChamber();
        // New Sponsor instance
        BillSponsor billSponsor = new BillSponsor();
        // Format the sponsor line
        sponsorLine = sponsorLine.replace("(MS)", "").toUpperCase().trim();

        // Check for RULES sponsors
        if (sponsorLine.startsWith("RULES")) {
            billSponsor.setRules(true);
            Matcher sposorMatch = rulesSponsorPattern.matcher(sponsorLine);
            if (sposorMatch.matches() && !"RULES COM".equals(sponsorLine)) {
                sponsorLine = sposorMatch.group(1);
                billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber));
            } else {
                billSponsor.setMember(null);
            }
        }
        // Budget bills don't have a specific sponsor
        else if (sponsorLine.startsWith("BUDGET")) {
            billSponsor.setBudget(true);
            billSponsor.setMember(null);
        }
        // Bills sponsored by the Redistricting commission don't have a specific sponsor.
        else if (sponsorLine.startsWith("REDISTRICTING")) {
           billSponsor.setRedistricting(true);
           billSponsor.setMember(null);
        }

        else {
            // In rare cases multiple sponsors can be listed on a single line. We can handle this
            // by setting the first contact as the sponsor, and subsequent ones as additional sponsors.
            if (sponsorLine.contains(",")) {
                List<String> sponsors = Lists.newArrayList(
                        Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sponsorLine));
                if (!sponsors.isEmpty()) {
                    sponsorLine = sponsors.remove(0);
                    for (String sponsor : sponsors) {
                        baseBill.getAdditionalSponsors().add(getMemberFromShortName(sponsor, sessionYear, chamber));
                    }
                }
            }

            // Set the member into the sponsor instance
            billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber));
        }
        baseBill.setSponsor(billSponsor);
    }

    /**
     * Sets the law section to the specified amendment version.
     * @param baseBill Bill
     * @param specificVersion Version
     * @param lawSection String
     * @param fragment LegDataFragment
     */
    protected void setLawSection(Bill baseBill, Version specificVersion, String lawSection, LegDataFragment fragment) {
        if (lawSection == null) lawSection = "";
        baseBill.getAmendment(specificVersion).setLawSection(lawSection.trim());
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Sets the title to the base bill.
     * @param baseBill Bill
     * @param title String
     * @param fragment LegDataFragment
     */
    protected void setTitle(Bill baseBill, String title, LegDataFragment fragment) {
        if (title == null) title = "";
        baseBill.setTitle(title.replace("\n", " ").trim());
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Sets the summary to the base bill.
     * @param baseBill Bill
     * @param summary String
     * @param fragment SoboFragment
     */
    protected void setSummary(Bill baseBill, String summary, LegDataFragment fragment) {
        if (summary == null) summary = "";
        baseBill.setSummary(summary.replace("\n", " ").trim());
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Applies information to bill events; replaces existing information in full.
     * Events are uniquely identified by text/date/sequenceNo/bill.
     *
     * Also parses bill events to apply several other bits of meta data to bills (see examples)
     *
     * Examples
     * --------------------------------------------------------------------
     * Same as             | 406/11/14 SUBSTITUTED BY A9504
     * --------------------------------------------------------------------
     * Stricken            | 403/10/14 RECOMMIT, ENACTING CLAUSE STRICKEN
     * --------------------------------------------------------------------
     * Current committee   | 406/21/13 COMMITTED TO RULES
     * --------------------------------------------------------------------
     *
     * There are currently no checks for the action list starting over again which
     * could lead back to back action blocks for a bill to produce a double long list.
     *
     * Bill events cannot be deleted, only replaced.
     *
     * @see BillActionParser
     * @throws ParseError
     */
    protected void parseActions(String data,
                                Bill bill,
                                BillAmendment specifiedAmendment,
                                LegDataFragment fragment,
                                Node xmlActions)
            throws ParseError {
        List<BillAction> billActions;
        if (xmlActions != null) {
            try {
                // try XML parsing the actions
                billActions = BillActionParser.parseActionsListXML(specifiedAmendment.getBillId(), xmlActions);
            } catch (ParseError e) {
                // Fallback to old behavior and use the BillActionParser to convert the actions string into objects.
                billActions = BillActionParser.parseActionsList(specifiedAmendment.getBillId(), data);
            }
        }
        else {
            // Fallback to old behavior and use the BillActionParser to convert the actions string into objects.
            billActions = BillActionParser.parseActionsList(specifiedAmendment.getBillId(), data);
        }

        bill.setActions(billActions);
        // Use the BillActionAnalyzer to derive other data from the actions list.
        Optional<PublishStatus> defaultPubStatus = bill.getPublishStatus(Version.ORIGINAL);
        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(specifiedAmendment.getBillId(), billActions, defaultPubStatus);
        analyzer.analyze();

        addAnyMissingAmendments(bill, billActions);

        // Apply the results to the bill

        final Version initialAV = bill.getActiveVersion();
        bill.setActiveVersion(analyzer.getActiveVersion());
        // If there is a new active amendment, transfer sponsors from the previous active amendment
        if (initialAV != bill.getActiveVersion()) {
            BillAmendment initialActiveAmend = bill.getAmendment(initialAV);
            BillAmendment newActiveAmend = bill.getActiveAmendment();
            newActiveAmend.setCoSponsors(initialActiveAmend.getCoSponsors());
            newActiveAmend.setMultiSponsors(initialActiveAmend.getMultiSponsors());
        }
        bill.setSubstitutedBy(analyzer.getSubstitutedBy());
        bill.setStatus(analyzer.getBillStatus());
        bill.setMilestones(analyzer.getMilestones());
        bill.setPastCommittees(analyzer.getPastCommittees());
        bill.setPublishStatuses(analyzer.getPublishStatusMap());
        // Ensure that amendments exist for all versions in the publish status map
        bill.getAmendPublishStatusMap().keySet().forEach(version ->
                getOrCreateBaseBill(
                        bill.getBaseBillId().withVersion(version),
                        fragment)
        );
        analyzer.getSameAsMap().forEach((k, v) -> {
            if (bill.hasAmendment(k)) {
                bill.getAmendment(k).setSameAs(Sets.newHashSet(v));
            }
        });
        specifiedAmendment.setStricken(analyzer.isStricken());
    }

    /**
     * Sets the modified datetime to the base bill. This modified datetime is not guaranteed to reflect an actual
     * change to the bill or it's amendments since we receive duplicate data from LBDC frequently.
     * @param baseBill Bill
     * @param fragment SoboFragment
     */
    protected void setModifiedDateTime(Bill baseBill, LegDataFragment fragment) {
        baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
    }

    /* --- Post Process Methods --- */

    /**
     * Uni-bills share text with their counterpart house. Ensure that the full text of bill amendments that
     * have a uni-bill designator are kept in sync.
     */
    protected void syncUniBillText(BillAmendment billAmendment, LegDataFragment legDataFragment) {
        billAmendment.getSameAs().forEach(uniBillId -> {
            Bill uniBill = getOrCreateBaseBill(uniBillId, legDataFragment);
            BillAmendment uniBillAmend = uniBill.getAmendment(uniBillId.getVersion());
            BaseBillId updatedBillId = null;
            // If this is the senate bill amendment, copy text to the assembly bill amendment
            if (billAmendment.getBillType().getChamber().equals(Chamber.SENATE)) {
                copyBillTexts(billAmendment, uniBillAmend);
                updatedBillId = uniBillAmend.getBaseBillId();
            }
            // Otherwise copy the text to this assembly bill amendment
            else if (StringUtils.isNotBlank(uniBillAmend.getFullText(PLAIN))) {
                copyBillTexts(uniBillAmend, billAmendment);
                updatedBillId = billAmendment.getBaseBillId();
            }
            if (updatedBillId != null) {
                eventBus.post(new BillFieldUpdateEvent(LocalDateTime.now(),
                        updatedBillId, BillUpdateField.FULLTEXT));
            }
        });
    }

    /**
     * After the BillActionAnalyzer updates the actions with the proper amendment version, the baseBill must be updated
     * with those changes
     * @param baseBill
     * @param billActions
     */
    protected void addAnyMissingAmendments(Bill baseBill, List<BillAction> billActions ) {
        for (BillAction action: billActions) {
            Version actionVersion = action.getBillId().getVersion();
            if (!baseBill.hasAmendment(actionVersion)) {
                BillAmendment baseAmendment = new BillAmendment(baseBill.getBaseBillId(), actionVersion);
                baseBill.addAmendment(baseAmendment);
            }
        }
    }

    private void copyBillTexts(BillAmendment sourceAmend, BillAmendment destAmend) {
        destAmend.setBillText(sourceAmend.getBillText());
    }
}