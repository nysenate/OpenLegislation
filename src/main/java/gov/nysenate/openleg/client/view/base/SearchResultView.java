package gov.nysenate.openleg.client.view.base;

import java.math.BigDecimal;

public class SearchResultView implements ViewObject
{
    protected ViewObject result;
    protected BigDecimal rank;

    public SearchResultView(ViewObject result, BigDecimal rank) {
        this.result = result;
        this.rank = rank;
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
}