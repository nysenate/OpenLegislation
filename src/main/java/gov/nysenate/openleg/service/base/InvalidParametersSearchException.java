package gov.nysenate.openleg.service.base;

/** An exception thrown from a search service when a given SearchParameters object is found to be invalid */
public class InvalidParametersSearchException extends RuntimeException {

    private static final long serialVersionUID = 2497556335879753721L;

    private SearchParameters searchParams;

    public InvalidParametersSearchException(SearchParameters searchParams) {
        super("Attempted to execute a search with invalid search parameters:" + "\n" + searchParams);
        this.searchParams = searchParams;
    }

    public SearchParameters getSearchParams() {
        return searchParams;
    }
}
