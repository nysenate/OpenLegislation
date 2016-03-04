package gov.nysenate.openleg.service.law.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.law.data.LawDataDao;
import gov.nysenate.openleg.dao.law.search.ElasticLawSearchDao;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.search.*;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.law.event.BulkLawUpdateEvent;
import gov.nysenate.openleg.service.law.event.LawUpdateEvent;
import org.elasticsearch.ElasticsearchException;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ElasticLawSearchService implements LawSearchService, IndexedSearchService<LawDocument>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticLawSearchService.class);

    @Autowired private EventBus eventBus;
    @Autowired private Environment env;
    @Autowired private ElasticLawSearchDao lawSearchDao;
    @Autowired private LawDataDao lawDataDao;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** --- LawSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(String query, String sort, LimitOffset limOff) throws SearchException {
        return searchLawDocs(query, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(String query, String lawId, String sort, LimitOffset limOff) throws SearchException {
        QueryBuilder queryBuilder = QueryBuilders.queryString(query);
        if (lawId != null) {
            queryBuilder = QueryBuilders.filteredQuery(queryBuilder, FilterBuilders.typeFilter(lawId));
        }
        try {
            return lawSearchDao.searchLawDocs(queryBuilder, null, null,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex);
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleLawUpdate(LawUpdateEvent lawUpdateEvent) {
        if (lawUpdateEvent != null && lawUpdateEvent.getLawDoc() != null) {
            updateIndex(lawUpdateEvent.getLawDoc());
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleBulkLawUpdate(BulkLawUpdateEvent bulkLawUpdateEvent) {
        if (bulkLawUpdateEvent != null && !bulkLawUpdateEvent.getLawDocuments().isEmpty()) {
            updateIndex(bulkLawUpdateEvent.getLawDocuments());
        }
    }

    /** --- IndexedSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public void updateIndex(LawDocument content) {
        updateIndex(Arrays.asList(content));
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<LawDocument> content) {
        if (env.isElasticIndexing()) {
            lawSearchDao.updateLawIndex(content);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        lawSearchDao.purgeIndices();
        lawSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        logger.info("Handling law search re-indexing");
        clearIndex();
        lawDataDao.getLawInfos().stream().forEach(lawInfo ->
            updateIndex(lawDataDao.getLawDocuments(lawInfo.getLawId(), LocalDate.now()).entrySet().stream()
                .map(doc -> doc.getValue()).collect(Collectors.toList())));
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.LAW)) {
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.LAW)) {
            clearIndex();
        }
    }
}