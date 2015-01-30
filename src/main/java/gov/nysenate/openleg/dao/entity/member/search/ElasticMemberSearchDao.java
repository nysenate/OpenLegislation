package gov.nysenate.openleg.dao.entity.member.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ElasticMemberSearchDao extends ElasticBaseDao implements MemberSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchDao.class);

    protected static final String memberIndexName = SearchIndex.MEMBER.getIndexName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<Member> searchMembers(QueryBuilder query, FilterBuilder filter, String sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder = getSearchRequest(memberIndexName, query, filter, sort, limOff);
        SearchResponse response = searchBuilder.execute().actionGet();
        logger.debug("Member search result with query {} and filter {} took {} ms", query, filter, response.getTookInMillis());
        return getSearchResults(response, limOff, this::getMemberFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateMemberIndex(Member member) {
        updateMemberIndex(Arrays.asList(member));
    }

    /** {@inheritDoc} */
    @Override
    public void updateMemberIndex(Collection<Member> members) {
        if (!members.isEmpty()) {
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            List<MemberView> memberViewList = members.stream().map(MemberView::new).collect(Collectors.toList());
            memberViewList.forEach(m ->
                            bulkRequest.add(searchClient.prepareIndex(memberIndexName,
                                    String.valueOf(m.getSessionYear()),
                                    String.valueOf(m.getMemberId()))
                                    .setSource(OutputUtils.toJson(m)))
            );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteMemberFromIndex(Member member) {
        if (member != null) {
            deleteEntry(memberIndexName, String.valueOf(member.getId()), String.valueOf(member.getSessionYear()));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(memberIndexName);
    }

    private Member getMemberFromHit(SearchHit hit) {
        return new Member(Integer.valueOf(hit.getId()), SessionYear.of(Integer.valueOf(hit.getType())));
    }
}
