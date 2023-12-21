package gov.nysenate.openleg.search;

import java.io.Serial;

public class InvalidSearchParamException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2245867337608015453L;

    public InvalidSearchParamException() {}

    public InvalidSearchParamException(String message) {
        super(message);
    }
}
