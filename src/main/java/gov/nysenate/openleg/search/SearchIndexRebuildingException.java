package gov.nysenate.openleg.search;

/**
 * Thrown when a search hits an index that is being cleared and refilled, since it would only
 * return whatever part of the data has been indexed so far.
 */
public class SearchIndexRebuildingException extends RuntimeException {
    public SearchIndexRebuildingException(SearchIndex index) {
        super("The " + index.getName() + " search index is being rebuilt. Try again later.");
    }
}
