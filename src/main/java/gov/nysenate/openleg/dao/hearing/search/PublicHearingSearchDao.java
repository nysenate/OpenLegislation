package gov.nysenate.openleg.dao.hearing.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching public hearings.
 */
public interface PublicHearingSearchDao
{

    /**
     * Performs a free-form search across all public hearings using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    public SearchResults<PublicHearingId> searchPublicHearings(QueryBuilder query, FilterBuilder filter, List<SortBuilder> sort, LimitOffset limOff);
    /**
     * Updates the public hearing search index with the supplied public hearing.
     * @param publicHearing
     */
    public void updatePublicHearingIndex(PublicHearing publicHearing);

    /**
     * Updates the public hearing search index with the supplied public hearings.
     * @param publicHearings
     */
    public void updatePublicHearingIndex(Collection<PublicHearing> publicHearings);

    /**
     * Removes the public hearing from the search index with the given id.
     * @param publicHearingId
     */
    public void deletePublicHearingFromIndex(PublicHearingId publicHearingId);

}
