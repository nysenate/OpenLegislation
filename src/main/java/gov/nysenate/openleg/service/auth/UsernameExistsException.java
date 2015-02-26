package gov.nysenate.openleg.service.auth;

public class UsernameExistsException extends RuntimeException
{
    private String email;

    public UsernameExistsException(String email) {
        super("The given email address, " +email+ " is already taken!");
        this.email = email;
    }

    public String getEmail() { return this.email; }
}
