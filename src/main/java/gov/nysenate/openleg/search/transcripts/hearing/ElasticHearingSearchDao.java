package gov.nysenate.openleg.search.transcripts.hearing;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ElasticHearingSearchDao extends ElasticBaseDao<HearingId, HearingView, Hearing> {
    /** {@inheritDoc} */
    @Override
    public SearchIndex indexType() {
        return SearchIndex.HEARING;
    }

    @Override
    protected String getId(Hearing data) {
        return data.getId().toString();
    }

    @Override
    protected HearingView getDoc(Hearing data) {
        return new HearingView(data);
    }

    @Override
    protected Map<String, HighlightField> highlightedFields() {
        var highlightField = HighlightField.of(b -> b.numberOfFragments(0));
        return Map.of("hosts", highlightField, "title", highlightField,
                "text", HighlightField.of(b -> b.numberOfFragments(2)));
    }

    @Override
    protected HearingId toId(String idStr) {
        return new HearingId(Integer.parseInt(idStr));
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("startTime", basicTimeMapping, "endTime", basicTimeMapping);
    }
}
