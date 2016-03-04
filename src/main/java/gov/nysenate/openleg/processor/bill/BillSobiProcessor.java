package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sobi.SobiBlock;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sobi.SobiLineType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.event.BillFieldUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The BillProcessor parses bill sobi fragments, applies bill updates, and persists into the backing
 * store via the service layer. This implementation is fairly lengthy due to the various types of data that
 * are applied to the bills via these fragments.
 */
@Service
public class BillSobiProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(BillSobiProcessor.class);

    /** --- Patterns --- */

    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /** The expected format for the first line of the vote memo [V] block data. */
    public static final Pattern voteHeaderPattern = Pattern.compile("Senate Vote    Bill: (.{18}) Date: (.{10}).*");

    /** The expected format for recorded votes in the SobiBlock[V] vote memo blocks; e.g. 'AYE  ADAMS' */
    protected static final Pattern votePattern = Pattern.compile("(Aye|Nay|Abs|Exc|Abd) (.{1,16})");

    /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
    protected static final Pattern sameAsPattern =
        Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?)+)");

    /** The expected format for Bill Info [1] block data. */
    public static final Pattern billInfoPattern =
        Pattern.compile("(.{20})([0-9]{5}[ A-Z])(.{33})([ A-Z][0-9]{5}[ `\\-A-Z0-9])(.{8})(.*)");

    /** RULES Sponsors are formatted as RULES COM followed by the name of the sponsor that requested passage. */
    protected static final Pattern rulesSponsorPattern =
        Pattern.compile("RULES (?:COM )?\\(?([a-zA-Z-']+)( [A-Z])?\\)?(.*)");

    /** The format for program info lines. */
    protected static final Pattern programInfoPattern = Pattern.compile("(\\d+)\\s+(.+)");

    /** Used to tokenize chunks of veto/approval messages by newlines that follow an end or delete line */
    protected static final String vetoApprovalSplitter =
        "(?<=00000.SO DOC (?:VETO\\d{4}|APPR\\d{3}\\s)\\s{8}(?:\\*END\\*.{3}|\\*DELETE\\*).{42})\\n";

    /** --- Constructors --- */

    public BillSobiProcessor() {}

    @PostConstruct
    public void init() {
        initBase();
    }

    /** --- Implementation methods --- */

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.BILL;
    }

    /**
     * Performs processing of the SOBI bill fragments.
     *
     * @param sobiFragment SobiFragment
     */
    @Override
    public void process(SobiFragment sobiFragment) {
        LocalDateTime date = sobiFragment.getPublishedDateTime();
        List<SobiBlock> blocks = sobiFragment.getSobiBlocks();
        logger.info("Processing " + sobiFragment.getFragmentId() + " with (" + blocks.size() + ") blocks.");
        DataProcessUnit unit = createProcessUnit(sobiFragment);
        for (SobiBlock block : blocks) {
            String data = block.getData();
            BillId billId = block.getBillId();
            Bill baseBill = getOrCreateBaseBill(sobiFragment.getPublishedDateTime(), billId, sobiFragment);
            Version specifiedVersion = billId.getVersion();
            BillAmendment specifiedAmendment = baseBill.getAmendment(specifiedVersion);
            BillAmendment activeAmendment = baseBill.getActiveAmendment();
            logger.debug("Updating {} - {} | Line {}-{}", billId, block.getType(),
                                                          block.getStartLineNo(), block.getEndLineNo());
            try {
                switch (block.getType()) {
                    case BILL_INFO: applyBillInfo(data, baseBill, specifiedAmendment, date, unit); break;
                    case LAW_SECTION: applyLawSection(data, baseBill, specifiedAmendment, date); break;
                    case TITLE: applyTitle(data, baseBill, date); break;
                    case BILL_EVENT: applyBillActions(data, baseBill, specifiedAmendment); break;
                    case SAME_AS: applySameAs(data, specifiedAmendment, sobiFragment, unit); break;
                    case SPONSOR: applySponsor(data, baseBill, specifiedAmendment, date); break;
                    case CO_SPONSOR: applyCosponsors(data, baseBill); break;
                    case MULTI_SPONSOR: applyMultisponsors(data, baseBill); break;
                    case PROGRAM_INFO: applyProgramInfo(data, baseBill, date); break;
                    case ACT_CLAUSE: applyActClause(data, specifiedAmendment); break;
                    case LAW: applyLaw(data, baseBill, specifiedAmendment, date); break;
                    case SUMMARY: applySummary(data, baseBill, date); break;
                    case SPONSOR_MEMO:
                    case RESOLUTION_TEXT:
                    case TEXT: applyText(data, specifiedAmendment, date, block.getType(), sobiFragment); break;
                    case VETO_APPROVE_MEMO: applyVetoApprovalMessage(data, baseBill, date); break;
                    case VOTE_MEMO: applyVoteMemo(data, specifiedAmendment, date); break;
                    default: {
                        throw new ParseError("Invalid Line Code " + block.getType());
                    }
                }
            }
            catch (ParseError ex) {
                logger.error("Bill Processing Parse Error!", ex);
                unit.addException("Bill Processing Parse Error",  ex);
            }
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, sobiFragment);

            if (billIngestCache.exceedsCapacity()) {
                logger.info("Flushing bill ingest cache with {} bills!", billIngestCache.getSize());
                flushBillUpdates();
            }
        }
        // Notify the data processor that a bill fragment has finished processing
        postDataUnitEvent(unit);

        // Flush cache after each fragment when doing incremental updates
        if (!env.isSobiBatchEnabled()) {
            flushBillUpdates();
        }
    }

    /**
     * Make sure that the global ingest cache is purged.
     */
    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    /** --- Processing Methods --- */

    /**
     * Apply information from the Bill Info block. Fully replaces existing information.
     * Currently fills in blank sponsors (doesn't replace existing sponsor information)
     * and previous version information (which has known issues).
     * A DELETE code sent with this block causes the bill to be unpublished.
     *
     * Examples
     * -----------------------------------------------------------------------------------------------------------
     * Nothing           | 1                    00000                                   00000              0000
     * -----------------------------------------------------------------------------------------------------------
     * Delete            | 1DELETE              00000                                   00000
     * -----------------------------------------------------------------------------------------------------------
     * Sponsor           | 1YOUNG               00000 MachiasVolunteerFireDept.100thAnn 00000 91989011
     * -----------------------------------------------------------------------------------------------------------
     * Prev Version      | 1                    00000                                  S07213              2010
     * -----------------------------------------------------------------------------------------------------------
     *
     * @throws ParseError
     */
    private void applyBillInfo(String data, Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime date,
                               DataProcessUnit unit) throws ParseError {
        Version version = specifiedAmendment.getVersion();
        if (data.startsWith("DELETE")) {
            // Un-publish the specified amendment.
            baseBill.updatePublishStatus(version, new PublishStatus(false, date, false, data));
            return;
        }
        else {
            // Set the publish status of the base amendment only if it has not been set or is currently un-published.
            if (specifiedAmendment.isBaseVersion()) {
                Optional<PublishStatus> pubStatus = baseBill.getPublishStatus(version);
                if (!pubStatus.isPresent() || !pubStatus.get().isPublished()) {
                    baseBill.updatePublishStatus(version, new PublishStatus(true, date, false, data));
                }
            }
        }
        Matcher billData = billInfoPattern.matcher(data);
        if (billData.find()) {
            String sponsor = billData.group(1).trim();
            if (!StringUtils.isEmpty(sponsor) && baseBill.getSponsor() == null) {
                // Apply the sponsor from bill info when the sponsor has not yet been set.
                setBillSponsorFromSponsorLine(baseBill, sponsor, baseBill.getSession());
                baseBill.setModifiedDateTime(date);
            }
            String prevPrintNo = billData.group(4).trim();
            String prevSessionYearStr = billData.group(6).trim();
            if (!prevSessionYearStr.equals("0000") && !prevPrintNo.equals("00000")) {
                try {
                    Integer prevSessionYear = Integer.parseInt(prevSessionYearStr);
                    baseBill.addPreviousVersion(new BillId(prevPrintNo, prevSessionYear));
                    baseBill.setModifiedDateTime(date);
                }
                catch (NumberFormatException ex) {
                    unit.addMessage("Failed to parse previous session year from Bill Info line: " + prevSessionYearStr);
                }
            }
        }
        else {
            throw new ParseError("Bill Info Pattern not matched by " + data);
        }
    }

    /**
     * Applies data to law section. Fully replaces existing data.
     * Cannot be deleted, only replaced.
     *
     * Examples
     * ------------------------------------------------------
     * Law Section   | 2Volunteer Firefighters' Benefit Law
     * ------------------------------------------------------
     *
     * @throws ParseError
     */
    private void applyLawSection(String data, Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime date) {
        specifiedAmendment.setLawSection(data.trim());
        baseBill.setModifiedDateTime(date);
    }

    /**
     * Applies the data to the bill title. Strips out all whitespace formatting and replaces
     * existing content in full. The bill title is a required field and cannot be deleted, only replaced.
     *
     * Examples
     * ---------------------------------------------------------------------------------------------------------
     * Title  | 3Report on the impact of a tax deduction for expenses attributed to the adoption of a child in
     *        | 3foster care
     * ---------------------------------------------------------------------------------------------------------
     *
     * @throws ParseError
     */
    private void applyTitle(String data, Bill baseBill, LocalDateTime date) {
        baseBill.setTitle(data.replace("\n", " ").trim());
        baseBill.setModifiedDateTime(date);
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
    private void applyBillActions(String data, Bill baseBill, BillAmendment specifiedAmendment)
                                throws ParseError {
        // Use the BillActionParser to convert the actions string into objects.
        List<BillAction> billActions = BillActionParser.parseActionsList(specifiedAmendment.getBillId(), data);
        baseBill.setActions(billActions);
        // Use the BillActionAnalyzer to derive other data from the actions list.
        Optional<PublishStatus> defaultPubStatus = baseBill.getPublishStatus(Version.DEFAULT);
        BillActionAnalyzer analyzer = new BillActionAnalyzer(specifiedAmendment.getBillId(), billActions, defaultPubStatus);
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
                baseBill.getAmendment(k).setSameAs(Sets.newHashSet(v));
            }
        });
        specifiedAmendment.setStricken(analyzer.isStricken());
    }

    /**
     * Applies the 'same as' bill id for the given amendment. Also indicates uni-bill status.
     * Allows for multiple same as bills.
     *
     * Examples:
     * ----------------------------------------------------------------------
     *  Same as                     | 2013A08586 5Same as S 6385
     * ----------------------------------------------------------------------
     *  Same as with uni bill flag  | 2013S03308B5Same as Uni. A 4117-B
     * ----------------------------------------------------------------------
     *  Multiple same as            | 2013A00837 5Same as S 1347, A 1862
     * ----------------------------------------------------------------------
     *  Delete same as              | 2013A10108 5DELETE
     *                              | 2009S51106 5No same as
     * ----------------------------------------------------------------------
     */
    private void applySameAs(String data, BillAmendment specifiedAmendment, SobiFragment fragment, DataProcessUnit unit) {
        if (data.trim().equalsIgnoreCase("No same as") || data.trim().equalsIgnoreCase("DELETE")) {
            specifiedAmendment.getSameAs().clear();
            specifiedAmendment.setUniBill(false);
        }
        else {
            Matcher sameAsMatcher = sameAsPattern.matcher(data);
            if (sameAsMatcher.find()) {
                specifiedAmendment.getSameAs().clear();
                List<String> sameAsMatches = new ArrayList<>(Arrays.asList(sameAsMatcher.group(2).split(", ")));
                for (String sameAs : sameAsMatches) {
                    specifiedAmendment.getSameAs().add(new BillId(sameAs.replace("-", "").replace(" ",""),
                                                       specifiedAmendment.getSession()));
                }
                // Check for uni-bill and sync
                if (sameAsMatcher.group(1) != null && !sameAsMatcher.group(1).isEmpty()) {
                    specifiedAmendment.setUniBill(true);
                    syncUniBillText(specifiedAmendment, fragment);
                }
            }
            else {
                unit.addMessage("sameAsPattern not matched: " + data);
            }
        }
    }

    /**
     * Applies data to bill sponsor. Fully replaces existing sponsor information. Because
     * this is a one line field the block parser is sometimes tricked into combining consecutive
     * blocks. Make sure to process the data 1 line at a time.
     *
     * Examples
     * ----------------------------------------
     * Sponsor        | 6MARCHIONE
     * ----------------------------------------
     * Rules Sponsor  | 6RULES COM McDonald
     * ----------------------------------------
     * Delete         | 6DELETE
     * ----------------------------------------
     *
     * A delete in these field removes all sponsor information.
     */
    private void applySponsor(String data, Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime date) throws ParseError {
        // Apply the lines in order given as each represents its own "block"
        SessionYear sessionYear = baseBill.getSession();

        for(String line : data.split("\n")) {
            line = line.toUpperCase().trim();
            if (line.equals("DELETE")) {
                baseBill.setSponsor(null);
                specifiedAmendment.setCoSponsors(new ArrayList<>());
                specifiedAmendment.setMultiSponsors(new ArrayList<>());
            }
            else {
                setBillSponsorFromSponsorLine(baseBill, line, sessionYear);
            }
        }
        baseBill.setModifiedDateTime(date);
    }

    /**
     * Applies data to bill co-sponsors. Expects a comma separated list and fully replaces
     * existing co-sponsor information. The delete code is sent through the sponsor block.
     *
     * Examples
     * -------------------------------------------
     * Cosponsors   | 7BALL, GRISANTI, RITCHIE
     * -------------------------------------------
     * Nothing      | 7
     * -------------------------------------------
     */
    private void applyCosponsors(String data, Bill baseBill) throws ParseError {
        List<SessionMember> coSponsors = new ArrayList<>();
        SessionYear session = baseBill.getSession();
        Chamber chamber = baseBill.getBillType().getChamber();
        List<String> badCoSponsors = new ArrayList<>();
        for (String coSponsor : data.replace("\n", " ").split(",")) {
            coSponsor = coSponsor.trim();
            if (!coSponsor.isEmpty()) {
                SessionMember member = getMemberFromShortName(coSponsor, session, chamber);
                if (member != null) {
                    coSponsors.add(member);
                } else {
                    badCoSponsors.add(coSponsor);
                }
            }
        }
        // The cosponsor info is always sent for the base bill version.
        // We can use the currently active amendment instead, plus any as yet unpublished amendments that follow.
        BillAmendment activeAmendment = baseBill.getActiveAmendment();
        activeAmendment.setCoSponsors(coSponsors);
        Version.after(activeAmendment.getVersion()).stream()
                .filter(baseBill::hasAmendment)
                .map(baseBill::getAmendment)
                .forEach(amend -> amend.setCoSponsors(coSponsors));

        if (!badCoSponsors.isEmpty()) {
            throw new ParseError(String.format("Could not parse %s co sponsors: %s",
                    baseBill.getBaseBillId(), StringUtils.join(badCoSponsors, ", ")));
        }
    }

    /**
     * Applies data to bill multi-sponsors. Expects a comma separated list and fully replaces
     * existing information. Delete code is sent through the sponsor block.
     *
     * Examples
     * ----------------------------------------------
     * Multi-sponsors | 8Barclay, McKevitt, Thiele
     * ----------------------------------------------
     * Nothing        | 8
     * ----------------------------------------------
     */
    private void applyMultisponsors(String data, Bill baseBill) throws ParseError {
        List<SessionMember> multiSponsors = new ArrayList<>();
        SessionYear session = baseBill.getSession();
        Chamber chamber = baseBill.getBillType().getChamber();
        List<String> badMultiSponsors = new ArrayList<>();
        for (String multiSponsor : data.replace("\n", " ").split(",")) {
            multiSponsor = multiSponsor.trim();
            if (!multiSponsor.isEmpty()) {
                SessionMember member = getMemberFromShortName(multiSponsor, session, chamber);
                if (member != null) {
                    multiSponsors.add(member);
                } else {
                    badMultiSponsors.add(multiSponsor);
                }
            }
        }

        // The multisponsor info is always set for the base amendment
        // We can use the currently active amendment instead, plus any as yet unpublished amendments that follow.
        BillAmendment activeAmendment = baseBill.getActiveAmendment();
        activeAmendment.setMultiSponsors(multiSponsors);
        Version.after(activeAmendment.getVersion()).stream()
                .filter(baseBill::hasAmendment)
                .map(baseBill::getAmendment)
                .forEach(amend -> amend.setMultiSponsors(multiSponsors));

        if (!badMultiSponsors.isEmpty()) {
            throw new ParseError(String.format("Could not parse %s multi sponsors: %s",
                    baseBill.getBaseBillId(), StringUtils.join(multiSponsors, ", ")));
        }
    }

    /**
     * Applies data to the ACT TO clause. Fully replaces existing data.
     * DELETE code removes existing ACT TO clause.
     *
     * Examples
     * ------------------------------------------------------------------------------
     * Act to   | AAN ACT to amend the education law, in relation to transfer credit
     * ------------------------------------------------------------------------------
     * Delete   | ADELETE*
     * ------------------------------------------------------------------------------
     */
    private void applyActClause(String data, BillAmendment specifiedAmendment) {
        if (data.trim().equals("DELETE")) {
            specifiedAmendment.setActClause("");
        }
        else {
            specifiedAmendment.setActClause(data.replace("\n", " ").trim());
        }
    }

    /**
     * Applies data to bill law. Fully replaces existing information.
     * DELETE code here also deletes the bill summary.
     *
     * Note: The encoding of the file may mess up the § (section) characters.
     *
     * Examples
     * -------------------------------------
     * Law     | BAmd §3, Chap 33 of 2002
     * -------------------------------------
     * Delete  | BDELETE
     * -------------------------------------
     */
    private void applyLaw(String data, Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime date) {
        // This is theoretically not safe because a law line *could* start with DELETE
        // We can't do an exact match because B can be multi-line
        if (data.trim().startsWith("DELETE")) {
            specifiedAmendment.setLaw("");
            baseBill.setSummary("");
            baseBill.setModifiedDateTime(date);
        }
        else {
            specifiedAmendment.setLaw(data.replace("\n", " ").trim());
        }
        baseBill.setModifiedDateTime(date);
    }

    /**
     * Applies the data to the bill summary. Strips out all whitespace formatting and replaces
     * existing content in full. Delete codes for this field are sent through the law block.
     *
     * Examples
     * ----------------------------------------------------------------------------------------------------------
     * Multi-line Summary  |  CAllows for reimbursement of transportation costs for emergency care without prior
     *                     |  Cauthorization by the social services official
     * ----------------------------------------------------------------------------------------------------------
     */
    private void applySummary(String data, Bill baseBill, LocalDateTime date) {
        baseBill.setSummary(data.replace("\n", " ").trim());
        baseBill.setModifiedDateTime(date);
    }

    /**
     * Applies sobi block information to a bill resolution or memo text
     * @param data
     * @param billAmendment
     * @param date
     */
    private void applyText(String data, BillAmendment billAmendment, LocalDateTime date, SobiLineType lineType,
                           SobiFragment fragment) throws ParseError {
        BillTextParser billTextParser = new BillTextParser(data, BillTextType.getTypeString(lineType), date);
        String fullText = billTextParser.extractText();
        if (fullText != null) {
            if (lineType == SobiLineType.SPONSOR_MEMO) {
                billAmendment.setMemo(fullText);
            }
            else if (lineType == SobiLineType.RESOLUTION_TEXT || lineType == SobiLineType.TEXT) {
                billAmendment.setFullText(fullText);
                if (billAmendment.isUniBill()) {
                    syncUniBillText(billAmendment, fragment);
                }
                eventBus.post(new BillFieldUpdateEvent(LocalDateTime.now(),
                        billAmendment.getBaseBillId(), BillUpdateField.FULLTEXT));
            }
        }
    }

    /**
     * Parses a chunk of memo into either veto or approval messages
     * @param data
     * @param baseBill
     * @param date
     * @throws ParseError
     */
    private void applyVetoApprovalMessage(String data, Bill baseBill, LocalDateTime date) throws ParseError {
        for (String vetoApprovalChunk : data.split(vetoApprovalSplitter)) {
            if (vetoApprovalChunk.startsWith("00000.SO DOC APPR")) {       // Approval message header
                applyApprovalMessageText(vetoApprovalChunk, baseBill, date);
            } else if (vetoApprovalChunk.startsWith("00000.SO DOC VETO")) {  // Veto message header
                applyVetoMessageText(vetoApprovalChunk, baseBill, date);
            } else {
                throw new ParseError("Unrecognized veto/approval memo header");
            }
        }
    }

    /**
     * Constructs a veto message object by parsing the memo
     * @throws ParseError
     */
    private void applyVetoMessageText(String data, Bill baseBill, LocalDateTime date) throws ParseError{
        VetoMemoParser vetoMemoParser = new VetoMemoParser(data, date);
        vetoMemoParser.extractText();
        if (vetoMemoParser.isDeleted()) {
            baseBill.getVetoMessages().remove(vetoMemoParser.getVetoId());
        } else {
            VetoMessage vetoMessage = vetoMemoParser.getVetoMessage();
            vetoMessage.setSession(baseBill.getSession());
            vetoMessage.setBillId(baseBill.getBaseBillId());
            vetoMessage.setModifiedDateTime(date);
            vetoMessage.setPublishedDateTime(date);

            baseBill.getVetoMessages().put(vetoMessage.getVetoId(), vetoMessage);
        }
    }

    /**
     * Constructs an approval message object by parsing a memo
     * @param data
     * @param baseBill
     * @param date
     * @throws ParseError
     */
    private void applyApprovalMessageText(String data, Bill baseBill, LocalDateTime date) throws ParseError{
        ApprovalMessageParser approvalMessageParser = new ApprovalMessageParser(data, date);
        approvalMessageParser.extractText();
        if (approvalMessageParser.isDeleted()) {
            baseBill.setApprovalMessage(null);
        } else {
            ApprovalMessage approvalMessage = approvalMessageParser.getApprovalMessage();
            approvalMessage.setBillId(baseBill.getActiveAmendment().getBillId());
            approvalMessage.setModifiedDateTime(date);
            approvalMessage.setPublishedDateTime(date);

            baseBill.setApprovalMessage(approvalMessage);
        }
    }

    /**
     * Applies data to bill Program. Fully replaces existing information.
     *
     * Examples
     * -----------------------------------------------------
     * Program Info   | 9020 Office of Court Administration
     * -----------------------------------------------------
     */
    private void applyProgramInfo(String data, Bill baseBill, LocalDateTime date) {
        if (!data.isEmpty()) {
            Matcher programMatcher = programInfoPattern.matcher(data);
            if (programMatcher.find()) {
                baseBill.setProgramInfo(new ProgramInfo(programMatcher.group(2), Integer.parseInt(programMatcher.group(1))));
                baseBill.setModifiedDateTime(date);
            }
        }
    }

    /**
     * Applies information to create or replace a bill vote. Votes are uniquely identified by date/bill.
     * If we have an existing vote on the same date, replace it; otherwise create a new one.
     *
     * Note: Only Floor votes are processed here, Committee votes are handled through the agendas.
     *
     * Examples:
     * -------------------------------------------------------------------------------------------
     * Header     | VSenate Vote    Bill: S6458              Date: 06/18/2014  Aye - 58  Nay - 1
     * -------------------------------------------------------------------------------------------
     * Votes      | Aye  Addabbo          Aye  Avella           Aye  Ball             Aye  Bonacic
     * -------------------------------------------------------------------------------------------
     *
     * Valid vote codes:
     *
     * Nay - Vote against
     * Aye - Vote for
     * Abs - Absent during voting
     * Exc - Excused from voting
     * Abd - Abstained from voting
     *
     * @throws ParseError
     */
    private void applyVoteMemo(String data, BillAmendment specifiedAmendment, LocalDateTime date) throws ParseError {
        // Because sometimes votes are back to back we need to check for headers
        // Example of a double vote entry: SOBI.D110119.T140802.TXT:390
        BillVote vote = null;
        BillId billId = specifiedAmendment.getBillId();
        for (String line : data.split("\n")) {
            Matcher voteHeader = voteHeaderPattern.matcher(line);
            // Start over if we hit a header, sometimes we get back to back entries.
            if (voteHeader.find()) {
                LocalDate voteDate;
                try {
                    voteDate = LocalDate.from(voteDateFormat.parse(voteHeader.group(2)));
                    vote = new BillVote(billId, voteDate, BillVoteType.FLOOR);
                    vote.setModifiedDateTime(date);
                    vote.setPublishedDateTime(date);
                }
                catch (DateTimeParseException ex) {
                    throw new ParseError("voteDateFormat not matched: " + line);
                }
            }
            // Otherwise, build the existing vote
            else if (vote != null) {
                Matcher voteLine = votePattern.matcher(line);
                while (voteLine.find()) {
                    BillVoteCode voteCode;
                    try {
                        voteCode = BillVoteCode.getValue(voteLine.group(1));
                    }
                    catch (IllegalArgumentException ex) {
                        throw new ParseError("No vote code mapping for " + voteLine);
                    }
                    String shortName = voteLine.group(2).trim();
                    // Only senator votes are received. A valid member mapping is required.
                    SessionMember voter = getMemberFromShortName(shortName, billId.getSession(), Chamber.SENATE);
                    vote.addMemberVote(voteCode, voter);
                }
            }
            else {
                throw new ParseError("Hit vote data without a header: " + data);
            }
        }
        specifiedAmendment.updateVote(vote);
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
            // If this is the senate bill amendment, copy text to the assembly bill amendment
            if (billAmendment.getBillType().getChamber().equals(Chamber.SENATE)) {
                uniBillAmend.setFullText(billAmendment.getFullText());
            }
            // Otherwise copy the text to this assembly bill amendment
            else if (!uniBillAmend.getFullText().isEmpty()) {
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