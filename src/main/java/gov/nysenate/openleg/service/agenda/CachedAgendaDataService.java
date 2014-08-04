package gov.nysenate.openleg.service.agenda;

import gov.nysenate.openleg.dao.agenda.AgendaDao;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CachedAgendaDataService implements AgendaDataService, CachingService
{
    @Autowired
    private AgendaDao agendaDao;

    @Override
    public void setupCaches() {

    }

    @Override
    public void evictCaches() {

    }

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws AgendaNotFoundEx {
        if (agendaId == null) {
            throw new IllegalArgumentException("AgendaId cannot be null.");
        }
        try {
            return agendaDao.getAgenda(agendaId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new AgendaNotFoundEx(agendaId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveAgenda(Agenda agenda, SobiFragment sobiFragment) {
        if (agenda == null) {
            throw new IllegalArgumentException("Agenda cannot be null when saving.");
        }
        agendaDao.updateAgenda(agenda, sobiFragment);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgenda(AgendaId agendaId) {
        //throw new NotImplementedException();
    }
}
