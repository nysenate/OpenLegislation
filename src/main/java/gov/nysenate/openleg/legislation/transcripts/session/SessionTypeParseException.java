package gov.nysenate.openleg.legislation.transcripts.session;

public class SessionTypeParseException extends IllegalArgumentException {
    public SessionTypeParseException(String inputSessionType) {
        super("Could not parse session type from " + inputSessionType);
    }
}
