package gov.nysenate.openleg.model.search;

import org.elasticsearch.search.highlight.HighlightField;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SearchResult<ResultType>
{
    protected ResultType result;

    protected BigDecimal rank;

    protected Map<String, HighlightField> highlights;

    /** --- Constructor --- */

    public SearchResult(ResultType result, BigDecimal rank) {
        this(result, rank, new HashMap<>());
    }

    public SearchResult(ResultType result, BigDecimal rank, Map<String, HighlightField> highlights) {
        this.result = result;
        this.rank = rank;
        this.highlights = highlights;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "SearchResult{" + "rank=" + rank + ", result=" + result + ", highlights=" + highlights + '}';
    }

    /** --- Basic Getters --- */

    public ResultType getResult() {
        return result;
    }

    public BigDecimal getRank() {
        return rank;
    }

    public Map<String, HighlightField> getHighlights() {
        return highlights;
    }
}
