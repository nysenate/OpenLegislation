package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.sobi.SobiBlock;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sobi.SobiLineType;

import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.IngestCache;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.base.SobiProcessor;
import gov.nysenate.openleg.model.bill.BillTextType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
public class BillProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(BillProcessor.class);

    /** Date format found in SobiBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /** The expected format for the first line of the vote memo [V] block data. */
    public static final Pattern voteHeaderPattern = Pattern.compile("Senate Vote    Bill: (.{18}) Date: (.{10}).*");

    /** The expected format for recorded votes in the SobiBlock[V] vote memo blocks; e.g. 'AYE  ADAMS' */
    protected static final Pattern votePattern = Pattern.compile("(Aye|Nay|Abs|Exc|Abd) (.{1,16})");

    /** The expected format for actions recorded in the bill events [4] block. e.g. 02/04/13 Event Text Here */
    protected static final Pattern billEventPattern = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (.*)");

    /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
    protected static final Pattern sameAsPattern =
        Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?)+)");

    /** The expected format for Bill Info [1] block data. */
    public static final Pattern billInfoPattern =
        Pattern.compile("(.{20})([0-9]{5}[ A-Z])(.{33})([ A-Z][0-9]{5}[ `\\-A-Z0-9])(.{8})(.*)");

    /** Pattern for extracting the committee from matching bill events. */
    public static final Pattern committeeEventTextPattern =
        Pattern.compile("(REFERRED|COMMITTED|RECOMMIT) TO ([A-Z, ]*[A-Z]+)\\s?([0-9]+[A-Z]?)?");

    /** Pattern for detecting calendar events in bill action lists. */
    public static final Pattern floorEventTextPattern = Pattern.compile("(REPORT CAL|THIRD READING|RULES REPORT)");

    /** Pattern to detect a bill being delivered/returned from one chamber to another */
    public static final Pattern chamberSwitchEventTextPattern = Pattern.compile("(DELIVERED|RETURNED) TO (SENATE|ASSEMBLY)");

    /** Pattern for extracting the substituting bill printNo from matching bill events. */
    public static final Pattern substituteEventTextPattern = Pattern.compile("SUBSTITUTED (FOR|BY) (.*)");

    /** RULES Sponsors are formatted as RULES COM followed by the name of the sponsor that requested passage. */
    protected static final Pattern rulesSponsorPattern =
            Pattern.compile("RULES (?:COM )?\\(?([a-zA-Z-']+)( [A-Z])?\\)?(.*)");

    /** Pattern to extract bill number and version when in the format 1234A. */
    protected static final String simpleBillRegex = "([0-9]{2,})([ a-zA-Z]?)";

    /** Patterns for bill actions that indicate that the specified bill amendment should be published. */
    protected static final List<Pattern> publishBillEventPatterns = Arrays.asList(
        Pattern.compile("PRINT NUMBER " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? ON THIRD READING " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? ON THIRD READING \\(T\\) " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? AND RECOMMIT(?:TED) TO RULES " + simpleBillRegex)
    );

    /** Patterns for bill actions that indicate that the specified bill amendment should be unpublished. */
    protected static final List<Pattern> unpublishBillEventPatterns = Arrays.asList(
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO PREVIOUS PRINT " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO ORIGINAL PRINT " + simpleBillRegex)
    );

    /** THe format for program info lines. We just care about the text portion. */
    protected static final Pattern programInfoPattern = Pattern.compile("\\d+\\s+(.+)");

    /** --- Constructors --- */

    public BillProcessor() {}

    /** --- Implementation methods --- */

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.BILL;
    }

    /**
     * Performs processing of the SOBI bill fragments with the option to limit to a collection of
     * bills using the restrictToBillIds set (for testing/development purposes only).
     *
     * @param sobiFragment SobiFragment
     */
    @Override
    public void process(SobiFragment sobiFragment) {
        IngestCache<BaseBillId, Bill> billIngestCache = new IngestCache<>();
        LocalDateTime date = sobiFragment.getPublishedDateTime();
        List<SobiBlock> blocks = sobiFragment.getSobiBlocks();
        logger.info("Processing " + sobiFragment.getFragmentId() + " with (" + blocks.size() + ") blocks.");
        for (SobiBlock block : blocks) {
            String data = block.getData();
            Bill baseBill = getOrCreateBaseBill(sobiFragment, block, billIngestCache);
            String specifiedVersion = block.getAmendment();
            BillAmendment specifiedAmendment = baseBill.getAmendment(specifiedVersion);
            BillAmendment activeAmendment = baseBill.getActiveAmendment();
            logger.debug("Updating {} - {} | Line {}-{}", block.getBasePrintNo() + block.getAmendment(), block.getType(),
                                                          block.getStartLineNo(), block.getEndLineNo());
            try {
                switch (block.getType()) {
                    case BILL_INFO: applyBillInfo(data, baseBill, specifiedAmendment, date); break;
                    case LAW_SECTION: applyLawSection(data, baseBill, date); break;
                    case TITLE: applyTitle(data, baseBill, date); break;
                    case BILL_EVENT: applyBillEvent(data, baseBill, specifiedAmendment, date); break;
                    case SAME_AS: applySameAs(data, specifiedAmendment, date); break;
                    case SPONSOR: applySponsor(data, baseBill, specifiedAmendment, date); break;
                    case CO_SPONSOR: applyCosponsors(data, activeAmendment, date); break;
                    case MULTI_SPONSOR: applyMultisponsors(data, activeAmendment, date); break;
                    case PROGRAM_INFO: applyProgramInfo(data, baseBill, date); break;
                    case ACT_CLAUSE: applyActClause(data, specifiedAmendment, date); break;
                    case LAW: applyLaw(data, baseBill, date); break;
                    case SUMMARY: applySummary(data, baseBill, date); break;
                    case SPONSOR_MEMO:
                    case RESOLUTION_TEXT:
                    case TEXT: applyText(data, specifiedAmendment, date, block.getType()); break;
                    case VETO_APPROVE_MEMO: applyVetoMessageText(data, baseBill, date); break;
                    case VOTE_MEMO: applyVoteMemo(data, specifiedAmendment, date); break;
                    default: throw new ParseError("Invalid Line Code " + block.getType());
                }
            }
            catch (ParseError ex) {
                logger.error("Parse Error: {}", ex);
            }
            billIngestCache.set(baseBill.getBaseBillId(), baseBill);
        }

        // Save all the bills worked on for this sobi file
        for (Bill bill : billIngestCache.getCurrentCache()) {
            logger.trace("Saving bill " + bill);
            applyPublishStatus(bill);
            applyUniBillText(bill, billIngestCache, sobiFragment);
            billDataService.saveBill(bill, sobiFragment);
        }
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
    private void applyBillInfo(String data, Bill baseBill, BillAmendment specifiedAmendent, LocalDateTime date) throws ParseError {
        if (data.startsWith("DELETE")) {
            specifiedAmendent.setPublishedDateTime(null);
            return;
        }
        else {
            // The base amendment can be published as soon as a bill info line is received.
            if (specifiedAmendent.isBaseVersion() && !specifiedAmendent.isPublished()) {
                specifiedAmendent.setPublishedDateTime(date);
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
                    logger.debug("Failed to parse previous session year from Bill Info line: " + prevSessionYearStr);
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
    private void applyLawSection(String data, Bill baseBill, LocalDateTime date) {
        baseBill.setLawSection(data.trim());
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
     * @throws ParseError
     */
    private void applyBillEvent(String data, Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime date) throws ParseError {
        ArrayList<BillAction> actions = new ArrayList<>();
        Boolean stricken = false;
        BillId sameAsBillId = null;
        Chamber currentChamber = baseBill.getBaseBillId().getChamber();
        CommitteeVersionId currentCommittee = null;
        SortedSet<CommitteeVersionId> pastCommittees = new TreeSet<>();
        BillId billId = specifiedAmendment.getBillId();
        int sequenceNo = 0;

        for (String line : data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                LocalDate eventDate;
                try {
                    eventDate = LocalDate.from(eventDateFormat.parse(billEvent.group(1)));
                }
                catch (DateTimeParseException ex) {
                    throw new ParseError("eventDateFormat parse failure: " + billEvent.group(1));
                }

                String eventText = billEvent.group(2).trim().toUpperCase();
                BillAction action = new BillAction(eventDate, eventText, currentChamber, ++sequenceNo, billId);
                action.setModifiedDateTime(date);
                action.setPublishedDateTime(date);
                actions.add(action);

                Matcher committeeEventText = committeeEventTextPattern.matcher(eventText);
                Matcher substituteEventText = substituteEventTextPattern.matcher(eventText);
                Matcher floorEventText = floorEventTextPattern.matcher(eventText);
                Matcher chamberSwitchEventText = chamberSwitchEventTextPattern.matcher(eventText);

                if (eventText.contains("ENACTING CLAUSE STRICKEN")) {
                    stricken = true;
                }
                else if (committeeEventText.find()) {
                    currentCommittee = new CommitteeVersionId(
                        currentChamber, committeeEventText.group(2), billId.getSession(), eventDate);
                    pastCommittees.add(currentCommittee);
                }
                else if (floorEventText.find()) {
                    currentCommittee = null;
                }
                else if (chamberSwitchEventText.find()) {
                    currentChamber = Chamber.valueOf(chamberSwitchEventText.group(2));
                }
                else if (substituteEventText.find()) {
                    // Note: Does not account for multiple same-as here.
                    sameAsBillId = new BillId(substituteEventText.group(2), baseBill.getSession());
                }
            }
            else {
                throw new ParseError("billEventPattern not matched: " + line);
            }
        }

        baseBill.setActions(actions);
        baseBill.setPastCommittees(pastCommittees);
        baseBill.setModifiedDateTime(date);

        if (sameAsBillId != null) {
            specifiedAmendment.getSameAs().clear();
            specifiedAmendment.getSameAs().add(sameAsBillId);
        }

        specifiedAmendment.setCurrentCommittee(currentCommittee);
        specifiedAmendment.setStricken(stricken);
        specifiedAmendment.setModifiedDateTime(date);
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
    private void applySameAs(String data, BillAmendment specifiedAmendment, LocalDateTime date) {
        if (data.trim().equalsIgnoreCase("No same as") || data.trim().equalsIgnoreCase("DELETE")) {
            specifiedAmendment.getSameAs().clear();
            specifiedAmendment.setUniBill(false);
            specifiedAmendment.setModifiedDateTime(date);
        }
        else {
            Matcher sameAsMatcher = sameAsPattern.matcher(data);
            if (sameAsMatcher.find()) {
                specifiedAmendment.getSameAs().clear();
                if (sameAsMatcher.group(1) != null && !sameAsMatcher.group(1).isEmpty()) {
                    specifiedAmendment.setUniBill(true);
                }
                List<String> sameAsMatches = new ArrayList<>(Arrays.asList(sameAsMatcher.group(2).split(", ")));
                for (String sameAs : sameAsMatches) {
                    specifiedAmendment.getSameAs().add(new BillId(sameAs.replace("-", "").replace(" ",""),
                                                       specifiedAmendment.getSession()));
                }
                specifiedAmendment.setModifiedDateTime(date);
            }
            else {
                logger.error("sameAsPattern not matched: " + data);
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
    private void applySponsor(String data, Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime date) {
        // Apply the lines in order given as each represents its own "block"
        int sessionYear = baseBill.getSession();

        for(String line : data.split("\n")) {
            line = line.toUpperCase().trim();
            if (line.equals("DELETE")) {
                baseBill.setSponsor(null);
                specifiedAmendment.setCoSponsors(new ArrayList<>());
                specifiedAmendment.setMultiSponsors(new ArrayList<>());
                specifiedAmendment.setModifiedDateTime(date);
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
    private void applyCosponsors(String data, BillAmendment activeAmendment, LocalDateTime date) {
        LinkedHashSet<Member> coSponsors = new LinkedHashSet<>();
        int session = activeAmendment.getSession();
        Chamber chamber = activeAmendment.getBillType().getChamber();
        for (String coSponsor : data.replace("\n", " ").split(",")) {
            coSponsor = coSponsor.trim();
            if (!coSponsor.isEmpty()) {
                coSponsors.add(getMemberFromShortName(coSponsor, session, chamber, true));
            }
        }
        // The cosponsor info is always sent for the base bill version.
        // We can use the currently active amendment instead.
        activeAmendment.setCoSponsors(Lists.newArrayList(coSponsors));
        activeAmendment.setModifiedDateTime(date);
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
    private void applyMultisponsors(String data, BillAmendment activeAmendment, LocalDateTime date) {
        LinkedHashSet<Member> multiSponsors = new LinkedHashSet<>();
        int session = activeAmendment.getSession();
        Chamber chamber = activeAmendment.getBillType().getChamber();
        for (String multiSponsor : data.replace("\n", " ").split(",")) {
            multiSponsor = multiSponsor.trim();
            if (!multiSponsor.isEmpty()) {
                multiSponsors.add(getMemberFromShortName(multiSponsor, session, chamber, false));
            }
        }
        activeAmendment.setMultiSponsors(Lists.newArrayList(multiSponsors));
        activeAmendment.setModifiedDateTime(date);
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
    private void applyActClause(String data, BillAmendment specifiedAmendment, LocalDateTime date) {
        if (data.trim().equals("DELETE")) {
            specifiedAmendment.setActClause("");
        }
        else {
            specifiedAmendment.setActClause(data.replace("\n", " ").trim());
        }
        specifiedAmendment.setModifiedDateTime(date);
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
    private void applyLaw(String data, Bill baseBill, LocalDateTime date) {
        // This is theoretically not safe because a law line *could* start with DELETE
        // We can't do an exact match because B can be multi-line
        if (data.trim().startsWith("DELETE")) {
            baseBill.setLaw("");
            baseBill.setSummary("");
            baseBill.setModifiedDateTime(date);
        }
        else {
            baseBill.setLaw(data.replace("\n", " ").trim());
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
    private void applyText(String data, BillAmendment billAmendment, LocalDateTime date, SobiLineType lineType) throws ParseError{
        BillTextParser billTextParser = new BillTextParser(data, BillTextType.getTypeString(lineType), date);
        String fullText = billTextParser.extractText();
        if (fullText != null) {
            if (lineType == SobiLineType.SPONSOR_MEMO) {
                billAmendment.setMemo(fullText);
            }
            else if (lineType == SobiLineType.RESOLUTION_TEXT || lineType == SobiLineType.TEXT){
                billAmendment.setFulltext(fullText);
            }
            billAmendment.setModifiedDateTime(date);
        }
    }

    /**
     * Constructs a veto message object by parsing the memo
     * @throws ParseError
     */
    private void applyVetoMessageText(String data, Bill baseBill, LocalDateTime date) throws ParseError{
        VetoMemoParser vetoMemoParser = new VetoMemoParser(data, date);
        vetoMemoParser.extractText();
        VetoMessage vetoMessage = vetoMemoParser.getVetoMessage();
        vetoMessage.setSession(baseBill.getSession());
        vetoMessage.setBillId(baseBill.getBaseBillId());
        vetoMessage.setModifiedDateTime(date);
        vetoMessage.setPublishedDateTime(date);

        baseBill.getVetoMessages().put(vetoMessage.getVetoId(), vetoMessage);
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
                baseBill.setProgramInfo(programMatcher.group(1));
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
                    Member voter = getMemberFromShortName(shortName, billId.getSession(), Chamber.SENATE, true);
                    vote.addMemberVote(voteCode, voter);
                }
            }
            else {
                throw new ParseError("Hit vote data without a header: " + data);
            }
        }
        specifiedAmendment.updateVote(vote);
        specifiedAmendment.setModifiedDateTime(date);
    }

    /** --- Post Process Methods --- */

    /**
     * Uni-bills share text with their counterpart house. Ensure that the full text of bill amendments that
     * have a uni-bill designator are kept in sync.
     */
    protected void applyUniBillText(Bill baseBill, IngestCache<BaseBillId, Bill> ingestCache, SobiFragment sobiFragment) {
        for (BillAmendment billAmendment : baseBill.getAmendmentList()) {
            if (billAmendment.isUniBill()) {
                for (BillId uniBillId : billAmendment.getSameAs()) {
                    try {
                        Bill uniBill = getBaseBillFromCacheOrService(uniBillId, ingestCache);
                        BillAmendment uniBillAmend = uniBill.getAmendment(uniBillId.getVersion());
                        String fullText = (billAmendment.getFulltext() != null) ? billAmendment.getFulltext() : "";
                        String uniFullText = (uniBillAmend.getFulltext() != null) ? uniBillAmend.getFulltext() : "";
                        if (fullText.isEmpty() && !uniFullText.isEmpty()) {
                            billAmendment.setFulltext(uniFullText);
                        }
                        else if (!fullText.isEmpty() && !fullText.equals(uniFullText)) {
                            // Perform an update of the same as bill that is receiving the text.
                            uniBillAmend.setFulltext(fullText);
                            billDataService.saveBill(uniBill, sobiFragment);
                        }
                    }
                    catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
                        logger.warn("Failed to synchronize full text of {} with same as bill {}. Reason: {}",
                            billAmendment, uniBillId, ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * The publish date for a bill amendment indicates if and when an amendment should be visible.
     * We can parse the actions list to determine the dates for when the amendments were published
     * as well as when they were reverted (if applicable).
     */
    protected void applyPublishStatus(Bill baseBill) {
        // The actions will indicate which bill amendments should be published.
        if (!baseBill.getActions().isEmpty()) {
            ArrayList<BillAction> sortedActions = new ArrayList<>(baseBill.getActions());
            Collections.sort(sortedActions, new BillAction.ByEventSequenceAsc());
            // Un-publish all the non-base bill amendments.
            for (BillAmendment amendment : baseBill.getAmendmentList()) {
                if (!amendment.isBaseVersion()) {
                    amendment.setPublishedDateTime(null);
                }
            }
            // Publish/unpublish an amendment depending on the action.
            for (BillAction action : sortedActions) {
                boolean foundPublishPattern = false;
                for (Pattern pattern : publishBillEventPatterns) {
                    Matcher matcher = pattern.matcher(action.getText());
                    if (matcher.find()) {
                        foundPublishPattern = true;
                        String version = matcher.group(2).trim();
                        if (baseBill.hasAmendment(version)) {
                            logger.trace("Publishing version " + version + " via action " + action.getText());
                            baseBill.getAmendment(version).setPublishedDateTime(action.getDate().atStartOfDay());
                            baseBill.setActiveVersion(version);
                        }
                        break;
                    }
                }
                if (!foundPublishPattern) {
                    for (Pattern pattern : unpublishBillEventPatterns) {
                        Matcher matcher = pattern.matcher(action.getText());
                        if (matcher.find()) {
                            String version = matcher.group(2).trim();
                            baseBill.setActiveVersion(version);
                            for (BillAmendment amendment : baseBill.getAmendmentList()) {
                                if (amendment.getVersion().compareTo(version) > 0) {
                                    baseBill.getAmendment(version).setPublishedDateTime(null);
                                    logger.trace("Unpublishing version " + version + " via action " + action.getText());
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Constructs a BillSponsor via the sponsorLine string and applies it to the bill.
     */
    protected void setBillSponsorFromSponsorLine(Bill baseBill, String sponsorLine, int sessionYear) {
        // Get the chamber from the Bill
        Chamber chamber = baseBill.getBillType().getChamber();
        // New Sponsor instance
        BillSponsor billSponsor = new BillSponsor();
        // Format the sponsor line
        sponsorLine = sponsorLine.replace("(MS)", "").toUpperCase().trim();
        // Check for RULES sponsors
        if (sponsorLine.startsWith("RULES")) {
            billSponsor.setRulesSponsor(true);
            Matcher rules = rulesSponsorPattern.matcher(sponsorLine);
            if (!sponsorLine.equals("RULES COM") && rules.matches()) {
                sponsorLine = rules.group(1) + ((rules.group(2) != null) ? rules.group(2) : "");
                billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber, false));
            }
        }
        // Budget bills don't have a specific sponsor
        else if (sponsorLine.startsWith("BUDGET")) {
            billSponsor.setBudgetBill(true);
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
                    sponsors.forEach(s ->
                        baseBill.getAdditionalSponsors().add(getMemberFromShortName(s, sessionYear, chamber, true)));
                }
            }
            // Set the member into the sponsor instance
            billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber, true));
        }
        baseBill.setSponsor(billSponsor);
    }
}