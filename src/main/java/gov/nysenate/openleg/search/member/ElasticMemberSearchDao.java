package gov.nysenate.openleg.search.member;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class ElasticMemberSearchDao extends ElasticBaseDao<FullMember> implements MemberSearchDao {
    private static final String memberIndexName = SearchIndex.MEMBER.getName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(Query query, Query postFilter, List<SortOptions> sort, LimitOffset limOff) {
        return search(memberIndexName, query, postFilter, sort, limOff, FullMember::getMemberId);
    }

    /** {@inheritDoc}
     * @param member*/
    @Override
    public void updateMemberIndex(FullMember member) {
        updateMemberIndex(List.of(member));
    }

    /** {@inheritDoc}
     * @param members*/
    @Override
    public void updateMemberIndex(Collection<FullMember> members) {
        var bulkBuilder = new BulkOperation.Builder();
        members.stream()
                .map(fmv -> getIndexOperationRequest(memberIndexName, String.valueOf(fmv.getMemberId()), fmv))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(memberIndexName).operations(bulkBuilder.build())));
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.MEMBER;
    }
}
