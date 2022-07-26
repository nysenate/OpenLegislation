package gov.nysenate.openleg.search;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.math.BigDecimal;
import java.util.Map;

public record SearchResult<ResultType>(ResultType result, BigDecimal rank, Map<String, HighlightField> highlights) {
    @Override
    public String toString() {
        return "SearchResult{" + "rank=" + rank + ", result=" + result + ", highlights=" + highlights + '}';
    }
}
