package gov.nysenate.openleg.search.agenda;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.Tuple;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.legislation.agenda.dao.ElasticAgendaSearchDao;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.agenda.AgendaUpdateEvent;
import gov.nysenate.openleg.updates.agenda.BulkAgendaUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ElasticAgendaSearchService extends IndexedSearchService<Tuple<Agenda, CommitteeId>>
        implements AgendaSearchService {
    private final ElasticAgendaSearchDao agendaSearchDao;
    private final AgendaDataService agendaDataService;

    @Autowired
    public ElasticAgendaSearchService(ElasticAgendaSearchDao agendaSearchDao,
                                      AgendaDataService agendaDataService, EventBus eventBus) {
        super(agendaSearchDao);
        this.agendaSearchDao = agendaSearchDao;
        this.agendaDataService = agendaDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String queryStr, Integer year,
                                                                   String sort, LimitOffset limOff) throws SearchException {
        return agendaSearchDao.searchForIds(queryStr, sort, limOff, getYearQuery("agenda.id.year", year));
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        for (int year = 2009; year <= LocalDate.now().getYear(); year++) {
            List<AgendaId> agendaIds = agendaDataService.getAgendaIds(year, SortOrder.ASC);
            List<Agenda> agendas = agendaIds.stream().map(agendaDataService::getAgenda).toList();
            agendaSearchDao.indexAgendas(agendas);
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleAgendaUpdateEvent(AgendaUpdateEvent agendaUpdateEvent) {
        if (agendaUpdateEvent != null && agendaUpdateEvent.agenda() != null) {
            agendaSearchDao.indexAgendas(List.of(agendaUpdateEvent.agenda()));
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleBulkAgendaUpdateEvent(BulkAgendaUpdateEvent bulkAgendaUpdateEvent) {
        if (bulkAgendaUpdateEvent != null && !bulkAgendaUpdateEvent.agendas().isEmpty()) {
            agendaSearchDao.indexAgendas(bulkAgendaUpdateEvent.agendas());
        }
    }
}
