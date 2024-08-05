package gov.nysenate.openleg.search.member;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class ElasticMemberSearchDao extends ElasticBaseDao<FullMemberView> implements MemberSearchDao {
    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(Query query, List<SortOptions> sort, LimitOffset limOff) {
        return search(query, sort, limOff, FullMemberView::getMemberId);
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
        members.stream().map(FullMemberView::new)
                .map(fmv -> getIndexOperation(String.valueOf(fmv.getMemberId()), fmv))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(bulkBuilder);
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.MEMBER;
    }
}
