package gov.nysenate.openleg.search.transcripts.hearing;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching public hearings.
 */
public interface HearingSearchDao {
    /**
     * Performs a free-form search across all public hearings using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    SearchResults<HearingId> searchHearings(QueryBuilder query, QueryBuilder filter, List<SortBuilder<?>> sort, LimitOffset limOff);
    /**
     * Updates the public hearing search index with the supplied public hearing.
     * @param hearing
     */
    void updateHearingIndex(Hearing hearing);

    /**
     * Updates the public hearing search index with the supplied public hearings.
     * @param hearings
     */
    void updateHearingIndex(Collection<Hearing> hearings);

    /**
     * Removes the public hearing from the search index with the given id.
     * @param hearingId
     */
    void deleteHearingFromIndex(HearingId hearingId);
}
