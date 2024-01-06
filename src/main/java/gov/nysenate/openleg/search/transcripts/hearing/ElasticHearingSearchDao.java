package gov.nysenate.openleg.search.transcripts.hearing;

import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class ElasticHearingSearchDao extends ElasticBaseDao implements HearingSearchDao {
    private static final String hearingIndexName = SearchIndex.HEARING.getName();
    private static final List<HighlightBuilder.Field> highlightedFields =
            List.of(new HighlightBuilder.Field("text").numOfFragments(2),
                          new HighlightBuilder.Field("hosts").numOfFragments(0),
                          new HighlightBuilder.Field("title").numOfFragments(0));

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(QueryBuilder query, QueryBuilder postFilter,
                                                   List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(hearingIndexName, query, postFilter, highlightedFields, null,
                sort, limOff, false, this::getHearingIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateHearingIndex(Hearing hearing) {
        updateHearingIndex(Collections.singletonList(hearing));
    }

    /** {@inheritDoc} */
    @Override
    public void updateHearingIndex(Collection<Hearing> hearings) {
        BulkRequest bulkRequest = new BulkRequest();
        hearings.stream()
                .map(HearingView::new)
                .map(hearingView -> getJsonIndexRequest(hearingIndexName, String.valueOf(hearingView.getId()), hearingView))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteHearingFromIndex(HearingId hearingId) {
        if (hearingId != null) {
            deleteEntry(hearingIndexName, String.valueOf(hearingId.id()));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.HEARING;
    }

    @Override
    protected HashMap<String, Object> getCustomMappingProperties() throws IOException {
        HashMap<String, Object> props = super.getCustomMappingProperties();
        props.put("startTime", basicTimeMapping);
        props.put("endTime", basicTimeMapping);
        return props;
    }

    private HearingId getHearingIdFromHit(SearchHit hit) {
        return new HearingId(Integer.parseInt(hit.getId()));
    }
}
