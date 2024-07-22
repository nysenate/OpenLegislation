package gov.nysenate.openleg.search;

public class GenericElasticsearchException extends RuntimeException {
    public GenericElasticsearchException(String message) {
        super(message);
    }

    public GenericElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
