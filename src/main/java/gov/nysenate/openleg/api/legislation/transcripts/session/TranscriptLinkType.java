package gov.nysenate.openleg.api.legislation.transcripts.session;

public enum TranscriptLinkType {
    NONE, OPEN_LEGISLATION, PUBLIC_WEBSITE;

    public static TranscriptLinkType fromString(String string) {
        if (string.isEmpty()) {
            return NONE;
        }
        return valueOf(string.toUpperCase());
    }
}
