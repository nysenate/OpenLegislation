package gov.nysenate.openleg.service.spotcheck.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.agenda.reference.AgendaAlertDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.CommitteeAgendaReportDao;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.agenda.reference.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgendaCheckReportService implements SpotCheckReportService<CommitteeAgendaAddendumId> {

    private static final Logger logger = LoggerFactory.getLogger(AgendaCheckReportService.class);

    @Autowired
    CommitteeAgendaReportDao reportDao;

    @Autowired
    AgendaSpotCheckService checkService;

    @Autowired
    AgendaAlertDao agendaAlertDao;

    @Autowired
    AgendaDataService agendaDataService;

    /** --- Implemented Methods --- */

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_AGENDA_ALERT;
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<CommitteeAgendaAddendumId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
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
                refId.getRefActiveDateTime().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        logger.info("Checking references...");
        // Check the references to generate observations, which are added to the report
        report.addObservations(getObservations(references));

        return report;
    }

    /** {@inheritDoc} */
    @Override
    public void saveReport(SpotCheckReport<CommitteeAgendaAddendumId> report) {
        reportDao.saveReport(report);
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<CommitteeAgendaAddendumId> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId cannot be null");
        }
        try {
            return reportDao.getReport(reportId);
        } catch (EmptyResultDataAccessException ex) {
            throw new SpotCheckReportNotFoundEx(reportId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReportId> getReportIds(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return reportDao.getReportIds(SpotCheckRefType.LBDC_AGENDA_ALERT, start, end, dateOrder, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId cannot be null");
        }
        reportDao.deleteReport(reportId);
    }

    /** --- Internal Methods --- */

    /**
     * Gets all Agenda Alert references with reference dates between the start and end date times
     * @throws ReferenceDataNotFoundEx if no references were found
     */
    protected List<AgendaAlertInfoCommittee> getReferences(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<AgendaAlertInfoCommittee> references = agendaAlertDao.getUncheckedAgendaAlertReferences(Range.closed(start, end));
        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx(
                    String.format("no unchecked agenda references were found within the given range %s to %s", start, end));
        }
        return references;
    }

    /**
     * Given a list of references, performs checks on the corresponding data, generating a list of observations
     * @return List<SpotcheckObservation>
     */
    protected List<SpotCheckObservation<CommitteeAgendaAddendumId>> getObservations(List<AgendaAlertInfoCommittee> references) {
        List<SpotCheckObservation<CommitteeAgendaAddendumId>> observations = new ArrayList<>();
        int unknownAgendaId = 0;
        for (AgendaAlertInfoCommittee reference : references) {
            Agenda agenda = null;
            try {
                // Attempt to get the committee meeting info that corresponds to the reference
                agenda = getAgenda(reference.getWeekOf());
                String addendumId = reference.getAddendum().getValue();
                CommitteeId committeeId = reference.getCommitteeId();
                if (agenda.getAgendaInfoAddendum(addendumId) == null
                        || agenda.getAgendaInfoAddendum(addendumId).getCommittee(committeeId) == null) {
                    throw new AgendaNotFoundEx("could not find committee meeting addendum " + reference.getAgendaAlertInfoCommId());
                }
                AgendaInfoCommittee content = agenda.getAgendaInfoAddendum(addendumId).getCommittee(committeeId);

                // Check the content against the reference, generating an observation
                observations.add(checkService.check(content, reference));

            } catch (AgendaNotFoundEx ex) {
                // Add a missing data observation if the committee meeting info was not found
                SpotCheckObservation<CommitteeAgendaAddendumId> obs = new SpotCheckObservation<>(reference.getReferenceId(),
                        reference.getAgendaAlertInfoCommId()
                                .getCommiteeAgendaAddendumId(agenda != null ? agenda.getId() : new AgendaId(unknownAgendaId++, 0)));
                obs.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING,
                        reference.getAgendaAlertInfoCommId().toString(), ""));
                observations.add(obs);
                logger.info("Committee Meeting Agenda {} | {} mismatch(es). | {}",
                        reference.getAgendaAlertInfoCommId(), obs.getMismatches().size(), obs.getMismatchTypes());
            }
            setReferenceChecked(reference);
        }
        return observations;
    }

    protected Agenda getAgenda(LocalDate weekOf) throws AgendaNotFoundEx {
        return agendaDataService.getAgenda(weekOf);
    }

    protected void setReferenceChecked(AgendaAlertInfoCommittee reference) {
        agendaAlertDao.setAgendaAlertChecked(reference.getAgendaAlertInfoCommId(), true);
    }
}
