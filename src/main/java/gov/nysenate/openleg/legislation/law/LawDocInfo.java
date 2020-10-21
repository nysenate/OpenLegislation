package gov.nysenate.openleg.legislation.law;

import gov.nysenate.openleg.processors.law.LawBlock;

import java.time.LocalDate;

public class LawDocInfo extends LawDocId
{
    /** The title of the document which is derived by parsing the body text. */
    protected String title;

    /** The document type which is parsed from the location id. */
    protected LawDocumentType docType;

    /** The last portion of the location id as it appears in the text.
     * For example, if locationId = 'A2T1ST2-B' then the docType will be
     * 'SUBTITLE' and this docTypeId will be '2-B'. */
    protected String docTypeId;

    /** True if this doc was created as a dummy.  i.e it was created by the parser but not specified in the sources.*/
    protected boolean dummy = false;

    /** --- Constructors --- */

    public LawDocInfo() {}

    public LawDocInfo(String documentId, String lawId, String locationId, String title, LawDocumentType docType,
                      String docTypeId, LocalDate publishedDate, boolean dummy) {
        super(documentId, publishedDate, locationId, lawId);
        this.title = title;
        this.docType = docType;
        this.docTypeId = docTypeId;
        this.dummy = dummy;
    }

    public LawDocInfo(LawDocInfo other) {
        super(other);
        this.title = other.title;
        this.docType = other.docType;
        this.docTypeId = other.docTypeId;
        this.dummy = other.dummy;
    }

    public LawDocInfo(LawBlock lawBlock) {
        this.setDocumentId(lawBlock.getDocumentId());
        this.setLawId(lawBlock.getLawId());
        this.setLocationId(lawBlock.getLocationId());
        this.setPublishedDate(lawBlock.getPublishedDate());
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return documentId + " (" + docType + ") " + publishedDate;
    }

    /** --- Basic Getters/Setters --- */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LawDocumentType getDocType() {
        return docType;
    }

    public void setDocType(LawDocumentType docType) {
        this.docType = docType;
    }

    public String getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(String docTypeId) {
        this.docTypeId = docTypeId;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
}