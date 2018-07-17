package gov.nysenate.openleg.dao.entity.member.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
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

    private static final String memberIndexName = SearchIndex.MEMBER.getIndexName();
    private static final String fullMemberType = "full_member";

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder = getSearchRequest(memberIndexName, query, filter, sort, limOff);
        SearchResponse response = searchBuilder.execute().actionGet();
        logger.debug("Member search result with query {} and filter {} took {} ms", query, filter, response.getTook().getMillis());
        return getSearchResults(response, limOff, this::getMemberFromHit);
    }

    /** {@inheritDoc}
     * @param member*/
    @Override
    public void updateMemberIndex(FullMember member) {
        updateMemberIndex(Arrays.asList(member));
    }

    /** {@inheritDoc}
     * @param members*/
    @Override
    public void updateMemberIndex(Collection<FullMember> members) {
        if (!members.isEmpty()) {
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            List<FullMemberView> memberViewList = members.stream()
                    .map(FullMemberView::new)
                    .collect(Collectors.toList());
            memberViewList.forEach(m ->
                            bulkRequest.add(searchClient.prepareIndex(memberIndexName,
                                    String.valueOf(fullMemberType),
                                    String.valueOf(m.getMemberId()))
                                    .setSource(OutputUtils.toJson(m), XContentType.JSON))
            );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(memberIndexName);
    }

    private Integer getMemberFromHit(SearchHit hit) {
        return Integer.valueOf(hit.getId());
    }
}
