package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.sobi.SobiBlock;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.base.IngestCache;
import gov.nysenate.openleg.service.base.SobiProcessor;
import gov.nysenate.openleg.service.sobi.AbstractSobiProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The BillProcessor parses bill sobi fragments, applies bill updates, and persists into the backing
 * store via the service layer. This implementation is fairly lengthy due to the various types of data that
 * are applied to the bills via these fragments.
 */
@Service
public class BillProcessor extends AbstractSobiProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(BillProcessor.class);

    /** Date format found in SobiBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final SimpleDateFormat eventDateFormat = new SimpleDateFormat("MM/dd/yy");

    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final SimpleDateFormat voteDateFormat = new SimpleDateFormat("MM/dd/yyyy");

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

    /** The expected format for header lines inside bill [T] and memo [M] text data. */
    public static final Pattern textHeaderPattern =
        Pattern.compile("00000\\.SO DOC ([ASC]) ([0-9R/A-Z ]{13}) ([A-Z* ]{24}) ([A-Z ]{20}) ([0-9]{4}).*");

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
        IngestCache<BillId, Bill> billIngestCache = new IngestCache<>();
        Date date = sobiFragment.getPublishedDateTime();
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
                    case SPONSOR_MEMO:    // Sponsor memo text - handled by applyText
                    case RESOLUTION_TEXT: // Resolution text - handled by applyText
                    case TEXT: applyText(data, specifiedAmendment, date); break;
                    case VOTE_MEMO: applyVoteMemo(data, specifiedAmendment, date); break;
                    default: throw new ParseError("Invalid Line Code " + block.getType());
                }
            }
            catch (ParseError ex) {
                logger.error("Parse Error: {}", ex);
            }
            billIngestCache.set(baseBill.getBillId(), baseBill);
        }

        // Save all the bills worked on for this sobi file
        for (Bill bill : billIngestCache.getCurrentCache()) {
            logger.trace("Saving bill " + bill.getBillId());
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
    private void applyBillInfo(String data, Bill baseBill, BillAmendment specifiedAmendent, Date date) throws ParseError {
        if (data.startsWith("DELETE")) {
            specifiedAmendent.setPublishDate(null);
            return;
        }
        else {
            // The base amendment can be published as soon as a bill info line is received.
            if (specifiedAmendent.isBaseVersion() && !specifiedAmendent.isPublished()) {
                specifiedAmendent.setPublishDate(date);
            }
        }
        Matcher billData = billInfoPattern.matcher(data);
        if (billData.find()) {
            String sponsor = billData.group(1).trim();
            if (!StringUtils.isEmpty(sponsor) && baseBill.getSponsor() == null) {
                // Apply the sponsor from bill info when the sponsor has not yet been set.
                baseBill.setSponsor(getBillSponsorFromSponsorLine(sponsor, baseBill.getSession(),
                                                                  baseBill.getBillType().getChamber()));
                baseBill.setModifiedDate(date);
            }
            String prevPrintNo = billData.group(4).trim();
            String prevSessionYearStr = billData.group(6).trim();
            if (!prevSessionYearStr.equals("0000") && !prevPrintNo.equals("00000")) {
                try {
                    Integer prevSessionYear = Integer.parseInt(prevSessionYearStr);
                    baseBill.addPreviousVersion(new BillId(prevPrintNo, prevSessionYear));
                    baseBill.setModifiedDate(date);
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
    private void applyLawSection(String data, Bill baseBill, Date date) {
        baseBill.setLawSection(data.trim());
        baseBill.setModifiedDate(date);
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
    private void applyTitle(String data, Bill baseBill, Date date) {
        baseBill.setTitle(data.replace("\n", " ").trim());
        baseBill.setModifiedDate(date);
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
    private void applyBillEvent(String data, Bill baseBill, BillAmendment specifiedAmendment, Date date) throws ParseError {
        ArrayList<BillAction> actions = new ArrayList<>();
        Boolean stricken = false;
        BillId sameAsBillId = null;
        Chamber currentChamber = baseBill.getBillId().getChamber();
        CommitteeVersionId currentCommittee = null;
        SortedSet<CommitteeVersionId> pastCommittees = new TreeSet<>();
        BillId billId = specifiedAmendment.getBillId();
        int sequenceNo = 0;

        for (String line : data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                try {
                    Date eventDate = eventDateFormat.parse(billEvent.group(1));
                    String eventText = billEvent.group(2).trim().toUpperCase();
                    BillAction action = new BillAction(eventDate, eventText, ++sequenceNo, billId);
                    actions.add(action);

                    Matcher committeeEventText = committeeEventTextPattern.matcher(eventText);
                    Matcher substituteEventText = substituteEventTextPattern.matcher(eventText);
                    Matcher floorEventText = floorEventTextPattern.matcher(eventText);
                    Matcher chamberSwitchEventText = chamberSwitchEventTextPattern.matcher(eventText);

                    if (eventText.contains("ENACTING CLAUSE STRICKEN")) {
                        stricken = true;
                    }
                    else if (committeeEventText.find()) {
                        if (currentCommittee != null) {
                            pastCommittees.add(currentCommittee);
                        }
                        currentCommittee = new CommitteeVersionId(
                                currentChamber, committeeEventText.group(2), billId.getSession(), eventDate
                        );
                    }
                    else if (floorEventText.find()){
                        if (currentCommittee != null) {
                            pastCommittees.add(currentCommittee);
                        }
                        currentCommittee = null;
                    }
                    else if (chamberSwitchEventText.find()){
                        currentChamber = Chamber.valueOf(chamberSwitchEventText.group(2));
                    }
                    else if (substituteEventText.find()) {
                        // Note: Does not account for multiple same-as here.
                        sameAsBillId = new BillId(substituteEventText.group(2), baseBill.getSession());
                    }
                }
                catch (ParseException e) {
                    throw new ParseError("eventDateFormat parse failure: " + billEvent.group(1));
                }
            }
            else {
                throw new ParseError("billEventPattern not matched: " + line);
            }
        }

        baseBill.setActions(actions);
        baseBill.setPastCommittees(pastCommittees);
        baseBill.setModifiedDate(date);

        if (sameAsBillId != null) {
            specifiedAmendment.getSameAs().clear();
            specifiedAmendment.getSameAs().add(sameAsBillId);
        }

        specifiedAmendment.setCurrentCommittee(currentCommittee);
        specifiedAmendment.setStricken(stricken);
        specifiedAmendment.setModifiedDate(date);
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
    private void applySameAs(String data, BillAmendment specifiedAmendment, Date date) {
        if (data.trim().equalsIgnoreCase("No same as") || data.trim().equalsIgnoreCase("DELETE")) {
            specifiedAmendment.getSameAs().clear();
            specifiedAmendment.setUniBill(false);
            specifiedAmendment.setModifiedDate(date);
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
                specifiedAmendment.setModifiedDate(date);
            }
            else {
                logger.error("sameAsPattern not matched: " + data);
            }
        }
    }

    /**
     * Applies data to bill sponsor. Fully replaces existing sponsor information. Because
     * this is a one line field the block parser is sometimes tricked into combining consecutive
     * blocks. Make sure to process the dta 1 line at a time.
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
    private void applySponsor(String data, Bill baseBill, BillAmendment specifiedAmendment, Date date) {
        // Apply the lines in order given as each represents its own "block"
        int sessionYear = baseBill.getSession();
        Chamber chamber = baseBill.getBillType().getChamber();

        for(String line : data.split("\n")) {
            line = line.toUpperCase().trim();
            if (line.equals("DELETE")) {
                baseBill.setSponsor(null);
                specifiedAmendment.setCoSponsors(new ArrayList<Member>());
                specifiedAmendment.setMultiSponsors(new ArrayList<Member>());
                specifiedAmendment.setModifiedDate(date);
            }
            else {
                baseBill.setSponsor(getBillSponsorFromSponsorLine(line, sessionYear, chamber));
            }
        }
        baseBill.setModifiedDate(date);
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
    private void applyCosponsors(String data, BillAmendment activeAmendment, Date date) {
        ArrayList<Member> coSponsors = new ArrayList<>();
        int session = activeAmendment.getSession();
        Chamber chamber = activeAmendment.getBillType().getChamber();
        for(String coSponsor : data.replace("\n", " ").split(",")) {
            coSponsor = coSponsor.trim();
            if (!coSponsor.isEmpty()) {
                coSponsors.add(getMemberFromShortName(coSponsor, session, chamber, true));
            }
        }
        // The cosponsor info is always sent for the base bill version.
        // We can use the currently active amendment instead.
        activeAmendment.setCoSponsors(coSponsors);
        activeAmendment.setModifiedDate(date);
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
    private void applyMultisponsors(String data, BillAmendment activeAmendment, Date date) {
        ArrayList<Member> multiSponsors = new ArrayList<>();
        int session = activeAmendment.getSession();
        Chamber chamber = activeAmendment.getBillType().getChamber();
        for (String multiSponsor : data.replace("\n", " ").split(",")) {
            multiSponsor = multiSponsor.trim();
            if (!multiSponsor.isEmpty()) {
                multiSponsors.add(getMemberFromShortName(multiSponsor, session, chamber, false));
            }
        }
        activeAmendment.setMultiSponsors(multiSponsors);
        activeAmendment.setModifiedDate(date);
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
    private void applyActClause(String data, BillAmendment specifiedAmendment, Date date) {
        if (data.trim().equals("DELETE")) {
            specifiedAmendment.setActClause("");
        }
        else {
            specifiedAmendment.setActClause(data.replace("\n", " ").trim());
        }
        specifiedAmendment.setModifiedDate(date);
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
    private void applyLaw(String data, Bill baseBill, Date date) {
        // This is theoretically not safe because a law line *could* start with DELETE
        // We can't do an exact match because B can be multi-line
        if (data.trim().startsWith("DELETE")) {
            baseBill.setLaw("");
            baseBill.setSummary("");
            baseBill.setModifiedDate(date);
        }
        else {
            baseBill.setLaw(data.replace("\n", " ").trim());
        }
        baseBill.setModifiedDate(date);
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
    private void applySummary(String data, Bill baseBill, Date date) {
        baseBill.setSummary(data.replace("\n", " ").trim());
        baseBill.setModifiedDate(date);
    }

    /**
     * Applies information to bill text or memo; replaces any existing information.
     * Header lines start with 00000.SO DOC and contain one of three actions:
     *
     * '' - Start of the bill text</li>
     * *END* - End of the bill text</li>
     * *DELETE* - Deletes existing bill text</li>
     *
     * Examples
     * -----------------------------------------------------------------------------------------------------
     * Resolution Text | R00000.SO DOC A R22                                    RESO TEXT            2013
     *                 | R00001LEGISLATIVE  RESOLUTION  congratulating  the Maine-Endwell Football Team
     *                 | R00000.SO DOC A R22           *END*                    RESO TEXT            2013
     * -----------------------------------------------------------------------------------------------------
     * Bill Text       | T00000.SO DOC S 53                                     BTXT                 2013
     *                 | T00002                           S T A T E   O F   N E W   Y O R K
     *                 | T00000.SO DOC S 53            *END*                    BTXT                 2013
     * -----------------------------------------------------------------------------------------------------
     * Memo Text       | M00000.SO DOC S 1626                                   MTXT                 2013
     *                 | M00006PURPOSE OR GENERAL IDEA OF BILL:  The purpose of this bill is to
     *                 | M00000.SO DOC S 1625          *END*                    MTXT                 2013
     * -----------------------------------------------------------------------------------------------------
     * Delete          | T00000.SO DOC A 8396          *DELETE*                 BTXT                 2013
     * -----------------------------------------------------------------------------------------------------
     *
     * @throws ParseError
     */
    private void applyText(String data, BillAmendment specifiedAmendment, Date date) throws ParseError {
        // BillText, ResolutionText, and MemoText can be handled the same way.
        // Since Text Blocks can be back to back we constantly look for headers
        // with actions that tell us to start over, end, or delete.
        String type = "";
        StringBuilder text = null;
        String fullText = null;

        for (String line : data.split("\n")) {
            Matcher header = textHeaderPattern.matcher(line);
            if (line.startsWith("00000") && header.find()) {
                String action = header.group(3).trim();
                type = header.group(4).trim();
                if (!type.equals("BTXT") && !type.equals("RESO TEXT") && !type.equals("MTXT")) {
                    throw new ParseError("Unknown text type found: " + type);
                }
                if (action.equals("*DELETE*")) {
                    fullText = "";
                }
                else if (action.equals("*END*")) {
                    if (text != null) {
                        fullText = text.toString();
                        text = null;
                    }
                    else {
                        throw new ParseError("Text END Found before a body: " + line);
                    }
                }
                else if (action.equals("")) {
                    if (text == null) {
                        //First header for this text segment so initialize
                        text = new StringBuilder();
                        text.ensureCapacity(data.length());
                    }
                    //Every 100th line is a repeated header for some reason
                }
                else {
                    throw new ParseError("Unknown text action found: "+action);
                }
            }
            else if (text != null) {
                // Remove the leading numbers
                text.append((line.length() > 5) ? line.substring(5) : line.substring(line.length()));
                text.append("\n");
            }
            else {
                throw new ParseError("Text Body found before header: "+line);
            }
        }
        if (text != null) {
            // This is a known issue that was resolved on 03/23/2011
            if (new GregorianCalendar(2011, 3, 23).after(date)) {
                throw new ParseError("Finished text data without a footer");
            }
            else {
                // Commit what we have and move on
                fullText = text.toString();
            }
        }
        if (fullText != null) {
            if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                specifiedAmendment.setFulltext(fullText);
            }
            else {
                specifiedAmendment.setMemo(fullText);
            }
            specifiedAmendment.setModifiedDate(date);
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
    private void applyProgramInfo(String data, Bill baseBill, Date date) {
        if (!data.isEmpty()) {
            Matcher programMatcher = programInfoPattern.matcher(data);
            if (programMatcher.find()) {
                baseBill.setProgramInfo(programMatcher.group(1));
                baseBill.setModifiedDate(date);
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
    private void applyVoteMemo(String data, BillAmendment specifiedAmendment, Date date) throws ParseError {
        // Because sometimes votes are back to back we need to check for headers
        // Example of a double vote entry: SOBI.D110119.T140802.TXT:390
        BillVote vote = null;
        BillId billId = specifiedAmendment.getBillId();
        for(String line : data.split("\n")) {
            Matcher voteHeader = voteHeaderPattern.matcher(line);
            if (voteHeader.find()) {
                // Start over if we hit a header, sometimes we get back to back entries.
                try {
                    // Use the old vote if we can find it, otherwise make a new one using now as the publish date
                    vote = new BillVote(billId, voteDateFormat.parse(voteHeader.group(2)), BillVoteType.FLOOR, 1);
                    vote.setPublishDate(date);
                    for (BillVote oldVote : specifiedAmendment.getVotesList()) {
                        if (oldVote.getVoteId().equals(vote.getVoteId())) {
                            // If we've received this vote before, use the old publish date
                            vote.setPublishDate(oldVote.getPublishDate());
                            break;
                        }
                    }
                    vote.setModifiedDate(date);
                }
                catch (ParseException ex) {
                    throw new ParseError("voteDateFormat not matched: " + line);
                }
            }
            else if (vote != null) {
                //Otherwise, build the existing vote
                Matcher voteLine = votePattern.matcher(line);
                while(voteLine.find()) {
                    BillVoteCode voteCode;
                    try {
                        voteCode = BillVoteCode.valueOf(voteLine.group(1).trim().toUpperCase());
                    }
                    catch (IllegalArgumentException ex) {
                        throw new ParseError("No vote code mapping for " + voteLine);
                    }
                    String shortName = voteLine.group(2).trim();
                    // Only senator votes are received
                    Member voter = getMemberFromShortName(shortName, billId.getSession(), Chamber.SENATE, true);
                    vote.addMemberVote(voteCode, voter);
                }
            }
            else {
                throw new ParseError("Hit vote data without a header: " + data);
            }
        }
        specifiedAmendment.updateVote(vote);
        specifiedAmendment.setModifiedDate(date);
    }

    /** --- Post Process Methods --- */

    /**
     * Uni-bills share text with their counterpart house. Ensure that the full text of bill amendments that
     * have a uni-bill designator are kept in sync.
     */
    protected void applyUniBillText(Bill baseBill, IngestCache<BillId, Bill> ingestCache, SobiFragment sobiFragment) {
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
                    amendment.setPublishDate(null);
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
                            baseBill.getAmendment(version).setPublishDate(action.getDate());
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
                                    baseBill.getAmendment(version).setPublishDate(null);
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
     * Constructs a BillSponsor via the sponsorLine string.
     */
    protected BillSponsor getBillSponsorFromSponsorLine(String sponsorLine, int sessionYear, Chamber chamber) {
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
            billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber, true));
        }
        return billSponsor;
    }
}