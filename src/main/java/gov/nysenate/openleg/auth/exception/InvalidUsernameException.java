package gov.nysenate.openleg.auth.exception;

import java.io.Serial;

public class InvalidUsernameException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6976993561679491834L;

    private final String username, properFormat;

    public InvalidUsernameException(String username, String properFormat) {
        super("The supplied username \"" + username + "\" is not valid.");
        this.username = username;
        this.properFormat = properFormat;
    }

    public String getUsername() {
        return username;
    }

    public String getProperFormat() {
        return properFormat;
    }
}
