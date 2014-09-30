package gov.nysenate.openleg.service.agenda.data;

import gov.nysenate.openleg.dao.agenda.AgendaDao;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CachedAgendaDataService implements AgendaDataService, CachingService
{
    private static final String agendaCache = "agendaData";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AgendaDao agendaDao;

    @Override
    @PostConstruct
    public void setupCaches() {
        cacheManager.addCache(agendaCache);
    }

    @Override
    @CacheEvict(value = agendaCache, allEntries = true)
    public void evictCaches() {}

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = agendaCache, key = "#agendaId")
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
    @CacheEvict(value = agendaCache, key = "#agenda.getId()")
    public void saveAgenda(Agenda agenda, SobiFragment sobiFragment) {
        if (agenda == null) {
            throw new IllegalArgumentException("Agenda cannot be null when saving.");
        }
        agendaDao.updateAgenda(agenda, sobiFragment);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = agendaCache, key = "#agendaId")
    public void deleteAgenda(AgendaId agendaId) {
        agendaDao.deleteAgenda(agendaId);
    }
}
