package gov.nysenate.openleg.legislation.transcripts.session;

public class InvalidLinkTypeEx extends RuntimeException {
    private final String linkTypeStr;

    public InvalidLinkTypeEx(String linkTypeStr) {
        super(linkTypeStr + " is not a valid link type.");
        this.linkTypeStr = linkTypeStr;
    }

    public String getLinkTypeStr() {
        return linkTypeStr;
    }
}
