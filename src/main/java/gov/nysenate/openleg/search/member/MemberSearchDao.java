package gov.nysenate.openleg.search.member;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

public interface MemberSearchDao {
    /**
     * Performs a free-form search across all members using the query string syntax and a filter.
     * @param query  String - Query Builder
     * @param sort   String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<Integer> - memberIds from matched members
     */
    SearchResults<Integer> searchMembers(Query query, List<SortOptions> sort, LimitOffset limOff);

    /**
     * Update the Member search index with the supplied Member.
     */
    void updateMemberIndex(FullMember member);

    /**
     * Updates the Member search index with the supplied Members.
     */
    void updateMemberIndex(Collection<FullMember> members);

}
