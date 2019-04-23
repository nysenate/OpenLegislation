package gov.nysenate.openleg.service.law.search;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.law.data.LawDataDao;
import gov.nysenate.openleg.dao.law.search.ElasticLawSearchDao;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawInfo;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.search.*;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.event.BulkLawUpdateEvent;
import gov.nysenate.openleg.service.law.event.LawTreeUpdateEvent;
import gov.nysenate.openleg.service.law.event.LawUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticLawSearchService implements LawSearchService, IndexedSearchService<LawDocument>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticLawSearchService.class);

    @Autowired private EventBus eventBus;
    @Autowired private Environment env;
    @Autowired private ElasticLawSearchDao lawSearchDao;
    @Autowired private LawDataDao lawDataDao;
    @Autowired private LawDataService lawDataService;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /* --- LawSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(String query, String sort, LimitOffset limOff) throws SearchException {
        return searchLawDocs(query, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(String query, String lawId, String sort, LimitOffset limOff) throws SearchException {
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
        if (lawId != null) {
            queryBuilder = QueryBuilders.boolQuery()
                    .must(queryBuilder)
                    .filter(QueryBuilders.termQuery("lawId", lawId.toLowerCase()));
        }
        try {
            return lawSearchDao.searchLawDocs(queryBuilder, null, null,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
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

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleLawTreeUpdate(LawTreeUpdateEvent lawTreeUpdateEvent) {
        String lawChapterId = lawTreeUpdateEvent.getLawChapterId();
        clearLawChapter(lawChapterId);
        indexLawChapter(lawChapterId);
    }

    /* --- IndexedSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public void updateIndex(LawDocument content) {
        updateIndex(Collections.singletonList(content));
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<LawDocument> content) {
        if (env.isElasticIndexing()) {
            List<LawDocument> indexableDocs = content.stream()
                    .filter(this::isLawDocIndexable)
                    .collect(Collectors.toList());
            lawSearchDao.updateLawIndex(indexableDocs);
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
        lawDataDao.getLawInfos().stream()
                .map(LawInfo::getLawId)
                .sorted()
                .forEach(this::indexLawChapter);
        logger.info("Completed law search re-index");
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

    /* --- Internal Methods --- */

    /**
     * Deletes all documents for the given law chapter from the law index.
     * @param lawId String - law chapter id
     */
    private void clearLawChapter(String lawId) {
        logger.info("Clearing law chapter {} from index", lawId);
        QueryBuilder query = QueryBuilders.termQuery("lawId", StringUtils.lowerCase(lawId));
        SearchResults<LawDocId> chapterDocs =
                lawSearchDao.searchLawDocs(query, null, null, ImmutableList.of(), LimitOffset.ALL);
        lawSearchDao.deleteLawDocsFromIndex(chapterDocs.getRawResults());
    }

    /**
     * Indexes all published documents in a law chapter.
     *
     * Does not clear the chapter, so previously indexed documents may still be present.
     * @param lawId String - law chapter id
     */
    private void indexLawChapter(String lawId) {
        logger.info("Indexing law chapter {}", lawId);
        Collection<LawDocument> lawDocs = lawDataService.getLawDocuments(lawId, LocalDate.now()).values();
        updateIndex(lawDocs);
    }

    /**
     * Determines if a law document can be indexed.
     *
     * The document must be present in the current law tree.
     */
    private boolean isLawDocIndexable(LawDocument doc) {
        LawTree lawTree = lawDataService.getLawTree(doc.getLawId());
        return lawTree.find(doc.getDocumentId()).isPresent();
    }
}