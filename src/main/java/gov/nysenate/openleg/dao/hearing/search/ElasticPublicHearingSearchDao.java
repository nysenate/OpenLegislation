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
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ElasticPublicHearingSearchDao extends ElasticBaseDao implements PublicHearingSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticPublicHearingSearchDao.class);

    protected static final String publicHearingIndexName = SearchIndex.HEARING.getIndexName();

    protected static final List<HighlightBuilder.Field> highlightedFields =
            Arrays.asList(new HighlightBuilder.Field("text").numOfFragments(2),
                          new HighlightBuilder.Field("committees").numOfFragments(0),
                          new HighlightBuilder.Field("title").numOfFragments(0));

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(QueryBuilder query, QueryBuilder postFilter,
                                                               List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequest searchRequest = getSearchRequest(publicHearingIndexName, query, postFilter, highlightedFields, null, sort, limOff, false);
        SearchResponse response = new SearchResponse();
        try {
            response = searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.error("Search Public Hearings request failed.", ex);
        }
        logger.debug("Public Hearing search result with query {} and filter {} took {} ms", query, postFilter, response.getTook().getMillis());
        return getSearchResults(response, limOff, this::getPublicHearingIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearingIndex(PublicHearing publicHearing) {
        updatePublicHearingIndex(Collections.singletonList(publicHearing));
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearingIndex(Collection<PublicHearing> publicHearings) {
        if (!publicHearings.isEmpty()) {
            BulkRequest bulkRequest = new BulkRequest();
            List<PublicHearingView> publicHearingViews = publicHearings.stream().map(PublicHearingView::new).collect(Collectors.toList());

            for (PublicHearingView ph : publicHearingViews) {
                bulkRequest.add(new IndexRequest(
                        publicHearingIndexName, defaultType, ph.getFilename())
                        .source(OutputUtils.toJson(ph), XContentType.JSON));

            }
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deletePublicHearingFromIndex(PublicHearingId publicHearingId) {
        if (publicHearingId != null) {
            deleteEntry(publicHearingIndexName, publicHearingId.getFileName());
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
