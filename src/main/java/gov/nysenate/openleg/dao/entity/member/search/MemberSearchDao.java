package gov.nysenate.openleg.dao.entity.member.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

public interface MemberSearchDao
{
    /**
     * Performs a free-form search across all members using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<Integer> - memberIds from matched members
     */
    SearchResults<Integer> searchMembers(QueryBuilder query, QueryBuilder filter, List<SortBuilder<?>> sort, LimitOffset limOff);

    /**
     * Update the Member search index with the supplied Member.
     * @param member
     */
    void updateMemberIndex(FullMember member);

    /**
     * Updates the Member search index with the supplied Members.
     * @param members
     */
    void updateMemberIndex(Collection<FullMember> members);

}
