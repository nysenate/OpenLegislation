package gov.nysenate.openleg.spotchecks.base;

public class SpotCheckException extends RuntimeException {
    public SpotCheckException(String message) {
        super(message);
    }

    public SpotCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpotCheckException(Throwable cause) {
        this("Error occurred while running spotcheck report", cause);
    }
}
