package gov.nysenate.openleg.dao.entity.member.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Collection;

public interface MemberSearchDao
{
    /**
     * Performs a free-form search across all members using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<Member>
     */
    public SearchResults<Member> searchMembers(QueryBuilder query, FilterBuilder filter, String sort, LimitOffset limOff);

    /**
     * Update the Member search index with the supplied Member.
     * @param member
     */
    public void updateMemberIndex(Member member);

    /**
     * Updates the Member search index with the supplied Members.
     * @param members
     */
    public void updateMemberIndex(Collection<Member> members);

    /**
     * Removes the Member from the search index with the given id.
     * @param member
     */
    public void deleteMemberFromIndex(Member member);
}
