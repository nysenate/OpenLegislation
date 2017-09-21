package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.agenda.AgendaCommAddendumView;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OpenlegAgendaCheckService extends BaseSpotCheckService<CommitteeAgendaAddendumId, AgendaCommAddendumView, AgendaCommAddendumView> {

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaCommAddendumView content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaCommAddendumView content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId>  check(AgendaCommAddendumView content, AgendaCommAddendumView reference) {
        return null;
    }
}
