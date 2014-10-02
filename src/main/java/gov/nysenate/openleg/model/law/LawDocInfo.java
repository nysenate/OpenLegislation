package gov.nysenate.openleg.model.law;

import gov.nysenate.openleg.processor.law.LawBlock;

import java.time.LocalDate;

public class LawDocInfo
{
    /** A unique document identifier specified by LBDC.
     *  For example 'EDNA1' indicates article 1 of education law while 'EDN100' indicates section 100. */
    protected String documentId;

    /** The 3 letter law id, e.g ABC, EDN, etc. */
    protected String lawId;

    /** The portion of the document id after the three letter law id. */
    protected String locationId;

    /** The title of the document which is derived by parsing the body text. */
    protected String title;

    /** The document type which is parsed from the location id. */
    protected LawDocumentType docType;

    /** The last portion of the location id. For example, if locationId = 'A2T1ST2-B' then the
     *  docType will be 'SUBTITLE' and this docTypeId will be '2-B'. */
    protected String docTypeId;

    /** The date on which this portion of law was published via LBDC. */
    protected LocalDate publishedDate;

    /** --- Constructors --- */

    public LawDocInfo() {}

    public LawDocInfo(String documentId, String lawId, String locationId, String title, LawDocumentType docType,
                      String docTypeId, LocalDate publishedDate) {
        this.documentId = documentId;
        this.lawId = lawId;
        this.locationId = locationId;
        this.title = title;
        this.docType = docType;
        this.docTypeId = docTypeId;
        this.publishedDate = publishedDate;
    }

    public LawDocInfo(LawBlock lawBlock) {
        this.setDocumentId(lawBlock.getDocumentId());
        this.setLawId(lawBlock.getLawId());
        this.setLocationId(lawBlock.getLocationId());
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return documentId + " (" + docType + ") " + publishedDate;
    }

    /** --- Basic Getters/Setters --- */

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getLawId() {
        return lawId;
    }

    public void setLawId(String lawId) {
        this.lawId = lawId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

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

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }
}