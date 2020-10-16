package gov.nysenate.openleg.client.view.search;

import gov.nysenate.openleg.client.view.base.ViewObject;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SearchResultView implements ViewObject
{
    protected ViewObject result;
    protected BigDecimal rank;
    protected Map<String, List<String>> highlights = new HashMap<>();

    public SearchResultView(ViewObject result, BigDecimal rank) {
        this(result, rank, null);
    }

    public SearchResultView(ViewObject result, BigDecimal rank, Map<String, HighlightField> highlightMap) {
        this.result = result;
        this.rank = rank;
        if (highlightMap != null) {
            highlightMap.forEach((k,v) ->
               highlights.put(k,
                   Arrays.stream(v.getFragments())
                       .map(Text::toString)
                       .collect(toList()))
            );
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