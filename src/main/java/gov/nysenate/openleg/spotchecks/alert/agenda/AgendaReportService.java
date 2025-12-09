package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.legislation.agenda.dao.CachedAgendaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaReportService extends BaseAgendaCheckReportService {
    private final CachedAgendaDataService agendaDataService;

    @Autowired
    public AgendaReportService(CachedAgendaDataService agendaDataService) {
        this.agendaDataService = agendaDataService;
    }

    /** {@inheritDoc} */
    @Override
    protected List<AgendaAlertInfoCommittee> getReferences(LocalDateTime start, LocalDateTime end) {
        return agendaAlertDao.getUncheckedAgendaAlertReferences();
    }

    /** {@inheritDoc} */
    @Override
    protected Agenda getAgenda(AgendaAlertInfoCommittee agendaAlertInfoCommittee) throws AgendaNotFoundEx {
        return agendaDataService.getAgenda(agendaAlertInfoCommittee.getWeekOf());
    }

    /** {@inheritDoc} */
    @Override
    protected void setReferenceChecked(AgendaAlertInfoCommittee reference) {
        agendaAlertDao.setAgendaAlertChecked(reference.getAgendaMeetingWeekId(), true);
    }
}
