package gov.nysenate.openleg.search.transcripts.hearing;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ElasticHearingSearchDao extends ElasticBaseDao<Hearing> implements HearingSearchDao {
    private static final String hearingIndexName = SearchIndex.HEARING.getName();
    private static final Map<String, HighlightField> highlightFields;
    static {
        var highlightField = HighlightField.of(b -> b.numberOfFragments(0));
        highlightFields = Map.of("hosts", highlightField, "title", highlightField,
                "text", HighlightField.of(b -> b.numberOfFragments(2)));
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(Query query, Query postFilter,
                                                   List<SortOptions> sort, LimitOffset limOff) {
        return search(hearingIndexName, query, postFilter, highlightFields, null,
                sort, limOff, false, Hearing::getId);
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
                .map(hearing -> getIndexOperationRequest(hearingIndexName, String.valueOf(hearing.getId()), hearing))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(hearingIndexName).operations(bulkBuilder.build())));
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
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("startTime", basicTimeMapping, "endTime", basicTimeMapping);
    }
}
