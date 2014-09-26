package gov.nysenate.openleg.model.law;

public class LawDocument
{
    /** A unique document identifier specified by LBDC.
     *  For example 'EDNA1' indicates article 1 of education law while 'EDN100' indicates section 100. */
    protected String documentId;

    /** Indicates the level in the hierarchy this document resides, extracted from the documentId. */
    protected LawDocLevel level;

    /** Indicates the id of the document relative to the level, e.g. '200' if this is section 200.
     *  This field is extracted by parsing the the documentId. */
    protected String levelId;

    /** Reference to the parent of this document. Will be null for the root document. */
    protected LawDocument parentDoc;

    /** The actual law text. */
    protected String text;

    /** --- Constructors --- */

    public LawDocument() {}

    /** --- Basic Getters/Setters --- */

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public LawDocLevel getLevel() {
        return level;
    }

    public void setLevel(LawDocLevel level) {
        this.level = level;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public LawDocument getParentDoc() {
        return parentDoc;
    }

    public void setParentDoc(LawDocument parentDoc) {
        this.parentDoc = parentDoc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}