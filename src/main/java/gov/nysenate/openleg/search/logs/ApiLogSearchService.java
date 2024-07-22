package gov.nysenate.openleg.search.logs;

import gov.nysenate.openleg.api.logs.ApiLogItemView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.IndexedSearchService;

public interface ApiLogSearchService extends IndexedSearchService<ApiResponse> {
    /**
     * Search the api log search index.
     * @param query String - search query
     * @param sort String - sort query
     * @param limOff LimitOffset - limit offset
     * @return SearchResults<ApiLogItemView>
     */
    SearchResults<ApiLogItemView> searchApiLogs(String query, String sort, LimitOffset limOff) throws SearchException;
}
