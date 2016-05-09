package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class AgendaCheckServices extends BaseSpotCheckService<AgendaId, Agenda, SenateSiteAgenda> {
    @Override
    public SpotCheckObservation<AgendaId> check(Agenda content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<AgendaId> check(Agenda content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<AgendaId> check(Agenda content, SenateSiteAgenda reference) {
        SpotCheckObservation<AgendaId> observation = new SpotCheckObservation<>(reference.getReferenceId(), reference.getAgendaId());
        checkAgendaId(content,reference,observation);
        return observation;
    }

    private void checkAgendaId(Agenda content, SenateSiteAgenda reference, SpotCheckObservation<AgendaId> observation) {
        checkString(content.getId().toString(),reference.getAgendaId().toString(),observation, SpotCheckMismatchType.AGENDA_ID);
    }
}
