package gov.nysenate.openleg.dao.log.search;

import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

public interface ApiLogSearchDao
{
    /**
     * Performs a free-form search on the log data. Returns the response ids.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<Integer>
     */
    SearchResults<Integer> searchLogs(QueryBuilder query, FilterBuilder filter, List<SortBuilder> sort, LimitOffset limOff);

    /**
     * Similar to #searchLogs, but instead of fetching just the request ids, this will
     * return the full stored ApiLogItemViews.
     *
     * @return SearchResults<ApiLogItemView>
     */
    SearchResults<ApiLogItemView> searchLogsAndFetchData(QueryBuilder query, FilterBuilder filter, List<SortBuilder> sort, LimitOffset limOff);

    /**
     * Update the log index with the content of the supplied ApiResponse.
     *
     * @param apiResponse ApiResponse
     */
    void updateLogIndex(ApiResponse apiResponse);

    /**
     * Updates the log index with the content of the supplied api responses.
     *
     * @param apiResponses Collection<ApiResponse>
     */
    void updateLogIndex(Collection<ApiResponse> apiResponses);

    /**
     * Removes the bill from the search index with the given id.
     *
     * @param requestId Integer
     */
    void deleteLogFromIndex(Integer requestId);
}
