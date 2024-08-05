package gov.nysenate.openleg.search.transcripts.hearing;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ElasticHearingSearchDao extends ElasticBaseDao<HearingView> implements HearingSearchDao {
    private static final Map<String, HighlightField> highlightFields;
    static {
        var highlightField = HighlightField.of(b -> b.numberOfFragments(0));
        highlightFields = Map.of("hosts", highlightField, "title", highlightField,
                "text", HighlightField.of(b -> b.numberOfFragments(2)));
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(Query query,
                                                   List<SortOptions> sort, LimitOffset limOff) {
        return search(query, highlightFields,
                sort, limOff, false, hv -> new HearingId(hv.getId()));
    }

    /** {@inheritDoc} */
    @Override
    public void updateHearingIndex(Hearing hearing) {
        updateHearingIndex(List.of(hearing));
    }

    /** {@inheritDoc} */
    @Override
    public void updateHearingIndex(Collection<Hearing> hearings) {
        var bulkBuilder = new BulkOperation.Builder();
        hearings.stream()
                .map(hearing -> getIndexOperation(String.valueOf(hearing.getId()), new HearingView(hearing)))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(bulkBuilder);
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.HEARING;
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("startTime", basicTimeMapping, "endTime", basicTimeMapping);
    }
}
