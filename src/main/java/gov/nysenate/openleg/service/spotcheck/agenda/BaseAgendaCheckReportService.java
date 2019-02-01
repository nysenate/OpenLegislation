package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.agenda.reference.AgendaAlertDao;
import gov.nysenate.openleg.dao.spotcheck.AgendaAlertReportDao;
import gov.nysenate.openleg.dao.spotcheck.CommitteeAgendaReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertCheckId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseAgendaCheckReportService extends BaseSpotCheckReportService<AgendaAlertCheckId> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgendaCheckReportService.class);

    @Autowired
    AgendaAlertReportDao reportDao;

    @Autowired
    AgendaSpotCheckService checkService;

    @Autowired
    AgendaAlertDao agendaAlertDao;

    @Autowired
    Environment environment;

    /**
     * --- Implemented Methods ---
     */

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_AGENDA_ALERT;
    }

    @Override
    protected SpotCheckReportDao<AgendaAlertCheckId> getReportDao() {
        return reportDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckReport<AgendaAlertCheckId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx, Exception {
        logger.info("Getting agenda references...");
        // Get all unchecked references outside of the grace period.
        List<AgendaAlertInfoCommittee> references = getReferences(start, end).stream()
                .filter(this::outsideGracePeriod).collect(Collectors.toList());

        SpotCheckReport<AgendaAlertCheckId> report = initSpotcheckReport(references);

        // Add references to a map, keyed by AgendaAlertCheckId
        Map<AgendaAlertCheckId, AgendaAlertInfoCommittee> referenceMap = references.stream()
                .collect(Collectors.toMap(AgendaAlertInfoCommittee::getAgendaAlertCheckId, Function.identity()));
        // Get associated Openleg data and transform it into the same data types to make comparisons easy.
        Map<AgendaAlertCheckId, AgendaAlertInfoCommittee> observedMap = createObservedMap(references);

        logger.info("Checking references...");
        for (AgendaAlertCheckId refKey : referenceMap.keySet()) {
            // Check for observedDataMissing mismatches
            if (observedMap.get(refKey) == null) {
                SpotCheckObservation<AgendaAlertCheckId> ob = new SpotCheckObservation<>(referenceMap.get(refKey).getReferenceId(), refKey);
                ob.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", refKey));
                report.addObservation(ob);
            } else {
                // Compare content
                report.addObservation(checkService.check(observedMap.get(refKey), referenceMap.get(refKey)));
            }
            // Mark reference as checked
            setReferenceChecked(referenceMap.get(refKey));
        }

        report.setNotes(getNotes());
        return report;
    }

    /**
     * --- Internal Methods ---
     */

    // We don't perform spotchecks on alert references inside a grace period to give openleg time to process data.
    private boolean outsideGracePeriod(AgendaAlertInfoCommittee ref) {
        return LocalDateTime.now().minus(environment.getSpotcheckAlertGracePeriod())
                .isAfter(ref.getReferenceId().getRefActiveDateTime());
    }

    private SpotCheckReport<AgendaAlertCheckId> initSpotcheckReport(List<AgendaAlertInfoCommittee> references) {
        SpotCheckReport<AgendaAlertCheckId> report = new SpotCheckReport<>();

        // Use the earliest reference date as the report reference date
        references.sort(Comparator.comparing(a -> a.getReferenceId().getRefActiveDateTime()));
        SpotCheckReferenceId refId = references.get(0).getReferenceId();

        report.setReportId(new SpotCheckReportId(refId.getReferenceType(), refId.getRefActiveDateTime(), LocalDateTime.now()));
        return report;
    }

    // Fetches Openleg Agenda data corresponding to the given references.
    // Transforms Openleg data into AgendaAlertInfoCommittee objects for simpler comparisons.
    private Map<AgendaAlertCheckId, AgendaAlertInfoCommittee> createObservedMap(List<AgendaAlertInfoCommittee> references) {
        Map<AgendaAlertCheckId, AgendaAlertInfoCommittee> observedMap = new HashMap<>();
        for (AgendaAlertInfoCommittee ref : references) {
            Agenda observedAgenda = getAgendaOrNull(ref);
            if (observedAgenda == null) {
                continue;
            }
            AgendaAlertInfoCommittee observedAlertCommittee = transformToAgendaAlertInfoCommittee(observedAgenda, ref);
            observedMap.put(observedAlertCommittee.getAgendaAlertCheckId(), observedAlertCommittee);
        }
        return observedMap;
    }

    // Return an agenda which contains the InfoAddendum and InfoCommittee corresponding to this reference or null.
    private Agenda getAgendaOrNull(AgendaAlertInfoCommittee reference) {
        try {
            Agenda a = getAgenda(reference);
            if (isAgendaMissingInfoAddendum(a, reference) || isAgendaMissingInfoCommittee(a, reference)) {
                throw new AgendaNotFoundEx("could not find committee meeting addendum " + reference.getAgendaAlertInfoCommId());
            }
            return a;
        } catch (AgendaNotFoundEx ex) {
            return null;
        }
    }

    private boolean isAgendaMissingInfoAddendum(Agenda a, AgendaAlertInfoCommittee reference) {
        return a.getAgendaInfoAddendum(reference.getAddendum().toString()) == null;
    }

    private boolean isAgendaMissingInfoCommittee(Agenda a, AgendaAlertInfoCommittee reference) {
        return a.getAgendaInfoAddendum(reference.getAddendum().toString()).getCommittee(reference.getCommitteeId()) == null;
    }

    private AgendaAlertInfoCommittee transformToAgendaAlertInfoCommittee(Agenda agenda, AgendaAlertInfoCommittee ref) {
        AgendaInfoAddendum observedAddendum = agenda.getAgendaInfoAddendum(ref.getAddendum().toString());
        AgendaInfoCommittee observedCommittee = observedAddendum.getCommittee(ref.getCommitteeId());

        AgendaAlertInfoCommittee observedAlertCommittee = new AgendaAlertInfoCommittee();
        observedAlertCommittee.setWeekOf(observedAddendum.getWeekOf());
        observedAlertCommittee.setAddendum(observedCommittee.getAddendum());
        observedAlertCommittee.setCommitteeId(observedCommittee.getCommitteeId());
        observedAlertCommittee.setChair(observedCommittee.getChair());
        observedAlertCommittee.setLocation(observedCommittee.getLocation());
        observedAlertCommittee.setMeetingDateTime(observedCommittee.getMeetingDateTime());
        observedAlertCommittee.setNotes(observedCommittee.getNotes());
        observedAlertCommittee.setItems(observedCommittee.getItems());
        return observedAlertCommittee;
    }

    /**
     * Gets all eligible Agenda Alert references with reference dates between the start and end date times
     *
     * @throws ReferenceDataNotFoundEx if no references were found
     */
    protected abstract List<AgendaAlertInfoCommittee> getReferences(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx;

    /**
     * Retrieves an agenda for the week of the given date
     *
     * @param agendaAlertInfoCommittee@return Agenda
     * @throws AgendaNotFoundEx
     */
    protected abstract Agenda getAgenda(AgendaAlertInfoCommittee agendaAlertInfoCommittee) throws AgendaNotFoundEx;

    /**
     * Marks the given reference as being checked
     *
     * @param reference AgendaAlertInfoCommittee
     */
    protected abstract void setReferenceChecked(AgendaAlertInfoCommittee reference);

    protected String getNotes() {
        return null;
    }
}
