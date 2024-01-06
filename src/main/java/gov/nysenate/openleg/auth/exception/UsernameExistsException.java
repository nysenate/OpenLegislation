package gov.nysenate.openleg.auth.exception;

import java.io.Serial;

public class UsernameExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5598502201519880435L;

    private final String email;

    public UsernameExistsException(String email) {
        super("The given email address, " + email + ", has already requested a key.");
        this.email = email;
    }

    public String getEmail() { return this.email; }
}
