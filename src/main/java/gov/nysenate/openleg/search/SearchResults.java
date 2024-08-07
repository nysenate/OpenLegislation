package gov.nysenate.openleg.search;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;

import java.util.List;

/**
 * Represents the basic structure of a search response with additional information such
 * as the total number of results matched as well as a LimitOffset reference to keep track
 * of pagination.
 *
 * @param <ResultType> The type of the result (should typically be an identifier such as BillId, AgendaId, etc).
 * @param totalResults The total number of results available.
 * @param resultList   A list of the selected results.
 * @param limitOffset  The limit offset value used to generate the results listing.
 */
public record SearchResults<ResultType>(int totalResults,
                                        List<SearchResult<ResultType>> resultList,
                                        LimitOffset limitOffset) {
    private static final SearchResults<Object> EMPTY = new SearchResults<>(0, ImmutableList.of(), LimitOffset.ALL);

    /**
     * Returns a {@link SearchResults} object with no results.
     * @return {@link SearchResults}
     */
    // Can safely cast to generic type since EMPTY will never contain any elements.
    @SuppressWarnings("unchecked")
    public static <T> SearchResults<T> empty() {
        return (SearchResults<T>) EMPTY;
    }

    /**
     * --- Overrides ---
     */

    @Override
    public String toString() {
        return "SearchResults{" + "resultCount=" + totalResults + ", results=" + resultList + ", limitOffset=" + limitOffset + '}';
    }

    /**
     * --- Functional Getters/Setters ---
     */

    public PaginatedList<ResultType> toPaginatedList() {
        return new PaginatedList<>(totalResults, limitOffset,
                resultList.stream().map(SearchResult::result).toList());
    }

    public List<ResultType> getRawResults() {
        return resultList.stream().map(SearchResult::result).toList();
    }
}