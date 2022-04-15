package gov.nysenate.openleg.legislation.agenda.dao;

import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.updates.agenda.AgendaUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CachedAgendaDataService extends CachingService<AgendaId, Agenda> implements AgendaDataService {
    private static final Logger logger = LoggerFactory.getLogger(CachedAgendaDataService.class);

    @Autowired
    private AgendaDao agendaDao;

    @Override
    protected CacheType cacheType() {
        return CacheType.AGENDA;
    }

    /**
     * Clears out the cache, then fills it with the 8 most recent agendas from this year.
     */
    public void warmCaches() {
        evictCache();
        logger.info("Warming up agenda cache.");
        List<AgendaId> ids = getAgendaIds(LocalDate.now().getYear(), SortOrder.DESC);
        // Adds up to the most recent 8 agendas.
        for (int i = 0; i < Math.min(7, ids.size()); i++)
            getAgenda(ids.get(i));
        logger.info("Done warming up agenda cache.");
    }

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws AgendaNotFoundEx {
        if (agendaId == null) {
            throw new IllegalArgumentException("AgendaId cannot be null.");
        }
        try {
            Agenda agenda = getCacheValue(agendaId);
            if (agenda == null) {
                agenda = agendaDao.getAgenda(agendaId);
                putCacheEntry(agendaId, agenda);
            }
            return agenda;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new AgendaNotFoundEx(agendaId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(LocalDate weekOf) throws AgendaNotFoundEx {
        try {
            return agendaDao.getAgenda(weekOf);
        } catch (EmptyResultDataAccessException ex) {
            throw new AgendaNotFoundEx(weekOf);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<AgendaId> getAgendaIds(int year, SortOrder idOrder) {
        return agendaDao.getAgendaIds(year, idOrder);
    }

    /** {@inheritDoc} */
    @Override
    public void saveAgenda(Agenda agenda, LegDataFragment legDataFragment, boolean postUpdateEvent) {
        if (agenda == null) {
            throw new IllegalArgumentException("Agenda cannot be null when saving.");
        }
        logger.debug("Persisting agenda {}", agenda.getId());
        agendaDao.updateAgenda(agenda, legDataFragment);
        putCacheEntry(agenda.getId(), agenda);
        if (postUpdateEvent) {
            eventBus.post(new AgendaUpdateEvent(agenda, LocalDateTime.now()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgenda(AgendaId agendaId) {
        agendaDao.deleteAgenda(agendaId);
        evictContent(agendaId);
    }
}