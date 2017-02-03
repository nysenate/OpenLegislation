package gov.nysenate.openleg.processor.base;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.process.DataProcessAction;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.process.DataProcessUnitEvent;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.agenda.event.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.data.VetoDataService;
import gov.nysenate.openleg.service.bill.event.BulkBillUpdateEvent;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;
import gov.nysenate.openleg.service.calendar.event.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired protected Environment env;

    /** --- Data Services --- */

    @Autowired protected AgendaDataService agendaDataService;
    @Autowired protected BillDataService billDataService;
    @Autowired protected CalendarDataService calendarDataService;
    @Autowired protected CommitteeDataService committeeDataService;
    @Autowired protected MemberService memberService;
    @Autowired protected VetoDataService vetoDataService;

    /** --- Events --- */

    @Autowired protected EventBus eventBus;

    /** --- Ingest Caches --- */

    @Resource(name = "agendaIngestCache") protected IngestCache<AgendaId, Agenda, SobiFragment> agendaIngestCache;
    @Resource(name = "billIngestCache") protected IngestCache<BaseBillId, Bill, SobiFragment> billIngestCache;
    @Resource(name = "calendarIngestCache") protected IngestCache<CalendarId, Calendar, SobiFragment> calendarIngestCache;

    public abstract void init();

    public void initBase() {
        eventBus.register(this);
    }

    /** --- Common Methods --- */

    protected DataProcessUnit createProcessUnit(SobiFragment sobiFragment) {
        return new DataProcessUnit("SOBI-" + sobiFragment.getType().name(), sobiFragment.getFragmentId(),
            LocalDateTime.now(), DataProcessAction.INGEST);
    }

    protected DataProcessUnit createDataProcessUnit(LawFile lawFile) {
        return new DataProcessUnit("LAW_FILE", lawFile.getFileName(), LocalDateTime.now(), DataProcessAction.INGEST);
    }

    protected void postDataUnitEvent(DataProcessUnit unit) {
        unit.setEndDateTime(LocalDateTime.now());
        eventBus.post(new DataProcessUnitEvent(unit));
    }

    /** --- Bill Methods --- */

    /**
     * Retrieves the base Bill container using the given billId from either the cache or the service layer.
     * If this base bill does not exist, it will be created. The amendment instance will also be created
     * if it does not exist.
     *
     * @param publishDate Date - Typically the date of the source data file. Only used when bill information
     *                           does not already exist and must be created.
     * @param billId BillId - The BillId to find a matching Bill for.
     * @return Bill
     */
    protected  final Bill getOrCreateBaseBill(LocalDateTime publishDate, BillId billId, SobiFragment fragment) {
        boolean isBaseVersion = BillId.isBaseVersion(billId.getVersion());
        BaseBillId baseBillId = BillId.getBaseId(billId);
        Bill baseBill;
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
                baseBill.setModifiedDateTime(publishDate);
                baseBill.setPublishedDateTime(publishDate);
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
                billIngestCache.getCurrentCache().stream().map(entry -> entry.getLeft()).collect(Collectors.toList());
            eventBus.post(new BulkBillUpdateEvent(bills, LocalDateTime.now()));
            billIngestCache.clearCache();
        }
    }

    /** --- Member Methods --- */

    /**
     * Retrieves a member from the LBDC short name.  Creates a new unverified session member entry if no member can be retrieved.
     */
    protected SessionMember getMemberFromShortName(String shortName, SessionYear sessionYear, Chamber chamber) throws ParseError {
        return memberService.getMemberByShortNameEnsured(shortName, sessionYear, chamber);
    }

    /** --- Agenda Methods --- */

    /**
     * Retrieve an Agenda instance from the cache/backing store or create it if it does not exist.
     *
     * @param agendaId AgendaId - Retrieve Agenda via this agendaId.
     * @param fragment SobiFragment
     * @return Agenda
     */
    protected final Agenda getOrCreateAgenda(AgendaId agendaId, SobiFragment fragment) {
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
                agendaIngestCache.getCurrentCache().stream().map(entry -> entry.getLeft()).collect(Collectors.toList());
            eventBus.post(new BulkAgendaUpdateEvent(agendas, LocalDateTime.now()));
            agendaIngestCache.clearCache();
        }
    }

    /** --- Calendar Methods --- */

    /**
     * Retrieve a Calendar from the cache/backing store or create it if it does not exist.
     *
     * @param calendarId CalendarId - Retrieve Calendar via this calendarId.
     * @param fragment SobiFragment
     * @return Calendar
     */
    protected final Calendar getOrCreateCalendar(CalendarId calendarId, SobiFragment fragment) {
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
                calendarIngestCache.getCurrentCache().stream().map(entry -> entry.getLeft()).collect(Collectors.toList());
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