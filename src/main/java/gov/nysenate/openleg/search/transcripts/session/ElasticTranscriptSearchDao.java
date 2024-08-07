package gov.nysenate.openleg.search.transcripts.session;

import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ElasticTranscriptSearchDao extends ElasticBaseDao<TranscriptId, TranscriptView, Transcript> {
    private static final Property searchableKeywordMapping =
            KeywordProperty.of(b -> b.fields("text",
                    TextProperty.of(textB -> textB)._toProperty()
            ))._toProperty();

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.TRANSCRIPT;
    }

    @Override
    protected TranscriptId getId(Transcript data) {
        return data.getId();
    }

    @Override
    protected TranscriptView getDoc(Transcript data) {
        return new TranscriptView(data);
    }

    @Override
    protected Map<String, HighlightField> highlightedFields() {
        return Map.of("text", HighlightField.of(b -> b.numberOfFragments(3)));
    }

    @Override
    protected TranscriptId toId(String idStr) {
        String[] parts = idStr.replaceAll("[()]", "").split(", ");
        return TranscriptId.from(LocalDateTime.parse(parts[0]), parts[1]);
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("filename", searchableKeywordMapping,
                "location", searchableKeywordMapping,
                "sessionType", searchableKeywordMapping);
    }
}
