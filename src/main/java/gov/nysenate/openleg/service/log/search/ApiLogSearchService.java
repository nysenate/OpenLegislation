package gov.nysenate.openleg.service.log.search;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;

import java.time.LocalDateTime;

public interface ApiLogSearchService extends IndexedSearchService<ApiResponse>
{
    /**
     * Search the api log search index.
     *
     * @param query String - search query
     * @param sort String - sort query
     * @param limOff LimitOffset - limit offset
     * @return SearchResults<ApiLogItemView>
     */
    SearchResults<ApiLogItemView> searchApiLogs(String query, String sort, LimitOffset limOff) throws SearchException;
}