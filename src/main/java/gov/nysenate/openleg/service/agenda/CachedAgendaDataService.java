package gov.nysenate.openleg.service.agenda;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Service
public class CachedAgendaDataService implements AgendaDataService, CachingService
{
    @Override
    public void setupCaches() {

    }

    @Override
    public void evictCaches() {

    }

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws AgendaNotFoundEx {
        throw new AgendaNotFoundEx();
    }

    /** {@inheritDoc} */
    @Override
    public void saveAgenda(Agenda agenda, SobiFragment sobiFragment) {
        //throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgenda(AgendaId agendaId) {
        //throw new NotImplementedException();
    }
}
