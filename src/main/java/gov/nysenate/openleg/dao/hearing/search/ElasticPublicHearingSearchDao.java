package gov.nysenate.openleg.dao.hearing.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.hearing.PublicHearingView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
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
public class ElasticPublicHearingSearchDao extends ElasticBaseDao implements PublicHearingSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticPublicHearingSearchDao.class);

    protected static final String publicHearingIndexName = SearchIndex.HEARING.getIndexName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(QueryBuilder query, FilterBuilder filter, String sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder = getSearchRequest(publicHearingIndexName, query, filter, sort, limOff);
        SearchResponse response = searchBuilder.execute().actionGet();
        logger.debug("Public Hearing search result with query {} took {} ms", query, response.getTookInMillis());
        return getSearchResults(response, limOff, this::getPublicHearingIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearingIndex(PublicHearing publicHearing) {
        updatePublicHearingIndex(Arrays.asList(publicHearing));
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearingIndex(Collection<PublicHearing> publicHearings) {
        if (!publicHearings.isEmpty()) {
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            List<PublicHearingView> publicHearingViews = publicHearings.stream().map(PublicHearingView::new).collect(Collectors.toList());
            publicHearingViews.forEach(ph ->
                    bulkRequest.add(searchClient.prepareIndex(publicHearingIndexName, "hearings", ph.getFilename())
                            .setSource(OutputUtils.toJson(ph)))
            );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deletePublicHearingFromIndex(PublicHearingId publicHearingId) {
        if (publicHearingId != null) {
            deleteEntry(publicHearingIndexName, "hearings", publicHearingId.getFileName());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(publicHearingIndexName);
    }

    private PublicHearingId getPublicHearingIdFromHit(SearchHit hit) {
        return new PublicHearingId(hit.getId());
    }
}
