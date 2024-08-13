package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;

public abstract class IndexedSearchService<T> {
    private static final Logger logger = LoggerFactory.getLogger(IndexedSearchService.class);
    private final SearchDao<?, ?, T> searchDao;
    private final OpenLegEnvironment env;

    protected IndexedSearchService(SearchDao<?, ?, T> searchDao, OpenLegEnvironment env) {
        this.searchDao = searchDao;
        this.env = env;
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
            logger.info("Adding 1 document into {} index", searchDao.getIndex());
            searchDao.updateIndex(content);
        }
    }

    /**
     * Update the search index with the given collection of items, replacing each existing one with
     * the one in the collection.
     */
    public void updateIndex(Collection<T> content) {
        if (content.size() == 1) {
            updateIndex(content.iterator().next());
        }
        if (env.isElasticIndexing() && !content.isEmpty()) {
            logger.info("Adding {} documents into {} index", content.size(), searchDao.getIndex());
            searchDao.updateIndex(content);
        }
    }

    public SearchIndex getIndex() {
        return searchDao.getIndex();
    }

    /**
     * Clears all entries from the search index that is managed by the implementation.
     */
    public void clearIndex() {
        searchDao.purgeIndices();
        searchDao.createIndices();
    }

    /**
     * Clears and fully constructs the search index using data from the canonical backing store.
     */
    public abstract void rebuildIndex();
}
