package gov.nysenate.openleg.service.auth;

public class UsernameExistsException extends RuntimeException
{
    private static final long serialVersionUID = -5598502201519880435L;

    private String email;

    public UsernameExistsException(String email) {
        super("The given email address, " + email + ", has already requested a key.");
        this.email = email;
    }

    public String getEmail() { return this.email; }
}
