package gov.nysenate.openleg.service.base;

public class NoResultsSearchException extends SearchException
{
    private static final long serialVersionUID = -5154933846565304528L;

    private SearchParameters searchParams;

    public NoResultsSearchException(SearchParameters searchParams) {
        super("Could not retrieve results for search:" + "\n" + searchParams);
        this.searchParams = searchParams;
    }

    public SearchParameters getSearchParams() {
        return searchParams;
    }
}
