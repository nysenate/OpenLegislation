package gov.nysenate.openleg.search.transcripts.hearing;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching hearings.
 */
public interface HearingSearchDao {
    /**
     * Performs a free-form search across all hearings using the query string syntax and a filter.
     * @param query  String - Query Builder
     * @param sort   String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    SearchResults<HearingId> searchHearings(Query query,
                                            List<SortOptions> sort, LimitOffset limOff);
    /**
     * Updates the hearing search index with the supplied hearing.
     */
    void updateHearingIndex(Hearing hearing);

    /**
     * Updates the hearing search index with the supplied hearings.
     */
    void updateHearingIndex(Collection<Hearing> hearings);
}
