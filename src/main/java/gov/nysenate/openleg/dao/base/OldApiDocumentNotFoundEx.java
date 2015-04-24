package gov.nysenate.openleg.dao.base;

public class OldApiDocumentNotFoundEx extends RuntimeException {

    private static final long serialVersionUID = -5998519715553382223L;

    private String url;
    private String docType;
    private String oid;

    public OldApiDocumentNotFoundEx(String url) {
        this(url, null);
    }

    public OldApiDocumentNotFoundEx(String url, Throwable cause) {
        super("could not retrieve 1.9.2 api data for url: " + url, cause);
    }

    public String getUrl() {
        return url;
    }
}
