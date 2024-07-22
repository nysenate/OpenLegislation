package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import gov.nysenate.openleg.legislation.SessionYear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface IndexedSearchService<T> {
    // TODO: make use of something similar to SearchParseException
    static Query getYearTermQuery(String fieldName, int year) {
        return TermQuery.of(b -> b.field(fieldName).value(year))._toQuery();
    }

    static Query getYearTermQuery(String fieldName, SessionYear sessionYear) {
        return getYearTermQuery(fieldName, sessionYear.year());
    }

    static Query getStringQuery(String query) {
        return QueryStringQuery.of(b -> b.query(query))._toQuery();
    }

    static List<Query> getBasicQueries(String yearFieldName, Integer year, String query) {
        var queries = new ArrayList<Query>();
        if (year != null) {
            queries.add(getYearTermQuery(yearFieldName, year));
        }
        if (query != null) {
            queries.add(getStringQuery(query));
        }
        return queries;
    }

    static Query getBasicBoolQuery(String yearFieldName, Integer year, String query) {
        return BoolQuery.of(b -> b.must(getBasicQueries(yearFieldName, year, query)))._toQuery();
    }

    static Query getBasicBoolQuery(String yearFieldName, SessionYear sessionYear, String query) {
        return BoolQuery.of(b -> b.must(
                getBasicQueries(yearFieldName, sessionYear == null ? null : sessionYear.year(), query)
        ))._toQuery();
    }

    /**
     * Update the search index with the given content, replacing an existing entry if it exists.
     */
    void updateIndex(T content);

    /**
     * Update the search index with the given collection of items, replacing each existing one with
     * the one in the collection.
     */
    void updateIndex(Collection<T> content);

    /**
     * Clears all entries from the search index(ices) that are managed by the implementation.
     */
    void clearIndex();

    /**
     * Clears and fully constructs the search index(ices) using data from the canonical backing store.
     */
    void rebuildIndex();

    /**
     * Handle a rebuild search index event by checking to see if event affects any of the indices managed
     * by the implementation and recreating them in full from the backing store.
     */
    void handleRebuildEvent(RebuildIndexEvent event);

    /**
     * Handle a clear search index event by checking to see if event affects any of the indices managed
     * by the implementation and clearing them from the backing store.
     */
    void handleClearEvent(ClearIndexEvent event);
}