package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
import gov.nysenate.openleg.service.bill.event.BillFieldUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.bill.BillTextFormat.PLAIN;

/**
 * The AbstractBillProcessor serves as a base class for actual bill processor implementations to provide unified
 * helper methods to address some of the quirks that are present when processing bill data.
 */
public abstract class AbstractBillProcessor extends AbstractDataProcessor implements LegDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(BillSobiProcessor.class);

    /* --- Patterns --- */

    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /** The expected format for the first line of the vote memo [V] block data. */
    public static final Pattern voteHeaderPattern = Pattern.compile("Senate Vote    Bill: (.{18}) Date: (.{10}).*");

    /** The expected format for recorded votes in the SobiBlock[V] vote memo blocks; e.g. 'AYE  ADAMS' */
    protected static final Pattern votePattern = Pattern.compile("(Aye|Nay|Abs|Exc|Abd) (.{1,16})");

    /** RULES Sponsors are formatted as RULES COM followed by the name of the sponsor that requested passage. */
    protected static final Pattern rulesSponsorPattern =
        Pattern.compile("^RULES (?:COM)? *\\(?([A-Z-_']+(?: [A-Z]+)?)\\)?", Pattern.CASE_INSENSITIVE);

    /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
    protected static final Pattern sameAsPattern =
        Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?(?: \\/ )?)+)");

    /** The format for program info lines. */
    protected static final Pattern programInfoPattern = Pattern.compile("(\\d+)\\s+(.+)");

    /* --- Constructors --- */

    @PostConstruct
    public void init() {
        initBase();
    }

    /* --- Abstract methods --- */

    /** {@inheritDoc} */
    public abstract LegDataFragmentType getSupportedType();

    /**
     * Performs processing of the SOBI bill fragments.
     * @param legDataFragment LegDataFragment
     */
    public abstract void process(LegDataFragment legDataFragment);

    /**
     * Make sure that the global ingest cache is purged.
     */
    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    /* --- Processing Methods --- */

    /**
     * Handles parsing a Session member out of a sobi or xml file
     */
    protected void handlePrimaryMemberParsing(Bill baseBill, String sponsorLine, SessionYear sessionYear) {
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
     * Un-publishes the specified bill amendment.
     * @param baseBill Bill
     * @param version Version
     * @param fragment LegDataFragment
     * @param source String - Indicates the origin of this un-publish request, e.g. restore amend in actions list.
     */
    protected void unpublishBillAmendment(Bill baseBill, Version version, LegDataFragment fragment, String source) {
        baseBill.updatePublishStatus(version, new PublishStatus(false, fragment.getPublishedDateTime(), false, source));
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Checks that the base bill's default amendment is published. If it isn't it will be set to published using
     * the source file's published date.
     * @param baseBill Bill
     * @param fragment LegDataFragment
     * @param source String - Indicates the origin of this publishing request, e.g. bill info line.
     */
    protected void ensureBaseBillIsPublished(Bill baseBill, LegDataFragment fragment, String source) {
        Optional<PublishStatus> pubStatus = baseBill.getPublishStatus(Version.ORIGINAL);
        if (!pubStatus.isPresent() || !pubStatus.get().isPublished()) {
            baseBill.updatePublishStatus(Version.ORIGINAL, new PublishStatus(true, fragment.getPublishedDateTime(), false, source));
            baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
            setModifiedDateTime(baseBill, fragment);
        }
    }

    /**
     * Adds to the base bill's list of previous session year bill ids.
     * @param baseBill Bill
     * @param prevPrintNo String
     * @param prevSessionYear Integer
     * @param fragment LegDataFragment
     */
    protected void addPreviousBillId(Bill baseBill, String prevPrintNo, Integer prevSessionYear, LegDataFragment fragment) {
        baseBill.setDirectPreviousVersion(new BillId(prevPrintNo, prevSessionYear));
        setModifiedDateTime(baseBill, fragment);
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
                                LegDataFragment fragment)
            throws ParseError {
        // Use the BillActionParser to convert the actions string into objects.
        List<BillAction> billActions = BillActionParser.parseActionsList(specifiedAmendment.getBillId(), data);
        bill.setActions(billActions);
        // Use the BillActionAnalyzer to derive other data from the actions list.
        Optional<PublishStatus> defaultPubStatus = bill.getPublishStatus(Version.ORIGINAL);
        BillActionAnalyzer analyzer = new BillActionAnalyzer(specifiedAmendment.getBillId(), billActions, defaultPubStatus);
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
        bill.setSubstitutedBy(analyzer.getSubstitutedBy().orElse(null));
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
     * Clears out any same as bill id references from the specified amendment and restores it's uni bill status
     * to false.
     * @param baseBill Bill
     * @param version Version
     * @param fragment LegDataFragment
     */
    protected void clearSameAs(Bill baseBill, Version version, LegDataFragment fragment) {
        baseBill.getAmendment(version).getSameAs().clear();
        baseBill.getAmendment(version).setUniBill(false);
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * For the specified amendment parse the same as data to obtain a list of same as bill id references.
     * If the uni bill flag is detected, the bill's uni bill status will be set to true and a uni bill
     * sync will be triggered.
     * @param baseBill Bill
     * @param version Version
     * @param sameAsData String
     * @param fragment LegDataFragment
     * @throws ParseError
     */
    protected void processSameAs(Bill baseBill, Version version, String sameAsData, LegDataFragment fragment) throws ParseError {
        Matcher sameAsMatcher = sameAsPattern.matcher(sameAsData);
        BillAmendment billAmendment = baseBill.getAmendment(version);
        if (sameAsMatcher.find()) {
            // Sometimes we get S 1797-A / A 4768-A for uni bills, which should convert to S 1797-A, A 4768-A
            String matches = sameAsMatcher.group(2).replaceAll(" / ", ", ");
            List<String> sameAsMatches = new ArrayList<>(Arrays.asList(matches.split(", ")));
            // We're adding the same as bills to the existing list. Same as bills are explicitly cleared.
            billAmendment.getSameAs().addAll(sameAsMatches.stream()
                    .map(sameAs -> new BillId(sameAs.replace("-", "").replace(" ", ""), baseBill.getSession()))
                    .collect(Collectors.toList()));
            // Check for uni-bill and sync
            if (sameAsMatcher.group(1) != null && !sameAsMatcher.group(1).isEmpty()) {
                billAmendment.setUniBill(true);
                syncUniBillText(billAmendment, fragment);
            }
            setModifiedDateTime(baseBill, fragment);
        }
        else {
            throw new ParseError("sameAsPattern not matched: " + sameAsData);
        }
    }

    /**
     * Sets the sponsor memo to the specified amendment.
     * @param baseBill Bill
     * @param version Version
     * @param text String
     * @param fragment LegDataFragment
     */
    protected void setSponsorMemo(Bill baseBill, Version version, String text, LegDataFragment fragment) {
        baseBill.getAmendment(version).setMemo(text);
        setModifiedDateTime(baseBill, fragment);
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
        for (BillTextFormat format : sourceAmend.getFullTextFormats()) {
            destAmend.setFullText(format, sourceAmend.getFullText(format));
        }
    }
}