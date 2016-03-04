package gov.nysenate.openleg.service.agenda.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.agenda.search.ElasticAgendaSearchDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.search.*;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.agenda.event.AgendaUpdateEvent;
import gov.nysenate.openleg.service.agenda.event.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ElasticAgendaSearchService implements AgendaSearchService, IndexedSearchService<Agenda>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticAgendaSearchService.class);

    @Autowired private Environment env;
    @Autowired private EventBus eventBus;
    @Autowired private ElasticAgendaSearchDao agendaSearchDao;
    @Autowired private AgendaDataService agendaDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, String sort, LimitOffset limOff) throws SearchException {
        return searchCommitteeAgendas(QueryBuilders.queryString(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(int year, String sort, LimitOffset limOff) throws SearchException {
        return searchCommitteeAgendas(
                QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("agenda.id.year", year)),
                null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, int year, String sort, LimitOffset limOff) throws SearchException {
        return searchCommitteeAgendas(
                QueryBuilders.filteredQuery(QueryBuilders.queryString(query), FilterBuilders.termFilter("agenda.id.year", year)),
                null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Agenda agenda) {
        if (env.isElasticIndexing()) {
            logger.info("Indexing agenda {} into elastic search.", agenda.getId());
            agendaSearchDao.updateAgendaIndex(agenda);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Agenda> agendas) {
        if (env.isElasticIndexing()) {
            logger.info("Indexing {} agendas into elastic search.", agendas.size());
            agendaSearchDao.updateAgendaIndex(agendas);
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
            List<Agenda> agendas = agendaIds.stream().map(aid -> agendaDataService.getAgenda(aid)).collect(toList());
            logger.info("Reindexing {} agendas from {}", agendas.size(), year);
            agendaSearchDao.updateAgendaIndex(agendas);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.AGENDA)) {
            logger.info("Handling agenda re-index event");
            try {
                rebuildIndex();
            }
            catch (Exception ex) {
                logger.error("Unexpected exception during handling of Agenda RebuildIndexEvent!", ex);
            }
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
        if (agendaUpdateEvent != null && agendaUpdateEvent.getAgenda() != null) {
            updateIndex(agendaUpdateEvent.getAgenda());
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleBulkAgendaUpdateEvent(BulkAgendaUpdateEvent bulkAgendaUpdateEvent) {
        if (bulkAgendaUpdateEvent != null && !bulkAgendaUpdateEvent.getAgendas().isEmpty()) {
            updateIndex(bulkAgendaUpdateEvent.getAgendas());
        }
    }

    /** --- Internal Methods --- */

    private SearchResults<CommitteeAgendaId> searchCommitteeAgendas(QueryBuilder query, FilterBuilder postFilter,
                                                      String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        try {
            return agendaSearchDao.searchCommitteeAgendas(query, postFilter,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
        } catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        } catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex);
        }
    }
}
