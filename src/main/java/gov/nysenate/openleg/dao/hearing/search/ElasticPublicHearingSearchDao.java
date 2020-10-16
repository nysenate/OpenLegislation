package gov.nysenate.openleg.dao.hearing.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.hearing.PublicHearingView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

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
                                                               List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(publicHearingIndexName, query, postFilter, highlightedFields, null,
                sort, limOff, false, this::getPublicHearingIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearingIndex(PublicHearing publicHearing) {
        updatePublicHearingIndex(Collections.singletonList(publicHearing));
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearingIndex(Collection<PublicHearing> publicHearings) {
        BulkRequest bulkRequest = new BulkRequest();
        publicHearings.stream()
                .map(PublicHearingView::new)
                .map(phv -> getJsonIndexRequest(publicHearingIndexName, phv.getFilename(), phv))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
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

    @Override
    protected HashMap<String, Object> getCustomMappingProperties() throws IOException {
        HashMap<String, Object> props = super.getCustomMappingProperties();
        props.put("startTime", basicTimeMapping);
        props.put("endTime", basicTimeMapping);
        return props;
    }

    private PublicHearingId getPublicHearingIdFromHit(SearchHit hit) {
        return new PublicHearingId(hit.getId());
    }
}
