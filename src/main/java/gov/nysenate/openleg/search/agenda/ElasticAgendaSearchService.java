package gov.nysenate.openleg.search.agenda;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.legislation.agenda.dao.ElasticAgendaSearchDao;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.agenda.AgendaUpdateEvent;
import gov.nysenate.openleg.updates.agenda.BulkAgendaUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class ElasticAgendaSearchService implements AgendaSearchService, IndexedSearchService<Agenda> {
    private static final Logger logger = LoggerFactory.getLogger(ElasticAgendaSearchService.class);

    private final OpenLegEnvironment env;
    private final ElasticAgendaSearchDao agendaSearchDao;
    private final AgendaDataService agendaDataService;

    @Autowired
    public ElasticAgendaSearchService(OpenLegEnvironment env,
                                      ElasticAgendaSearchDao agendaSearchDao,
                                      AgendaDataService agendaDataService, EventBus eventBus) {
        this.env = env;
        this.agendaSearchDao = agendaSearchDao;
        this.agendaDataService = agendaDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, Integer year,
                                                                   String sort, LimitOffset limOff) throws SearchException {
        if (limOff == null) {
            limOff = LimitOffset.ALL;
        }
        return agendaSearchDao.searchForIds(
                IndexedSearchService.getBasicBoolQuery("agenda.id.year", year, query),
                ElasticSearchServiceUtils.extractSortBuilders(sort), limOff
        );
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Agenda agenda) {
        if (env.isElasticIndexing()) {
            logger.info("Indexing agenda {} into elastic search.", agenda.getId());
            agendaSearchDao.indexAgendas(List.of(agenda));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Agenda> agendas) {
        if (env.isElasticIndexing()) {
            logger.info("Indexing {} agendas into elastic search.", agendas.size());
            agendaSearchDao.indexAgendas(agendas);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        agendaSearchDao.purgeIndices();
        agendaSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        for (int year = 2009; year <= LocalDate.now().getYear(); year++) {
            List<AgendaId> agendaIds = agendaDataService.getAgendaIds(year, SortOrder.ASC);
            List<Agenda> agendas = agendaIds.stream().map(agendaDataService::getAgenda).toList();
            logger.info("Reindexing {} agendas from {}", agendas.size(), year);
            agendaSearchDao.indexAgendas(agendas);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.AGENDA)) {
            logger.info("Handling agenda re-index event");
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.AGENDA)) {
            clearIndex();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleAgendaUpdateEvent(AgendaUpdateEvent agendaUpdateEvent) {
        if (agendaUpdateEvent != null && agendaUpdateEvent.agenda() != null) {
            updateIndex(agendaUpdateEvent.agenda());
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleBulkAgendaUpdateEvent(BulkAgendaUpdateEvent bulkAgendaUpdateEvent) {
        if (bulkAgendaUpdateEvent != null && !bulkAgendaUpdateEvent.agendas().isEmpty()) {
            updateIndex(bulkAgendaUpdateEvent.agendas());
        }
    }
}
