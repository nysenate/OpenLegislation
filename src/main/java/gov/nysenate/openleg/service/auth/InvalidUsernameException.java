package gov.nysenate.openleg.service.auth;

public class InvalidUsernameException extends RuntimeException {

    private static final long serialVersionUID = 6976993561679491834L;

    private String username;
    private String properFormat;

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
