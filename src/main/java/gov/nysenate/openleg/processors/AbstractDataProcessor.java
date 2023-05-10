package gov.nysenate.openleg.processors;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillAmendment;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.processors.law.LawFile;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import gov.nysenate.openleg.processors.log.DataProcessUnitEvent;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.updates.agenda.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.legislation.bill.dao.service.ApprovalDataService;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.bill.exception.BillNotFoundEx;
import gov.nysenate.openleg.legislation.bill.dao.service.VetoDataService;
import gov.nysenate.openleg.updates.bill.BulkBillUpdateEvent;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.legislation.calendar.CalendarNotFoundEx;
import gov.nysenate.openleg.updates.calendar.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.common.util.XmlHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The AbstractDataProcessor class is intended to serve as a common base for all the
 * data processors and provides functionality to fetch and persist various entity types.
 * This is to allow different processors to be consistent in how they utilize various data
 * operations.
 */
public abstract class AbstractDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProcessor.class);

    protected static final Pattern rulesSponsorPattern =
            Pattern.compile("RULES (?:COM )?\\(?([a-zA-Z-']+)( [A-Z])?\\)?(.*)");

    @Autowired protected Environment env;

    /* --- Data Services --- */

    @Autowired protected AgendaDataService agendaDataService;
    @Autowired protected BillDataService billDataService;
    @Autowired protected CalendarDataService calendarDataService;
    @Autowired protected CommitteeDataService committeeDataService;
    @Autowired protected MemberService memberService;
    @Autowired protected VetoDataService vetoDataService;
    @Autowired protected ApprovalDataService apprDataService;

    /* --- Events --- */

    @Autowired protected EventBus eventBus;

    /* --- Ingest Caches --- */

    @Resource(name = "agendaIngestCache") protected IngestCache<AgendaId, Agenda, LegDataFragment> agendaIngestCache;
    @Resource(name = "billIngestCache") protected IngestCache<BaseBillId, Bill, LegDataFragment> billIngestCache;
    @Resource(name = "calendarIngestCache") protected IngestCache<CalendarId, Calendar, LegDataFragment> calendarIngestCache;

    /* --- Utilities --- */

    @Autowired protected XmlHelper xmlHelper;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    /* --- Common Methods --- */

    protected DataProcessUnit createProcessUnit(LegDataFragment legDataFragment) {
        return new DataProcessUnit(legDataFragment.getParentLegDataFile().getSourceType() + "-" + legDataFragment.getType().name(), legDataFragment.getFragmentId(),
            LocalDateTime.now(), DataProcessAction.INGEST);
    }

    protected DataProcessUnit createDataProcessUnit(LawFile lawFile) {
        return new DataProcessUnit("LAW_FILE", lawFile.getFileName(), LocalDateTime.now(), DataProcessAction.INGEST);
    }

    protected void postDataUnitEvent(DataProcessUnit unit) {
        unit.setEndDateTime(LocalDateTime.now());
        eventBus.post(new DataProcessUnitEvent(unit));
    }

    /* --- Bill Methods --- */

    /**
     * Retrieves the base Bill container using the given billId from either the cache or the service layer.
     * If this base bill does not exist, it will be created. The amendment instance will also be created
     * if it does not exist.
     *
     * @param billId BillId - The BillId to find a matching Bill for.
     * @return Bill
     */
    protected final Bill getOrCreateBaseBill(BillId billId, LegDataFragment fragment) {
        boolean isBaseVersion = BillId.isBaseVersion(billId.getVersion());
        BaseBillId baseBillId = BillId.getBaseId(billId);
        Bill baseBill;
        LocalDateTime publishedDateTime = fragment.getPublishedDateTime();
        // Check the cache, or hit the data service otherwise
        if (billIngestCache.has(baseBillId)) {
            baseBill = billIngestCache.get(baseBillId).getLeft();
        }
        else {
            try {
                baseBill = billDataService.getBill(baseBillId);
            }
            catch (BillNotFoundEx ex) {
                // Create the bill since it does not exist and add it to the ingest cache.
                if (!isBaseVersion) {
                    logger.warn("Bill Amendment {} filed without initial bill.", billId);
                }
                baseBill = new Bill(baseBillId);
                baseBill.setModifiedDateTime(publishedDateTime);
                baseBill.setPublishedDateTime(publishedDateTime);
                billIngestCache.set(baseBillId, baseBill, fragment);
            }
            billIngestCache.set(baseBillId, baseBill, fragment);
        }

        if (!baseBill.hasAmendment(billId.getVersion())) {
            BillAmendment billAmendment = new BillAmendment(baseBillId, billId.getVersion());
            // If an active amendment exists, apply its ACT TO clause to this amendment
            if (baseBill.hasActiveAmendment()) {
                billAmendment.setActClause(baseBill.getActiveAmendment().getActClause());
            }
            // Create the base version if an amendment was received before the base version
            if (!isBaseVersion) {
                if (!baseBill.hasAmendment(BillId.DEFAULT_VERSION)) {
                    BillAmendment baseAmendment = new BillAmendment(baseBillId, BillId.DEFAULT_VERSION);
                    baseBill.addAmendment(baseAmendment);
                    baseBill.setActiveVersion(BillId.DEFAULT_VERSION);
                }
                // If the active amendment does not exist, create it
                if (!baseBill.hasActiveAmendment()) {
                    BillAmendment activeAmendment = new BillAmendment(baseBillId, baseBill.getActiveVersion());
                    baseBill.addAmendment(activeAmendment);
                }
                // Otherwise pull 'initially shared' data from the currently active amendment
                else {
                    BillAmendment activeAmendment = baseBill.getActiveAmendment();
                    billAmendment.setCoSponsors(activeAmendment.getCoSponsors());
                    billAmendment.setMultiSponsors(activeAmendment.getMultiSponsors());
                    billAmendment.setLawCode(activeAmendment.getLawCode());
                    billAmendment.setLawSection(activeAmendment.getLawSection());
                }
            }
            logger.trace("Adding bill amendment: " + billAmendment);
            baseBill.addAmendment(billAmendment);
        }
        return baseBill;
    }

    /**
     * Flushes all bills stored in the cache to the persistence layer and clears the cache.
     */
    protected void flushBillUpdates() {
        if (billIngestCache.getSize() > 0) {
            logger.info("Flushing {} bills", billIngestCache.getSize());
            billIngestCache.getCurrentCache().forEach(entry ->
                billDataService.saveBill(entry.getLeft(), entry.getRight(), false));
            logger.debug("Broadcasting bill updates...");
            List<Bill> bills =
                billIngestCache.getCurrentCache().stream().map(Pair::getLeft).collect(Collectors.toList());
            eventBus.post(new BulkBillUpdateEvent(bills, LocalDateTime.now()));
            billIngestCache.clearCache();
        }
    }

    /* --- Member Methods --- */


    /**
     * Retrieves a member from the LBDC short name.  Throws a MemberNotFoundEx if no member can be retrieved.
     */
    protected SessionMember getMemberFromShortName(String shortName, SessionYear sessionYear, Chamber chamber) throws ParseError {
        return memberService.getSessionMemberByShortName(shortName, sessionYear, chamber);
    }

    /**
     * This method is responsible for getting a list of Session Members from a line by parsing it.
     *
     * @param sponsors String of the line to be parsed
     * @param session Bill Session for getting ShortName
     * @param chamber Bill Chamber for getting ShortName
     * @return
     */
    protected List<SessionMember> getSessionMember(String sponsors, SessionYear session, Chamber chamber, String fragmentId) {
        List<String> shortNames = Lists.newArrayList(
                Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sponsors.toUpperCase()));
        List<SessionMember> sessionMembers = new ArrayList<>();
        List<String> badSponsors = new ArrayList<>();
        for (String shortName : shortNames) {
            SessionMember sessionMember = getMemberFromShortName(shortName, session, chamber);
            if (sessionMember != null) {
                sessionMembers.add(sessionMember);
            }
            else {
                badSponsors.add(shortName);
            }
        }
        if (!badSponsors.isEmpty()) {
            throw new ParseError(String.format("Could not parse sponsors from fragment: %s", fragmentId));
        }
        return sessionMembers;
    }

    /**
     * Gets the xml root node from the given fragment.
     *
     * Some legacy fragments are wrapped in a <SENATEDATA> tag and this method unwraps them.
     *
     * @param xmlText String
     * @return Node
     */
    protected Node getXmlRoot(String xmlText) throws XPathExpressionException, IOException, SAXException {
        Node root = xmlHelper.parse(xmlText);
        // some fragments will be wrapped in a senatedata tag.
        Node senateDataNode = xmlHelper.getNode("SENATEDATA", root);
        if (senateDataNode != null) {
            root = senateDataNode;
        }
        return root;
    }

    /* --- Agenda Methods --- */

    /**
     * Retrieve an Agenda instance from the cache/backing store or create it if it does not exist.
     *
     * @param agendaId AgendaId - Retrieve Agenda via this agendaId.
     * @param fragment LegDataFragment
     * @return Agenda
     */
    protected final Agenda getOrCreateAgenda(AgendaId agendaId, LegDataFragment fragment) {
        Agenda agenda;
        try {
            if (agendaIngestCache.has(agendaId)) {
                return agendaIngestCache.get(agendaId).getLeft();
            }
            else {
                agenda = agendaDataService.getAgenda(agendaId);
            }
        }
        catch (AgendaNotFoundEx ex) {
            agenda = new Agenda(agendaId);
            agenda.setModifiedDateTime(fragment.getPublishedDateTime());
            agenda.setPublishedDateTime(fragment.getPublishedDateTime());
        }
        agendaIngestCache.set(agendaId, agenda, fragment);
        return agenda;
    }

    /**
     * Flushes all agendas stored in the cache to the persistence layer and clears the cache.
     */
    protected void flushAgendaUpdates() {
        if (agendaIngestCache.getSize() > 0) {
            logger.info("Flushing {} agendas", agendaIngestCache.getSize());
            agendaIngestCache.getCurrentCache().forEach(
                entry -> agendaDataService.saveAgenda(entry.getLeft(), entry.getRight(), false));
            List<Agenda> agendas =
                agendaIngestCache.getCurrentCache().stream().map(Pair::getLeft).collect(Collectors.toList());
            eventBus.post(new BulkAgendaUpdateEvent(agendas, LocalDateTime.now()));
            agendaIngestCache.clearCache();
        }
    }

    /* --- Calendar Methods --- */

    /**
     * Retrieve a Calendar from the cache/backing store or create it if it does not exist.
     *
     * @param calendarId CalendarId - Retrieve Calendar via this calendarId.
     * @param fragment LegDataFragment
     * @return Calendar
     */
    protected final Calendar getOrCreateCalendar(CalendarId calendarId, LegDataFragment fragment) {
        Calendar calendar;
        try {
            if (calendarIngestCache.has(calendarId)) {
                return calendarIngestCache.get(calendarId).getLeft();
            }
            else {
                calendar = calendarDataService.getCalendar(calendarId);
            }
        }
        catch (CalendarNotFoundEx ex) {
            calendar = new Calendar(calendarId);
            calendar.setModifiedDateTime(fragment.getPublishedDateTime());
            calendar.setPublishedDateTime(fragment.getPublishedDateTime());
        }
        calendarIngestCache.set(calendarId, calendar, fragment);
        return calendar;
    }

    /**
     * Flushes all calendars stored in the cache to the persistence layer and clears the cache.
     */
    protected void flushCalendarUpdates() {
        if (calendarIngestCache.getSize() > 0) {
            logger.info("Flushing {} calendars", calendarIngestCache.getSize());
            calendarIngestCache.getCurrentCache().forEach(
                entry -> calendarDataService.saveCalendar(entry.getLeft(), entry.getRight(), false));
            List<Calendar> calendars =
                calendarIngestCache.getCurrentCache().stream().map(Pair::getLeft).collect(Collectors.toList());
            eventBus.post(new BulkCalendarUpdateEvent(calendars, LocalDateTime.now()));
            calendarIngestCache.clearCache();
        }
    }

    /**
     * Flushes all updates.
     */
    protected void flushAllUpdates() {
        flushBillUpdates();
        flushAgendaUpdates();
        flushCalendarUpdates();
    }
}