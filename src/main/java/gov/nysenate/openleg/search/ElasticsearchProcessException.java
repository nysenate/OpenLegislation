package gov.nysenate.openleg.search;

public class ElasticsearchProcessException extends RuntimeException {
    public ElasticsearchProcessException(String message) {
        super(message);
    }

    public ElasticsearchProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
