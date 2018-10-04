package gov.nysenate.openleg.dao.entity.member.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class ElasticMemberSearchDao extends ElasticBaseDao implements MemberSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchDao.class);

    private static final String memberIndexName = SearchIndex.MEMBER.getIndexName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        return search(memberIndexName, query, filter, sort, limOff, this::getMemberIdFromHit);
    }

    /** {@inheritDoc}
     * @param member*/
    @Override
    public void updateMemberIndex(FullMember member) {
        updateMemberIndex(Collections.singletonList(member));
    }

    /** {@inheritDoc}
     * @param members*/
    @Override
    public void updateMemberIndex(Collection<FullMember> members) {
        BulkRequest bulkRequest = new BulkRequest();
        members.stream()
                .map(FullMemberView::new)
                .map(mv -> getJsonIndexRequest(memberIndexName, String.valueOf(mv.getMemberId()), mv))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(memberIndexName);
    }

    @Override
    protected HashMap<String, Object> getCustomMappingProperties() throws IOException {
        HashMap<String, Object> props = super.getCustomMappingProperties();
        props.put("chamber", searchableKeywordMapping);
        props.put("email", searchableKeywordMapping);
        props.put("shortName", searchableKeywordMapping);
        props.put("fullName", searchableKeywordMapping);
        props.put("prefix", searchableKeywordMapping);
        props.put("firstName", searchableKeywordMapping);
        props.put("middleName", searchableKeywordMapping);
        props.put("lastName", searchableKeywordMapping);
        props.put("suffix", searchableKeywordMapping);
        props.put("imgName", searchableKeywordMapping);
        return props;
    }

    private Integer getMemberIdFromHit(SearchHit hit) {
        return Integer.valueOf(hit.getId());
    }
}
