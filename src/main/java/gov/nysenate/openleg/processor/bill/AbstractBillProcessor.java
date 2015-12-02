package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.event.BillFieldUpdateEvent;
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

/**
 * The AbstractBillProcessor serves as a base class for actual bill processor implementations to provide unified
 * helper methods to address some of the quirks that are present when processing bill data.
 */
public abstract class AbstractBillProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(BillSobiProcessor.class);

    /** --- Patterns --- */

    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /** The expected format for the first line of the vote memo [V] block data. */
    public static final Pattern voteHeaderPattern = Pattern.compile("Senate Vote    Bill: (.{18}) Date: (.{10}).*");

    /** The expected format for recorded votes in the SobiBlock[V] vote memo blocks; e.g. 'AYE  ADAMS' */
    protected static final Pattern votePattern = Pattern.compile("(Aye|Nay|Abs|Exc|Abd) (.{1,16})");

    /** RULES Sponsors are formatted as RULES COM followed by the name of the sponsor that requested passage. */
    protected static final Pattern rulesSponsorPattern =
        Pattern.compile("RULES (?:COM )?\\(?([a-zA-Z-']+)( [A-Z])?\\)?(.*)");

    /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
    protected static final Pattern sameAsPattern =
        Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?(?: \\/ )?)+)");

    /** The format for program info lines. */
    protected static final Pattern programInfoPattern = Pattern.compile("(\\d+)\\s+(.+)");

    /** --- Constructors --- */

    @PostConstruct
    public void init() {
        initBase();
    }

    /** --- Abstract methods --- */

    /** {@inheritDoc} */
    public abstract SobiFragmentType getSupportedType();

    /**
     * Performs processing of the SOBI bill fragments.
     * @param sobiFragment SobiFragment
     */
    public abstract void process(SobiFragment sobiFragment);

    /**
     * Make sure that the global ingest cache is purged.
     */
    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    /** --- Processing Methods --- */

    /**
     * Un-publishes the specified bill amendment.
     * @param baseBill Bill
     * @param version Version
     * @param fragment SobiFragment
     * @param source String - Indicates the origin of this un-publish request, e.g. restore amend in actions list.
     */
    protected void unpublishBillAmendment(Bill baseBill, Version version, SobiFragment fragment, String source) {
        baseBill.updatePublishStatus(version, new PublishStatus(false, fragment.getPublishedDateTime(), false, source));
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Checks that the base bill's default amendment is published. If it isn't it will be set to published using
     * the source file's published date.
     * @param baseBill Bill
     * @param fragment SobiFragment
     * @param source String - Indicates the origin of this publishing request, e.g. bill info line.
     */
    protected void ensureBaseBillIsPublished(Bill baseBill, SobiFragment fragment, String source) {
        Optional<PublishStatus> pubStatus = baseBill.getPublishStatus(Version.DEFAULT);
        if (!pubStatus.isPresent() || !pubStatus.get().isPublished()) {
            baseBill.updatePublishStatus(Version.DEFAULT, new PublishStatus(true, fragment.getPublishedDateTime(), false, source));
            baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
            setModifiedDateTime(baseBill, fragment);
        }
    }

    /**
     * Adds to the base bill's list of previous session year bill ids.
     * @param baseBill Bill
     * @param prevPrintNo String
     * @param prevSessionYear Integer
     * @param fragment SobiFragment
     */
    protected void addPreviousBillId(Bill baseBill, String prevPrintNo, Integer prevSessionYear, SobiFragment fragment) {
        baseBill.addPreviousVersion(new BillId(prevPrintNo, prevSessionYear));
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Sets the law section to the specified amendment version.
     * @param baseBill Bill
     * @param specificVersion Version
     * @param lawSection String
     * @param fragment SobiFragment
     */
    protected void setLawSection(Bill baseBill, Version specificVersion, String lawSection, SobiFragment fragment) {
        if (lawSection == null) lawSection = "";
        baseBill.getAmendment(specificVersion).setLawSection(lawSection.trim());
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Sets the title to the base bill.
     * @param baseBill Bill
     * @param title String
     * @param fragment SobiFragment
     */
    protected void setTitle(Bill baseBill, String title, SobiFragment fragment) {
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
    protected void setSummary(Bill baseBill, String summary, SobiFragment fragment) {
        if (summary == null) summary = "";
        baseBill.setSummary(summary.replace("\n", " ").trim());
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Given a list of bill actions separated by new lines, parse each action to obtain the action's date, text,
     * and chamber, and then analyze the actions to determine a variety of meta data including:
     *
     * Same as bills - if bill was subsituted for/by
     * The active version of the bill
     * The current bill status
     * The milestones list
     * Past committees
     * Publish statuses
     * Stricken status
     *
     * @param baseBill Bill
     * @param version Version
     * @param actionsStr String
     * @param fragment SobiFragment
     * @throws ParseError
     */
    protected void setBillActionsAndDerivedData(Bill baseBill, Version version, String actionsStr,
                                                SobiFragment fragment) throws ParseError {
        // Use the BillActionParser to convert the actions string into objects.
        BillId specificBillId = baseBill.getBaseBillId().withVersion(version);
        List<BillAction> billActions = BillActionParser.parseActionsList(specificBillId, actionsStr);

        // Process the actions even if the list hasn't changed in the event that we modify
        // the actions analyzer
        baseBill.setActions(billActions);
        setModifiedDateTime(baseBill, fragment);

        // Use the BillActionAnalyzer to derive other data from the actions list.
        Optional<PublishStatus> defaultPubStatus = baseBill.getPublishStatus(Version.DEFAULT);
        BillActionAnalyzer analyzer = new BillActionAnalyzer(specificBillId, billActions, defaultPubStatus);
        analyzer.analyze();

        // Apply the results to the bill
        baseBill.setSubstitutedBy(analyzer.getSubstitutedBy().orElse(null));
        baseBill.setActiveVersion(analyzer.getActiveVersion());
        baseBill.setStatus(analyzer.getBillStatus());
        baseBill.setMilestones(analyzer.getMilestones());
        baseBill.setPastCommittees(analyzer.getPastCommittees());
        baseBill.setPublishStatuses(analyzer.getPublishStatusMap());
        analyzer.getSameAsMap().forEach((k, v) -> {
            if (baseBill.hasAmendment(k)) {
                baseBill.getAmendment(k).getSameAs().add(v);
            }
        });
        baseBill.getAmendment(version).setStricken(analyzer.isStricken());
    }

    /**
     * Clears out any same as bill id references from the specified amendment and restores it's uni bill status
     * to false.
     * @param baseBill Bill
     * @param version Version
     * @param fragment SobiFragment
     */
    protected void clearSameAs(Bill baseBill, Version version, SobiFragment fragment) {
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
     * @param fragment SobiFragment
     * @throws ParseError
     */
    protected void processSameAs(Bill baseBill, Version version, String sameAsData, SobiFragment fragment) throws ParseError {
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
     * @param fragment SobiFragment
     */
    protected void setSponsorMemo(Bill baseBill, Version version, String text, SobiFragment fragment) {
        baseBill.getAmendment(version).setMemo(text);
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Sets the full text to the specified amendment. If the bill is also a uni bill, a uni bill sync will
     * be triggered.
     * @param baseBill Bill
     * @param version Version
     * @param text String
     * @param fragment SobiFragment
     */
    protected void setFullText(Bill baseBill, Version version, String text, SobiFragment fragment) {
        BillAmendment billAmendment = baseBill.getAmendment(version);
        billAmendment.setFullText(text);
        if (billAmendment.isUniBill()) {
            syncUniBillText(billAmendment, fragment);
        }
        eventBus.post(
            new BillFieldUpdateEvent(LocalDateTime.now(), billAmendment.getBaseBillId(), BillUpdateField.FULLTEXT));
        setModifiedDateTime(baseBill, fragment);
    }

    /**
     * Sets the modified datetime to the base bill. This modified datetime is not guaranteed to reflect an actual
     * change to the bill or it's amendments since we receive duplicate data from LBDC frequently.
     * @param baseBill Bill
     * @param fragment SoboFragment
     */
    protected void setModifiedDateTime(Bill baseBill, SobiFragment fragment) {
        baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
    }

    /** --- Post Process Methods --- */

    /**
     * Uni-bills share text with their counterpart house. Ensure that the full text of bill amendments that
     * have a uni-bill designator are kept in sync.
     */
    protected void syncUniBillText(BillAmendment billAmendment, SobiFragment sobiFragment) {
        billAmendment.getSameAs().forEach(uniBillId -> {
            Bill uniBill = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), uniBillId, sobiFragment);
            BillAmendment uniBillAmend = uniBill.getAmendment(uniBillId.getVersion());
            // If this is the senate bill amendment and same as is assembly, copy text to the assembly bill amendment.
            if (billAmendment.isSenateBill() && uniBillAmend.isAssemblyBill()) {
                uniBillAmend.setFullText(billAmendment.getFullText());
            }
            // Otherwise copy the senate text to this assembly bill amendment
            else if (billAmendment.isAssemblyBill() && uniBillAmend.isSenateBill() &&
                     !uniBillAmend.getFullText().isEmpty()) {
                billAmendment.setFullText(uniBillAmend.getFullText());
            }
        });
    }

    /**
     * Constructs a BillSponsor via the sponsorLine string and applies it to the bill.
     */
    protected void setBillSponsorFromSponsorLine(Bill baseBill, String sponsorLine, SessionYear sessionYear) throws ParseError {
        // Get the chamber from the Bill
        Chamber chamber = baseBill.getBillType().getChamber();
        // New Sponsor instance
        BillSponsor billSponsor = new BillSponsor();
        // Format the sponsor line
        sponsorLine = sponsorLine.replace("(MS)", "").toUpperCase().trim();
        // Check for RULES sponsors
        if (sponsorLine.startsWith("RULES")) {
            billSponsor.setRules(true);
            Matcher rules = rulesSponsorPattern.matcher(sponsorLine);
            if (!"RULES COM".equals(sponsorLine) && rules.matches()) {
                sponsorLine = rules.group(1) + ((rules.group(2) != null) ? rules.group(2) : "");
                billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber));
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
                        baseBill.getAdditionalSponsors().add(getMemberFromShortName(sponsor, sessionYear, chamber));
                    }
                }
            }
            // Set the member into the sponsor instance
            billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber));
        }
        baseBill.setSponsor(billSponsor);
    }
}