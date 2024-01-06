package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoAddendum;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.spotchecks.alert.agenda.dao.AgendaAlertDao;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;
import gov.nysenate.openleg.spotchecks.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseAgendaCheckReportService implements SpotCheckReportService<AgendaMeetingWeekId> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgendaCheckReportService.class);

    @Autowired
    AgendaSpotCheckService checkService;

    @Autowired
    AgendaAlertDao agendaAlertDao;

    @Autowired
    OpenLegEnvironment environment;

    /* --- Implemented Methods --- */

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_AGENDA_ALERT;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReferenceDataNotFoundEx if no references are checked in the report.
     */
    @Override
    public SpotCheckReport<AgendaMeetingWeekId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        logger.info("Getting agenda references...");
        // Get all unchecked references outside of the grace period.
        List<AgendaAlertInfoCommittee> allReferences = getReferences(start, end);
        List<AgendaAlertInfoCommittee> references = allReferences.stream()
                .filter(this::outsideGracePeriod)
                .toList();

        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx( "All unchecked agenda references (" +
                            allReferences.size() +
                            ") were still in their grace period.");
        }

        SpotCheckReport<AgendaMeetingWeekId> report = initSpotcheckReport(references);

        // Add references to a map, keyed by AgendaAlertCheckId
        Map<AgendaMeetingWeekId, AgendaAlertInfoCommittee> referenceMap = references.stream()
                .collect(Collectors.toMap(AgendaAlertInfoCommittee::getAgendaMeetingWeekId, Function.identity()));
        // Get associated Openleg data and transform it into the same data types to make comparisons easy.
        Map<AgendaMeetingWeekId, AgendaAlertInfoCommittee> observedMap = createObservedMap(references);

        logger.info("Checking references...");
        for (AgendaMeetingWeekId refKey : referenceMap.keySet()) {
            // Check for observedDataMissing mismatches
            if (observedMap.get(refKey) == null) {
                SpotCheckObservation<AgendaMeetingWeekId> ob = new SpotCheckObservation<>(referenceMap.get(refKey).getReferenceId(), refKey);
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

    private SpotCheckReport<AgendaMeetingWeekId> initSpotcheckReport(List<AgendaAlertInfoCommittee> references) {
        SpotCheckReport<AgendaMeetingWeekId> report = new SpotCheckReport<>();

        // Use the earliest reference date as the report reference date
        references.sort(Comparator.comparing(a -> a.getReferenceId().getRefActiveDateTime()));
        SpotCheckReferenceId refId = references.get(0).getReferenceId();

        report.setReportId(new SpotCheckReportId(refId.getReferenceType(), refId.getRefActiveDateTime(), LocalDateTime.now()));
        return report;
    }

    // Fetches Openleg Agenda data corresponding to the given references.
    // Transforms Openleg data into AgendaAlertInfoCommittee objects for simpler comparisons.
    private Map<AgendaMeetingWeekId, AgendaAlertInfoCommittee> createObservedMap(List<AgendaAlertInfoCommittee> references) {
        Map<AgendaMeetingWeekId, AgendaAlertInfoCommittee> observedMap = new HashMap<>();
        for (AgendaAlertInfoCommittee ref : references) {
            Agenda observedAgenda = getAgendaOrNull(ref);
            if (observedAgenda == null) {
                continue;
            }
            AgendaAlertInfoCommittee observedAlertCommittee = transformToAgendaAlertInfoCommittee(observedAgenda, ref);
            observedMap.put(observedAlertCommittee.getAgendaMeetingWeekId(), observedAlertCommittee);
        }
        return observedMap;
    }

    // Return an agenda which contains the InfoAddendum and InfoCommittee corresponding to this reference or null.
    private Agenda getAgendaOrNull(AgendaAlertInfoCommittee reference) {
        try {
            Agenda a = getAgenda(reference);
            if (isAgendaMissingInfoAddendum(a, reference) || isAgendaMissingInfoCommittee(a, reference)) {
                throw new AgendaNotFoundEx("could not find committee meeting addendum " + reference.getAgendaMeetingWeekId());
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
