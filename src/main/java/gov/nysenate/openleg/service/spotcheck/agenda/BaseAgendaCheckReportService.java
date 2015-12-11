package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.agenda.reference.AgendaAlertDao;
import gov.nysenate.openleg.dao.spotcheck.CommitteeAgendaReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAgendaCheckReportService extends BaseSpotCheckReportService<CommitteeAgendaAddendumId> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgendaCheckReportService.class);

    @Autowired
    CommitteeAgendaReportDao reportDao;

    @Autowired
    AgendaSpotCheckService checkService;

    @Autowired
    AgendaAlertDao agendaAlertDao;

    @Autowired
    Environment environment;

    /** --- Implemented Methods --- */

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_AGENDA_ALERT;
    }

    @Override
    protected SpotCheckReportDao<CommitteeAgendaAddendumId> getReportDao() {
        return reportDao;
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<CommitteeAgendaAddendumId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx, Exception {
        // Create a new report instance
        SpotCheckReport<CommitteeAgendaAddendumId> report = new SpotCheckReport<>();

        // Get unchecked references from within the specified time range
        logger.info("Getting agenda references...");
        List<AgendaAlertInfoCommittee> references = getReferences(start, end);

        // Use the earliest reference date as the report reference date
        references.sort((a, b) -> a.getReferenceId().getRefActiveDateTime().compareTo(b.getReferenceId().getRefActiveDateTime()));
        SpotCheckReferenceId refId = references.get(0).getReferenceId();

        // The report date/time should be truncated to the second to make it easier to query
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_AGENDA_ALERT,
                refId.getRefActiveDateTime(),
                LocalDateTime.now()));

        logger.info("Checking references...");
        // Check the references to generate observations, which are added to the report
        report.addObservations(getObservations(references));
        report.setNotes(getNotes());

        return report;
    }

    /** --- Internal Methods --- */

    /**
     * Gets all eligible Agenda Alert references with reference dates between the start and end date times
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
     * @param reference AgendaAlertInfoCommittee
     */
    protected abstract void setReferenceChecked(AgendaAlertInfoCommittee reference);

    protected String getNotes() {
        return null;
    }

    /**
     * Given a list of references, performs checks on the corresponding data, generating a list of observations
     * @return List<SpotcheckObservation>
     */
    protected List<SpotCheckObservation<CommitteeAgendaAddendumId>> getObservations(List<AgendaAlertInfoCommittee> references) {
        List<SpotCheckObservation<CommitteeAgendaAddendumId>> observations = new ArrayList<>();
        List<AgendaAlertInfoCommittee> checkedReferences = new ArrayList<>();
        for (AgendaAlertInfoCommittee reference : references) {
            Agenda agenda = null;
            try {
                // Attempt to get the committee meeting info that corresponds to the reference
                agenda = getAgenda(reference);
                String addendumId = reference.getAddendum().getValue();
                CommitteeId committeeId = reference.getCommitteeId();
                if (agenda.getAgendaInfoAddendum(addendumId) == null
                        || agenda.getAgendaInfoAddendum(addendumId).getCommittee(committeeId) == null) {
                    throw new AgendaNotFoundEx("could not find committee meeting addendum " + reference.getAgendaAlertInfoCommId());
                }
                AgendaInfoCommittee content = agenda.getAgendaInfoAddendum(addendumId).getCommittee(committeeId);

                // Check the content against the reference, generating an observation
                observations.add(checkService.check(content, reference));
                checkedReferences.add(reference);
            } catch (AgendaNotFoundEx ex) {
                // Add a data not found mismatch if the reference is past its grace period
                if (LocalDateTime.now().minus(environment.getSpotcheckAlertGracePeriod())
                        .isAfter(reference.getReferenceId().getRefActiveDateTime())) {
                    // Add a missing data observation if the committee meeting info was not found
                    AgendaId obsAgId = agenda != null ? agenda.getId()
                            : ex.getAgendaId() != null ? ex.getAgendaId()
                            : new AgendaId(reference.getMeetingDateTime().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), 0);
                    SpotCheckObservation<CommitteeAgendaAddendumId> obs = new SpotCheckObservation<>(reference.getReferenceId(),
                            reference.getAgendaAlertInfoCommId().getCommiteeAgendaAddendumId(obsAgId));
                    obs.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING,
                            "", reference.getAgendaAlertInfoCommId().toString()));
                    observations.add(obs);
                    logger.info("Committee Meeting Agenda {} | {} mismatch(es). | {}",
                            reference.getAgendaAlertInfoCommId(), obs.getMismatches().size(), obs.getMismatchTypes(false));
                    checkedReferences.add(reference);
                }
            }
        }
        // Cancel the report if no references were checked
        if (checkedReferences.isEmpty()) {
            throw new SpotCheckAbortException();
        }
        checkedReferences.forEach(this::setReferenceChecked);
        return observations;
    }
}
