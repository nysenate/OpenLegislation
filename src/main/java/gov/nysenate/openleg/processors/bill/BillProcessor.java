package gov.nysenate.openleg.processors.bill;

import gov.nysenate.openleg.dao.bill.BillDao;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Person;
import gov.nysenate.openleg.model.sobi.SOBIBlock;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.processors.sobi.SOBIProcessor;
import gov.nysenate.openleg.processors.util.IngestCache;
import gov.nysenate.openleg.util.Storage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BillProcessor extends SOBIProcessor
{
    private static final Logger logger = Logger.getLogger(BillProcessor.class);

    /** Date format found in SOBIBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final SimpleDateFormat eventDateFormat = new SimpleDateFormat("MM/dd/yy");

    /** Date format found in SOBIBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final SimpleDateFormat voteDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    /** The expected format for the first line of the vote memo [V] block data. */
    public static final Pattern voteHeaderPattern = Pattern.compile("Senate Vote    Bill: (.{18}) Date: (.{10}).*");

    /** The expected format for recorded votes in the SOBIBlock[V] vote memo blocks; e.g. 'AYE  ADAMS' */
    protected static final Pattern votePattern = Pattern.compile("(.{4}) (.{1,15})");

    /** The expected format for actions recorded in the bill events [4] block. e.g. 02/04/13 Event Text Here */
    protected static final Pattern billEventPattern = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (.*)");

    /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
    protected static final Pattern sameAsPattern = Pattern.compile("Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?)+)");

    /** The expected format for Bill Info [1] block data. */
    public static final Pattern billInfoPattern = Pattern.compile("(.{20})([0-9]{5}[ A-Z])(.{33})([ A-Z][0-9]{5}[ `\\-A-Z0-9])(.{8})(.*)");

    /** The expected format for header lines inside bill [T] and memo [M] text data. */
    public static final Pattern textHeaderPattern = Pattern.compile("00000\\.SO DOC ([ASC]) ([0-9R/A-Z ]{13}) ([A-Z* ]{24}) ([A-Z ]{20}) ([0-9]{4}).*");

    /** Pattern for extracting the committee from matching bill events. */
    public static final Pattern committeeEventTextPattern = Pattern.compile("(REFERRED|COMMITTED|RECOMMIT) TO (.*)");

    /** Pattern for detecting calendar events in bill action lists. */
    public static final Pattern floorEventTextPattern = Pattern.compile("(REPORT CAL|THIRD READING|RULES REPORT)");

    /** Pattern for extracting the substituting bill printNo from matching bill events. */
    public static final Pattern substituteEventTextPattern = Pattern.compile("SUBSTITUTED (FOR|BY) (.*)");

    /** Pattern to extract bill number and version when in the format 1234A. */
    public static final String simpleBillRegex = "([0-9]{2,})([ a-zA-Z]?)";

    /** Patterns for bill actions that indicate that the specified bill amendment should be published. */
    public static final List<Pattern> publishBillEventPatterns = Arrays.asList(
        Pattern.compile("PRINT NUMBER " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? ON THIRD READING " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? ON THIRD READING \\(T\\) " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? AND RECOMMIT(?:TED) TO RULES " + simpleBillRegex)
    );

    /** Patterns for bill actions that indicate that the specified bill amendment should be unpublished. */
    public static final List<Pattern> unpublishBillEventPatterns = Arrays.asList(
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO PREVIOUS PRINT " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO ORIGINAL PRINT " + simpleBillRegex)
    );

    /** Retrieves/saves bill data to the persistence layer. */
    @Autowired
    private BillDao billDao;

    /** Cache used to improve processing performance. */
    private IngestCache<BillId, Bill> ingestCache;

    /** --- Constructors --- */

    public BillProcessor() {
        this.ingestCache = new IngestCache<>();
    }

    /** --- Implementation methods --- */

    /**
     * Process all bills with updates that are referenced in the given SOBIFragment.
     * @param sobiFragment SOBIFragment
     */
    @Override
    public void process(SOBIFragment sobiFragment) {
        process(sobiFragment, null);
    }

    /**
     * Performs processing of the SOBI bill fragments with the option to limit to a collection of
     * bills using the restrictToBillIds set (for testing/development purposes only).
     * @param sobiFragment SOBIFragment
     * @param restrictToBillIds Set<BillId> - Set as null or empty to process all bills that come up.
     *                                        To restrict the processing, add the bill ids into the set. All
     *                                        amendments of the bill will be processed so the version in the
     *                                        bill id is ignored.
     */
    public void process(SOBIFragment sobiFragment, Set<BillId> restrictToBillIds) {
        IngestCache<BillId, Bill> billCache = new IngestCache<>();
        Date date = sobiFragment.getPublishedDateTime();
        List<SOBIBlock> blocks = sobiFragment.getSOBIBlocks();
        logger.info("Processing " + sobiFragment.getFileName() + " with (" + blocks.size() + ") blocks.");
        for (SOBIBlock block : blocks) {
            // Restricting processing to certain print numbers is intended primarily for testing.
            if (restrictToBillIds != null && !restrictToBillIds.isEmpty() &&
                !restrictToBillIds.contains(block.getBillId().getBase())) {
                continue;
            }
            String data = block.getData();
            Bill baseBill = getOrCreateBaseBill(sobiFragment, block, billCache);
            String specifiedVersion = block.getAmendment();
            BillAmendment specifiedAmendment = baseBill.getAmendment(specifiedVersion);
            BillAmendment activeAmendment = baseBill.getActiveAmendment();
            logger.debug("Updating " + block.getBasePrintNo() + block.getAmendment() + " : " + block.getType());
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
                    case PROGRAM_INFO: applyProgramInfo(data, specifiedAmendment, date); break;
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
                logger.error(ex);
            }
            billCache.set(baseBill.getBillId(), baseBill);
        }

        for (Bill bill : billCache.getCurrentCache()) {
            logger.debug("Saving bill " + bill.getBillId());
            billDao.updateBill(bill, sobiFragment);
        }
    }

    /**
     * Retrieves the base Bill from storage using the bill print number and year set in the SOBIBlock.
     * If this base bill does not exist, it will be created. The amendment instance will also be created
     * if it does not exist.
     *
     * @param fragment SOBIFragment
     * @param block SOBIBlock
     * @param billCache IngestCache<Bill>
     * @return Bill
     */
    public Bill getOrCreateBaseBill(SOBIFragment fragment, SOBIBlock block, IngestCache<BillId, Bill> billCache) {
        Date modifiedDate = fragment.getPublishedDateTime();
        boolean isBaseVersion = block.getAmendment().equals(BillId.BASE_VERSION);
        BillId baseBillId = new BillId(block.getBasePrintNo(), block.getSession());
        boolean isCached = billCache.has(baseBillId);
        logger.trace("Bill ingest cache " + ((isCached) ? "hit" : "miss") + " for bill id " + baseBillId);
        Bill baseBill = (isCached) ? billCache.get(baseBillId) : billDao.getBill(block.getBasePrintNo(), block.getSession());
        if (baseBill == null) {
            if (!isBaseVersion) {
                logger.warn("Bill Amendment filed without initial bill at " + block.getLocation() + " - " + block.getHeader());
            }
            baseBill = new Bill(block.getBasePrintNo(), block.getSession());
            baseBill.setModifiedDate(modifiedDate);
            billCache.set(baseBillId, baseBill);
        }
        if (!baseBill.hasAmendment(block.getAmendment())) {
            BillAmendment billAmendment = new BillAmendment(baseBill, block.getAmendment());
            billAmendment.setModifiedDate(modifiedDate);
            // If an active amendment exists, apply its ACT TO clause to this amendment
            if (baseBill.getActiveAmendment() != null) {
                billAmendment.setActClause(baseBill.getActiveAmendment().getActClause());
            }
            // Create the base version if an amendment was received before the base version
            if (!isBaseVersion) {
                if (!baseBill.hasAmendment(BillId.BASE_VERSION)) {
                    BillAmendment baseAmendment = new BillAmendment(baseBill, BillId.BASE_VERSION);
                    baseAmendment.setModifiedDate(modifiedDate);
                    baseBill.addAmendment(baseAmendment);
                    baseBill.setActiveVersion(BillId.BASE_VERSION);
                }
                // Pull 'shared' data from the currently active amendment
                BillAmendment activeAmendment = baseBill.getAmendment(baseBill.getActiveVersion());
                billAmendment.setCoSponsors(activeAmendment.getCoSponsors());
                billAmendment.setMultiSponsors(activeAmendment.getMultiSponsors());
            }
            baseBill.addAmendment(billAmendment);
        }
        return baseBill;
    }

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
     * @param data String
     * @param baseBill Bill
     * @param specifiedAmendent BillAmendment
     * @param date Date
     * @throws ParseError
     */
    public void applyBillInfo(String data, Bill baseBill, BillAmendment specifiedAmendent, Date date) throws ParseError {
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
            if (!sponsor.isEmpty() && (baseBill.getSponsor() == null || baseBill.getSponsor().getFullName().isEmpty())) {
                baseBill.setSponsor(new Person(sponsor));
                baseBill.setModifiedDate(date);
            }

            String prevPrintNo = billData.group(4).trim().replaceAll("[0-9`-]$", "");
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
     * @param data String
     * @param baseBill Bill
     * @param date Date
     * @throws ParseError
     */
    public void applyLawSection(String data, Bill baseBill, Date date) {
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
     * @param data String
     * @param baseBill Bill
     * @param date Date
     * @throws ParseError
     */
    public void applyTitle(String data, Bill baseBill, Date date) {
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
     * @param data
     * @param baseBill
     * @param date
     * @throws ParseError
     */
    public void applyBillEvent(String data, Bill baseBill, BillAmendment specifiedAmendment, Date date) throws ParseError {
        ArrayList<BillAction> actions = new ArrayList<>();
        Boolean stricken = false;
        BillId sameAsBillId = null;
        String currentCommittee = "";
        List<String> pastCommittees = new ArrayList<>();
        BillId billId = specifiedAmendment.getBillId();
        int sequenceNo = 0;

        for (String line : data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                try {
                    Date eventDate = eventDateFormat.parse(billEvent.group(1));
                    String eventText = billEvent.group(2).trim();
                    BillAction action = new BillAction(eventDate, eventText, ++sequenceNo, billId);
                    actions.add(action);

                    eventText = eventText.toUpperCase();
                    Matcher committeeEventText = committeeEventTextPattern.matcher(eventText);
                    Matcher substituteEventText = substituteEventTextPattern.matcher(eventText);
                    Matcher floorEventText = floorEventTextPattern.matcher(eventText);

                    if (eventText.contains("ENACTING CLAUSE STRICKEN")) {
                        stricken = true;
                    }
                    else if (committeeEventText.find()) {
                        if (!currentCommittee.isEmpty()) {
                            pastCommittees.add(currentCommittee);
                        }
                        currentCommittee = committeeEventText.group(2);
                    }
                    else if (floorEventText.find()){
                        if (!currentCommittee.isEmpty()) {
                            pastCommittees.add(currentCommittee);
                        }
                        currentCommittee = "";
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

        // The actions will indicate which bill amendments should be published.
        if (!actions.isEmpty()) {
            applyPublishStatus(baseBill, actions);
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
     * The publish date for a bill amendment indicates if and when an amendment should be visible.
     * We can parse the actions list to determine the dates for when the amendments were published
     * as well as when they were reverted (if applicable).
     * @param baseBill Bill
     * @param actions ArrayList<BillAction>
     */
    private void applyPublishStatus(Bill baseBill, ArrayList<BillAction> actions) {
        ArrayList<BillAction> sortedActions = new ArrayList<>(actions);
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
                        logger.debug("Publishing version " + version + " via action " + action.getText());
                        baseBill.getAmendment(version).setPublishDate(action.getDate());
                        baseBill.setActiveVersion(version);
                    }
                    else {
                        logger.fatal("The publish action " + action.getText() + " referenced a bill amendment that was " +
                                "not added to the base bill.");
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
                                logger.debug("Unpublishing version " + version + " via action " + action.getText());
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Safely saves the bill to storage. This makes sure to sync sponsor data across all versions
     * so that different versions of the bill cannot get out of sync
     *
     * @param bill
     * @param storage
     */
    public void saveBill(Bill bill, BillAmendment specifiedAmendment, Storage storage) {
        logger.info("SAVING "+bill.getBillId());

        // An old bug with the assembly sponsors field needs to be corrected, NYSS 7215
        if (bill.getSponsor() != null && bill.getSponsor().getFullName().startsWith("RULES ")) {
            bill.getSponsor().setFullName("RULES");
        }

        if (specifiedAmendment.isPublished()) {
            /** FIXME: Handle the syncing of uni bill text */
//            if (specifiedAmendment.isUniBill() && !specifiedAmendment.getSameAs().isEmpty()) {
//                Uni bills share text, always sent to the senate bill.
//                Bill uniBill = storage.getBill(bill.getSameAs(version));
//                if (uniBill != null) {
//                    String billText = bill.getFulltext(version);
//                    String uniBillText = uniBill.getFulltext(version);
//
//                    If our bill text was deleted, it will still copy the uni-bill's text
//                    if (billText.isEmpty()) {
//                        if (!uniBillText.isEmpty()) {
//                            bill.setFulltext(uniBillText, version);
//                        }
//                    }
//                    else if (!billText.equals(uniBillText)) {
//                        If we differ, then we must have just changed, share the text
//                        uniBill.setFulltext(bill.getFulltext(version), version);
//                    }
//                    storage.set(uniBill);
//                }
//            }

            storage.set(bill);
        }
//        else {
//            storage.set(bill);
//        }

        //billDao.saveBill(bill, null);
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
     *
     * @param data String
     * @param specifiedAmendment BillAmendment
     * @param date Date
     */
    public void applySameAs(String data, BillAmendment specifiedAmendment, Date date) {
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
     *
     * @param data String
     * @param baseBill Bill
     * @param specifiedAmendment BillAmendment
     * @param date Date
     */
    public void applySponsor(String data, Bill baseBill, BillAmendment specifiedAmendment, Date date) {
        // Apply the lines in order given as each represents its own "block"
        for(String line : data.split("\n")) {
            if (line.trim().equals("DELETE")) {
                baseBill.setSponsor(null);
                specifiedAmendment.setCoSponsors(new ArrayList<Person>());
                specifiedAmendment.setMultiSponsors(new ArrayList<Person>());
                specifiedAmendment.setModifiedDate(date);
            }
            else {
                baseBill.setSponsor(new Person(line.trim()));
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
     *
     * @param data String
     * @param activeAmendment BillAmendment
     * @param date Date
     */
    public void applyCosponsors(String data, BillAmendment activeAmendment, Date date) {
        ArrayList<Person> coSponsors = new ArrayList<>();
        for(String coSponsor : data.replace("\n", " ").split(",")) {
            coSponsors.add(new Person(coSponsor.trim()));
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
     *
     * @param data String
     * @param activeAmendment BillAmendment
     * @param date Date
     */
    public void applyMultisponsors(String data, BillAmendment activeAmendment, Date date) {
        ArrayList<Person> multiSponsors = new ArrayList<>();
        for (String multiSponsor : data.replace("\n", " ").split(",")) {
            multiSponsors.add(new Person(multiSponsor.trim()));
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
     *
     * @param data String
     * @param specifiedAmendment BillAmendment
     * @param date Date
     */
    public void applyActClause(String data, BillAmendment specifiedAmendment, Date date) {
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
     *
     * @param data String
     * @param baseBill Bill
     * @param date Date
     */
    public void applyLaw(String data, Bill baseBill, Date date) {
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
     * @param data String
     * @param baseBill Bill
     * @param date Date
     */
    public void applySummary(String data, Bill baseBill, Date date) {
        baseBill.setSummary(data.replace("\n", " ").trim());
        baseBill.setModifiedDate(date);
    }

    /**
     * Applies information to bill text or memo; replaces any existing information.
     *
     * Expected format:
     * <pre>
     * SOBI.D101201.T152619.TXT:2011S00063 T00000.SO DOC S 63                                     BTXT                 2011
     * SOBI.D101201.T152619.TXT:2011S00063 T00083   28    S 4. This act shall take effect immediately.
     * SOBI.D101201.T152619.TXT:2011S00063 T00000.SO DOC S 63            *END*                    BTXT                 2011
     * </pre>
     * Header lines start with 00000.SO DOC and contain one of three actions:
     * <ul>
     * <li>'' - Start of the bill text</li>
     * <li>*END* - End of the bill text</li>
     * <li>*DELETE* - Deletes existing bill text</li>
     * </ul>
     *
     * @param data
     * @param date
     * @throws ParseError
     */
    public void applyText(String data, BillAmendment specifiedAmendment, Date date) throws ParseError {
        // BillText, ResolutionText, and MemoText can be handled the same way.
        // Since Text Blocks can be back to back we constantly look for headers
        // with actions that tell us to start over, end, or delete.
        String type = "";
        StringBuffer text = null;
        String fulltext = null;
        String memo = null;

        for (String line : data.split("\n")) {
            Matcher header = textHeaderPattern.matcher(line);
            if (line.startsWith("00000") && header.find()) {
                //TODO: If house == C then bills can be used for SAME AS verification
                // e.g. 2013S02278 T00000.SO DOC C 2279/2392
                // and  2013S02277 5Same as Uni. A 2394
                //String house = header.group(1);
                //String bills = header.group(2).trim();
                //String year = header.group(5);
                String action = header.group(3).trim();
                type = header.group(4).trim();

                if (!type.equals("BTXT") && !type.equals("RESO TEXT") && !type.equals("MTXT")) {
                    throw new ParseError("Unknown text type found: "+type);
                }

                if (action.equals("*DELETE*")) {
                    if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                        fulltext = "";
                    }
                    else if (type.equals("MTXT")) {
                        memo = "";
                    }
                }
                else if (action.equals("*END*")) {
                    if (text != null) {
                        if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                            fulltext = text.toString();
                        }
                        else if (type.equals("MTXT")) {
                            memo = text.toString();
                        }
                        text = null;
                    }
                    else {
                        throw new ParseError("Text END Found before a body: "+line);
                    }
                }
                else if (action.equals("")) {
                    if (text == null) {
                        //First header for this text segment so initialize
                        text = new StringBuffer();
                        text.ensureCapacity(data.length());
                    }
                    else {
                        //Every 100th line is a repeated header for some reason
                    }
                }
                else {
                    throw new ParseError("Unknown text action found: "+action);
                }
            }
            else if (text != null) {
                // Remove the leading numbers
                text.append(line.substring(5));
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
            } else {
                // Commit what we have and move on
                if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                    fulltext = text.toString();
                } else if (type.equals("MTXT")) {
                    memo = text.toString();
                }
            }
        }

        if (fulltext != null) {
            specifiedAmendment.setFulltext(fulltext);
            specifiedAmendment.setModifiedDate(date);
        }
        if (memo != null) {
            specifiedAmendment.setMemo(fulltext);
            specifiedAmendment.setModifiedDate(date);
        }
    }

    /**
     * Applies data to bill Program. Fully replaces existing information
     *
     * Examples
     * -----------------------------------------------------
     * Program Info   | 9020 Office of Court Administration
     * -----------------------------------------------------
     *
     * @param data String
     * @param specifiedAmendment BillAmendment
     * @param date Date
     */
    public void applyProgramInfo(String data, BillAmendment specifiedAmendment, Date date) {
        // This information currently isn't used for anything
        //if (!data.equals(""))
        //    throw new ParseError("Program info not implemented", data);
    }

    /**
     * Applies information to create or replace a bill vote. Votes are
     * uniquely identified by date/bill. If we have an existing vote on
     * the same date, replace it; otherwise create a new one.
     * <p>
     * Expected vote format:
     * <pre>
     *  2011S01892 VSenate Vote    Bill: S1892              Date: 01/19/2011  Aye - 41  Nay - 19
     *  2011S01892 VNay  Adams            Aye  Addabbo          Aye  Alesi            Aye  Avella
     * </pre>
     * Valid vote codes:
     * <ul>
     * <li>Nay - Vote against</li>
     * <li>Aye - Vote for</li>
     * <li>Abs - Absent during voting</li>
     * <li>Exc - Excused from voting</li>
     * <li>Abd - Abstained from voting</li>
     * </ul>
     * Deleting votes is not possible
     *
     * @param data String
     * @param specifiedAmendment BillAmendment
     * @param date Date
     * @throws ParseError
     */
    public void applyVoteMemo(String data, BillAmendment specifiedAmendment, Date date) throws ParseError {
        // TODO: Parse out sequence number once LBDC (maybe) includes it, #6531
        // Because sometimes votes are back to back we need to check for headers
        // Example of a double vote entry: SOBI.D110119.T140802.TXT:390
        BillVote vote = null;
        for(String line : data.split("\n")) {
            Matcher voteHeader = voteHeaderPattern.matcher(line);
            if (voteHeader.find()) {
                // TODO: this assumes they are the same vote sent twice, what else could happen?
                // Start over if we hit a header, sometimes we get back to back entries.
                try {
                    // Use the old vote if we can find it, otherwise make a new one using now as the publish date
                    vote = new BillVote(specifiedAmendment, voteDateFormat.parse(voteHeader.group(2)), BillVote.VOTE_TYPE_FLOOR, "1");
                    vote.setPublishDate(date);
                    for (BillVote oldVote : specifiedAmendment.getVotes()) {
                        if (oldVote.equals(vote)) {
                            // If we've received this vote before, use the old publish date
                            vote.setPublishDate(oldVote.getPublishDate());
                            break;
                        }
                    }
                    vote.setModifiedDate(date);
                }
                catch (ParseException e) {
                    throw new ParseError("voteDateFormat not matched: "+line);
                }
            }
            else if (vote != null) {
                //Otherwise, build the existing vote
                Matcher voteLine = votePattern.matcher(line);
                while(voteLine.find()) {
                    String type = voteLine.group(1).trim();
                    Person voter = new Person(voteLine.group(2).trim());

                    if (type.equals("Aye")) {
                        vote.addAye(voter);
                    }
                    else if (type.equals("Nay")) {
                        vote.addNay(voter);
                    }
                    else if (type.equals("Abs")) {
                        vote.addAbsent(voter);
                    }
                    else if (type.equals("Abd")) {
                        vote.addAbstain(voter);
                    }
                    else if (type.equals("Exc")) {
                        vote.addExcused(voter);
                    }
                    else {
                        throw new ParseError("Unknown vote type found: "+line);
                    }
                }
            }
            else {
                throw new ParseError("Hit vote data without a header: "+data);
            }
        }
        specifiedAmendment.updateVote(vote);
        specifiedAmendment.setModifiedDate(date);
    }
}