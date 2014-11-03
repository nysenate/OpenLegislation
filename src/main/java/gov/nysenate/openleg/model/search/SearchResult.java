package gov.nysenate.openleg.model.search;

import java.math.BigDecimal;

public class SearchResult<ResultType>
{
    protected ResultType result;

    protected BigDecimal rank;

    /** --- Constructor --- */

    public SearchResult(ResultType result, BigDecimal rank) {
        this.result = result;
        this.rank = rank;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "{" + result + ", rank=" + rank + '}';
    }

    /** --- Basic Getters --- */

    public ResultType getResult() {
        return result;
    }

    public BigDecimal getRank() {
        return rank;
    }
}
