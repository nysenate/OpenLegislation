package gov.nysenate.openleg.service.base;

public class SearchResult<ResultType>
{
    protected ResultType result;

    protected int rank;

    public SearchResult(ResultType result, int rank) {
        this.result = result;
        this.rank = rank;
    }

    public ResultType getResult() {
        return result;
    }

    public int getRank() {
        return rank;
    }
}
