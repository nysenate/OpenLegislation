package gov.nysenate.openleg.service.base;

import gov.nysenate.openleg.dao.base.LimitOffset;

import java.util.List;

/**
 * Represents the basic structure of a search response with additional information such
 * as the total number of results matched as well as a LimitOffset reference to keep track
 * of pagination.
 *
 * @param <ResultType> The type of the result (should typically be an identifier such as BillId, AgendaId, etc).
 */
public class SearchResults<ResultType>
{
    /** The total number of results available. */
    private int resultCount;

    /** A list of the selected results. */
    private List<SearchResult<ResultType>> results;

    /** The limit offset value used to generate the results listing. */
    private LimitOffset limitOffset;

    /** --- Constructors --- */

    public SearchResults(int resultCount, List<SearchResult<ResultType>> results, LimitOffset limitOffset) {
        this.resultCount = resultCount;
        this.results = results;
        this.limitOffset = limitOffset;
    }

    /** --- Methods --- */

    public boolean hasResults() {
        return (resultCount > 0);
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "SearchResults{" + "resultCount=" + resultCount + ", results=" + results + ", limitOffset=" + limitOffset + '}';
    }

    /** --- Basic Getters/Setters --- */

    public int getResultCount() {
        return resultCount;
    }

    public List<SearchResult<ResultType>> getResults() {
        return results;
    }

    public LimitOffset getLimitOffset() {
        return limitOffset;
    }
}