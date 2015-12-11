package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.service.agenda.data.CachedAgendaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaReportService extends BaseAgendaCheckReportService {

    @Autowired
    CachedAgendaDataService agendaDataService;

    /** {@inheritDoc} */
    @Override
    protected List<AgendaAlertInfoCommittee> getReferences(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<AgendaAlertInfoCommittee> references = agendaAlertDao.getUncheckedAgendaAlertReferences();
        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx(
                    String.format("no unchecked agenda references were found within the given range %s to %s", start, end));
        }
        return references;
    }

    /** {@inheritDoc} */
    @Override
    protected Agenda getAgenda(AgendaAlertInfoCommittee agendaAlertInfoCommittee) throws AgendaNotFoundEx {
        return agendaDataService.getAgenda(agendaAlertInfoCommittee.getWeekOf());
    }

    /** {@inheritDoc} */
    @Override
    protected void setReferenceChecked(AgendaAlertInfoCommittee reference) {
        agendaAlertDao.setAgendaAlertChecked(reference.getAgendaAlertInfoCommId(), true);
    }
}
