package gov.nysenate.openleg.spotchecks.alert.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.legislation.agenda.dao.CachedAgendaDataService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportRunMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IntervalAgendaReportService extends BaseAgendaCheckReportService{
    private final CachedAgendaDataService agendaDataService;

    @Autowired
    public IntervalAgendaReportService(CachedAgendaDataService agendaDataService) {
        this.agendaDataService = agendaDataService;
    }

    /** {@inheritDoc} */
    @Override
    protected List<AgendaAlertInfoCommittee> getReferences(LocalDateTime start, LocalDateTime end) {
        return agendaAlertDao.getAgendaAlertReferences(Range.closed(start, end));
    }

    /** {@inheritDoc}
     * @param agendaAlertInfoCommittee*/
    @Override
    protected Agenda getAgenda(AgendaAlertInfoCommittee agendaAlertInfoCommittee) throws AgendaNotFoundEx {
        return agendaDataService.getAgenda(agendaAlertInfoCommittee.getWeekOf());
    }

    /** {@inheritDoc} */
    @Override
    protected void setReferenceChecked(AgendaAlertInfoCommittee reference) {
        // Do nothing
    }

    @Override
    protected String getNotes() {
        return "digest";
    }

    @Override
    public SpotCheckReportRunMode getRunMode() {
        return SpotCheckReportRunMode.PERIODIC;
    }
}
