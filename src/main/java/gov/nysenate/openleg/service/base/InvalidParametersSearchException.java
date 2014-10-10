package gov.nysenate.openleg.service.base;

import java.util.Set;

/** An exception thrown from a search service when a given SearchParameters object is found to be invalid */
public class InvalidParametersSearchException extends SearchException
{
    private static final long serialVersionUID = 2497556335879753721L;

    private SearchParameters searchParams;
    private Set<String> invalidParams;

    public InvalidParametersSearchException(SearchParameters searchParams) {
        super("Attempted to execute a search with invalid search parameters:" + "\n" + searchParams);
        this.searchParams = searchParams;
    }

    public InvalidParametersSearchException(SearchParameters searchParams, Set<String> invalidParams) {

        super("Attempted to execute a search with invalid search parameters:" + "\n" + invalidParams);
        this.searchParams = searchParams;
        this.invalidParams = invalidParams;
    }

    public SearchParameters getSearchParams() {
        return searchParams;
    }

    public Set<String> getInvalidParams() {
        return invalidParams;
    }
}
