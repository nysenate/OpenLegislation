package gov.nysenate.openleg.dao.base;

public class OldApiDocumentNotFoundEx extends RuntimeException {

    private static final long serialVersionUID = -5998519715553382223L;

    private String docType;
    private String oid;

    public OldApiDocumentNotFoundEx(String docType, String oid) {
        this(docType, oid, null);
    }

    public OldApiDocumentNotFoundEx(String docType, String oid, Throwable cause) {
        super("could not retrieve 1.9.2 api data for document: " + docType + " " + oid, cause);
        this.docType = docType;
        this.oid = oid;
    }

    public String getDocType() {
        return docType;
    }

    public String getOid() {
        return oid;
    }
}
