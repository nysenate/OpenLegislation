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
 * DAO interface for searching hearings.
 */
public interface HearingSearchDao {
    /**
     * Performs a free-form search across all hearings using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    SearchResults<HearingId> searchHearings(QueryBuilder query, QueryBuilder filter, List<SortBuilder<?>> sort, LimitOffset limOff);
    /**
     * Updates the hearing search index with the supplied hearing.
     * @param hearing
     */
    void updateHearingIndex(Hearing hearing);

    /**
     * Updates the hearing search index with the supplied hearings.
     * @param hearings
     */
    void updateHearingIndex(Collection<Hearing> hearings);

    /**
     * Removes the hearing from the search index with the given id.
     * @param hearingId
     */
    void deleteHearingFromIndex(HearingId hearingId);
}
