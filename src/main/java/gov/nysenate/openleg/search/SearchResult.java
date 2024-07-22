package gov.nysenate.openleg.search;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SearchResult<ResultType>(ResultType result, BigDecimal rank, Map<String, List<String>> highlights) {
    @Override
    public String toString() {
        return "SearchResult{" + "rank=" + rank + ", result=" + result + ", highlights=" + highlights + '}';
    }
}
