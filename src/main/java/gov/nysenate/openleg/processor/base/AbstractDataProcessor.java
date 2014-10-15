package gov.nysenate.openleg.processor.base;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.data.VetoDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;
import gov.nysenate.openleg.service.entity.CommitteeService;
import gov.nysenate.openleg.service.entity.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * The AbstractDataProcessor class is intended to serve as a common base for all the
 * data processors and provides functionality to fetch and persist various entity types.
 * This is to allow different processors to be consistent in how they utilize various data
 * operations.
 */
public abstract class AbstractDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProcessor.class);

    /** --- Data Services --- */

    @Autowired
    protected AgendaDataService agendaDataService;
    @Autowired
    protected BillDataService billDataService;
    @Autowired
    protected CalendarDataService calendarDataService;
    @Autowired
    protected CommitteeService committeeService;
    @Autowired
    protected MemberService memberService;
    @Autowired
    protected VetoDataService vetoDataService;

    /** --- Bill Methods --- */

    /**
     * Retrieves/creates the Bill without checking a cache.
     *
     * @param publishDate Date
     * @param billId BillId
     * @return Bill
     */
    protected Bill getOrCreateBaseBill(LocalDateTime publishDate, BillId billId) {
        return getOrCreateBaseBill(publishDate, billId, null);
    }

    /**
     * Retrieves the base Bill container using the given billId from either the cache or the service layer.
     * If this base bill does not exist, it will be created. The amendment instance will also be created
     * if it does not exist.
     *
     * @param publishDate Date - Typically the date of the source data file. Only used when bill information
     *                           does not already exist and must be created.
     * @param billId BillId - The BillId to find a matching Bill for.
     * @param billIngestCache IngestCache<BillId, Bill> - Optional cache used to speed up processing.
     * @return Bill
     */
    protected Bill getOrCreateBaseBill(LocalDateTime publishDate, BillId billId, IngestCache<BaseBillId, Bill> billIngestCache) {
        boolean isBaseVersion = BillId.isBaseVersion(billId.getVersion());
        BaseBillId baseBillId = BillId.getBaseId(billId);
        Bill baseBill;
        try {
            baseBill = getBaseBillFromCacheOrService(baseBillId, billIngestCache);
        }
        catch (BillNotFoundEx ex) {
            // Create the bill since it does not exist and add it to the ingest cache.
            if (!isBaseVersion) {
                logger.warn("Bill Amendment {} filed without initial bill.", billId);
            }
            baseBill = new Bill(baseBillId);
            baseBill.setModifiedDateTime(publishDate);
            baseBill.setPublishedDateTime(publishDate);
            if (billIngestCache != null) {
                billIngestCache.set(baseBillId, baseBill);
            }
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
     * Helper method to retrieve Bill references from either the cache or the BillDataService.
     *
     * @param billId BillId
     * @param billIngestCache IngestCache<BillId, Bill> (can be null)
     * @return Bill
     * @throws BillNotFoundEx - If the bill was not found by the service.
     */
    protected Bill getBaseBillFromCacheOrService(BillId billId, IngestCache<BaseBillId, Bill> billIngestCache) throws BillNotFoundEx {
        if (billId == null) {
            throw new IllegalArgumentException("Bill Id cannot be null!");
        }
        // Ensure bill id references the base bill id since the cache will not distinguish.
        BaseBillId baseBillId = BillId.getBaseId(billId);
        // Try the cache, otherwise use the service which can throw the BillNotFoundEx exception.
        boolean isCached = (billIngestCache != null) && billIngestCache.has(baseBillId);
        logger.trace("Bill ingest cache " + ((isCached) ? "HIT" : "MISS") + " for bill id " + baseBillId);
        return (isCached) ? billIngestCache.get(baseBillId) : billDataService.getBill(baseBillId);
    }

    /** --- Member Methods --- */

    /**
     * Retrieves a member from the LBDC short name.  Creates a new unverified session member entry if no member can be retrieved.
     */
    protected Member getMemberFromShortName(String shortName, SessionYear sessionYear, Chamber chamber) {
        if (StringUtils.isNotBlank(shortName)) {
            return memberService.getMemberByShortNameEnsured(shortName, sessionYear, chamber);
        }
        return null;
    }

    /** --- Agenda Methods --- */

    /**
     * Retrieve an Agenda instance from the backing store or create it if it does not exist.
     *
     * @param agendaId AgendaId - Retrieve Agenda via this agendaId.
     * @param date Date - The published date of the requesting sobi fragment.
     * @return Agenda
     */
    protected Agenda getOrCreateAgenda(AgendaId agendaId, LocalDateTime date) {
        Agenda agenda;
        try {
            agenda = agendaDataService.getAgenda(agendaId);
        }
        catch (AgendaNotFoundEx ex) {
            agenda = new Agenda(agendaId);
            agenda.setModifiedDateTime(date);
            agenda.setPublishedDateTime(date);
        }
        return agenda;
    }

    /** --- Calendar Methods --- */

    /**
     * Retrieve a Calendar from the backing store or create it if it does not exist.
     *
     * @param calendarId CalendarId - Retrieve Calendar via this calendarId.
     * @param date Date - The published date of the requesting sobi fragment.
     * @return Calendar
     */
    protected Calendar getOrCreateCalendar(CalendarId calendarId, LocalDateTime date) {
        Calendar calendar;
        try {
            calendar = calendarDataService.getCalendar(calendarId);
        }
        catch (CalendarNotFoundEx ex) {
            calendar = new Calendar(calendarId);
            calendar.setModifiedDateTime(date);
            calendar.setPublishedDateTime(date);
        }
        return calendar;
    }
}