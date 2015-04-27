package gov.nysenate.openleg.service.spotcheck.agenda;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.agenda.reference.AgendaAlertDao;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;
import static org.apache.commons.lang3.StringUtils.*;

@Service
public class AgendaSpotCheckService
        implements SpotCheckService<CommitteeAgendaAddendumId, AgendaInfoCommittee, AgendaAlertInfoCommittee> {

    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotCheckService.class);

    @Autowired
    AgendaAlertDao agendaAlertDao;

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaInfoCommittee content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaInfoCommittee content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaInfoCommittee content, AgendaAlertInfoCommittee reference) {
        final SpotCheckObservation<CommitteeAgendaAddendumId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(), content.getId());
        checkBills(observation, content, reference);
        checkChair(observation, content, reference);
        checkMeetingTime(observation, content, reference);
        checkLocation(observation, content, reference);
        checkNotes(observation, content, reference);
        // Some friendly logging
        int mismatchCount = observation.getMismatches().size();
        if (mismatchCount > 0) {
            logger.info("Committee Meeting Agenda {} | {} mismatch(es). | {}", content.getId(), mismatchCount, observation.getMismatchTypes());
        }
        return observation;
    }

    /** --- Internal Methods --- */

    private void checkBills(SpotCheckObservation<CommitteeAgendaAddendumId> obs,
                            AgendaInfoCommittee content, AgendaAlertInfoCommittee reference) {
        Set<String> refBills = new TreeSet<>();
        Set<String> contentBills = new TreeSet<>();
        reference.getItems().forEach(item -> refBills.add(item.getBillId() + " " + item.getMessage()));
        content.getItems().forEach(item -> contentBills.add(item.getBillId() + " " + item.getMessage()));

        if (!Sets.symmetricDifference(refBills, contentBills).isEmpty()) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_BILL_LISTING,
                    StringUtils.join(refBills, "\n"), StringUtils.join(contentBills, "\n")));
        }
    }

    private void checkChair(SpotCheckObservation<CommitteeAgendaAddendumId> obs,
                            AgendaInfoCommittee content, AgendaAlertInfoCommittee reference) {
        if (!StringUtils.equals(trim(content.getChair()), trim(reference.getChair()))) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_CHAIR, reference.getChair(), content.getChair()));
        }
    }

    private void checkMeetingTime(SpotCheckObservation<CommitteeAgendaAddendumId> obs,
                                  AgendaInfoCommittee content, AgendaAlertInfoCommittee reference) {
        if (content.getMeetingDateTime() == null
                || !content.getMeetingDateTime().equals(reference.getMeetingDateTime())) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_MEETING_TIME,
                    String.valueOf(content.getMeetingDateTime()), String.valueOf(reference.getMeetingDateTime())));
        }
    }

    private void checkLocation(SpotCheckObservation<CommitteeAgendaAddendumId> obs,
                               AgendaInfoCommittee content, AgendaAlertInfoCommittee reference) {
        if (!StringUtils.equals(trim(content.getLocation()), trim(reference.getLocation()))) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_LOCATION, reference.getLocation(), content.getLocation()));
        }
    }

    private void checkNotes(SpotCheckObservation<CommitteeAgendaAddendumId> obs,
                            AgendaInfoCommittee content, AgendaAlertInfoCommittee reference) {
        if (!StringUtils.equals(trim(content.getNotes()), trim(reference.getNotes()))) {
            obs.addMismatch(new SpotCheckMismatch(AGENDA_NOTES, reference.getNotes(), content.getNotes()));
        }
    }
}
