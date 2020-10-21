package gov.nysenate.openleg.spotchecks.alert.agenda;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.base.SpotCheckService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.*;

@Service
public class AgendaSpotCheckService
        implements SpotCheckService<AgendaMeetingWeekId, AgendaAlertInfoCommittee, AgendaAlertInfoCommittee> {

    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotCheckService.class);

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<AgendaMeetingWeekId> check(AgendaAlertInfoCommittee observed, AgendaAlertInfoCommittee reference) {
        final SpotCheckObservation<AgendaMeetingWeekId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(), reference.getAgendaMeetingWeekId());

        checkBills(observation, observed, reference);
        checkChair(observation, observed, reference);
        checkMeetingTime(observation, observed, reference);
        checkLocation(observation, observed, reference);
        checkNotes(observation, observed, reference);

        // Some friendly logging
        if (observation.getMismatches().size() > 0) {
            logger.info("Agenda Alert Check Id {} | {} mismatch(es). | {}",
                    reference.getAgendaMeetingWeekId(), observation.getMismatches().size(), observation.getMismatchTypes(false));
        }

        return observation;
    }

    /** --- Internal Methods --- */

    private void checkBills(SpotCheckObservation<AgendaMeetingWeekId> obs,
                            AgendaAlertInfoCommittee observed, AgendaAlertInfoCommittee reference) {
        Set<String> refBills = new TreeSet<>();
        Set<String> obsBills = new TreeSet<>();
        reference.getItems().forEach(item -> refBills.add(item.getBillId() + " " + item.getMessage()));
        observed.getItems().forEach(item -> obsBills.add(item.getBillId() + " " + item.getMessage()));

        if (!Sets.symmetricDifference(refBills, obsBills).isEmpty()) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_BILL_LISTING,
                    StringUtils.join(obsBills, "\n"), StringUtils.join(refBills, "\n")));
        }
    }

    private void checkChair(SpotCheckObservation<AgendaMeetingWeekId> obs,
                            AgendaAlertInfoCommittee observed, AgendaAlertInfoCommittee reference) {
        String refChair = StringUtils.trim(reference.getChair());
        String obsChair = StringUtils.trim(observed.getChair());
        if (!StringUtils.equals(refChair, obsChair)) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_CHAIR, obsChair, refChair));
        }
    }

    private void checkMeetingTime(SpotCheckObservation<AgendaMeetingWeekId> obs,
                                  AgendaAlertInfoCommittee observed, AgendaAlertInfoCommittee reference) {
        if (observed.getMeetingDateTime() == null
                || !observed.getMeetingDateTime().equals(reference.getMeetingDateTime())) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_MEETING_TIME,
                    String.valueOf(observed.getMeetingDateTime()), String.valueOf(reference.getMeetingDateTime())));
        }
    }

    private void checkLocation(SpotCheckObservation<AgendaMeetingWeekId> obs,
                               AgendaAlertInfoCommittee observed, AgendaAlertInfoCommittee reference) {
        String refLocation = StringUtils.trim(reference.getLocation());
        String obsLocation = StringUtils.trim(observed.getLocation());
        if (!StringUtils.equals(refLocation, obsLocation)) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_LOCATION, obsLocation, refLocation));
        }
    }

    private void checkNotes(SpotCheckObservation<AgendaMeetingWeekId> obs,
                            AgendaAlertInfoCommittee observed, AgendaAlertInfoCommittee reference) {
        String refNotes = StringUtils.trim(reference.getNotes());
        String obsNotes = StringUtils.trim(observed.getNotes());
        if (!StringUtils.equals(refNotes, obsNotes)) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_NOTES, obsNotes, refNotes));
        }
    }
}
