package gov.nysenate.openleg.api.search.view;

import gov.nysenate.openleg.api.ViewObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultView implements ViewObject {
    protected ViewObject result;
    protected BigDecimal rank;
    protected Map<String, List<String>> highlights = new HashMap<>();

    public SearchResultView(ViewObject result, BigDecimal rank) {
        this(result, rank, null);
    }

    public SearchResultView(ViewObject result, BigDecimal rank, Map<String, List<String>> highlightMap) {
        this.result = result;
        this.rank = rank;
        if (highlightMap != null) {
            this.highlights = highlightMap;
        }
    }

    @Override
    public String getViewType() {
        return "search-results";
    }

    public ViewObject getResult() {
        return result;
    }

    public BigDecimal getRank() {
        return rank;
    }

    public Map<String, List<String>> getHighlights() {
        return highlights;
    }
}