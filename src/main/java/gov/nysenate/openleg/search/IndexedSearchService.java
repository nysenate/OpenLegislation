package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import gov.nysenate.openleg.config.EnvironmentUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Provides access to search functionality, with some common implementations.
 */
public abstract class IndexedSearchService<T> {
    private static final Logger logger = LoggerFactory.getLogger(IndexedSearchService.class);

    private final SearchDao<?, ?, T> searchDao;
    @Autowired
    private EnvironmentUtils envUtils;
    @Autowired
    private OpenLegEnvironment env;

    protected IndexedSearchService(SearchDao<?, ?, T> searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * Ensures indices are filled with data, since even non-search operations uses the data.
     */
    @PostConstruct
    private void init() {
        if (searchDao.createIndex() && !envUtils.isTest()) {
            rebuildIndex();
        }
    }

    @PreDestroy
    private void destroy() {
        if (envUtils.isTest()) {
            searchDao.deleteIndex();
        }
    }

    protected static QueryVariant getYearQuery(String yearFieldName, Integer year) {
        if (year == null) {
            return null;
        }
        return TermQuery.of(b -> b.field(yearFieldName).value(year));
    }

    protected static QueryVariant getYearRangeQuery(String yearFieldName, Integer year) {
        if (year == null) {
            return null;
        }
        return RangeQuery.of(b -> b.field(yearFieldName)
                .from(LocalDate.of(year, 1, 1).toString())
                .to(LocalDate.of(year, 12, 31).toString()));
    }

    /**
     * Update the search index with the given content, replacing an existing entry if it exists.
     */
    public void updateIndex(T content) {
        if (env.isElasticIndexing()) {
            if (searchDao.indexType() != SearchIndex.API_LOG) {
                logger.info("Adding a document into {} index", searchDao.indexType());
            }
            searchDao.updateIndex(content);
        }
    }

    /**
     * Update the search index with the given collection of items, replacing each existing one with
     * the one in the collection if needed.
     */
    public void updateIndex(Collection<T> content) {
        if (content.size() == 1) {
            updateIndex(content.iterator().next());
        }
        if (env.isElasticIndexing() && !content.isEmpty()) {
            if (searchDao.indexType() != SearchIndex.API_LOG) {
                logger.info("Adding {} documents into {} index", content.size(), searchDao.indexType());
            }
            searchDao.updateIndex(content);
        }
    }

    public SearchIndex getIndex() {
        return searchDao.indexType();
    }

    /**
     * Clears all entries from the search index that is managed by the implementation.
     */
    public void clearIndex() {
        searchDao.deleteIndex();
        searchDao.createIndex();
    }

    /**
     * Clears and fully constructs the search index using data from the canonical backing store.
     */
    public abstract void rebuildIndex();
}
