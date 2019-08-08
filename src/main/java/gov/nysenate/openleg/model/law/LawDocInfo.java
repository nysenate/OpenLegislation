package gov.nysenate.openleg.model.law;

import gov.nysenate.openleg.processor.law.LawBlock;

import java.time.LocalDate;

public class LawDocInfo extends LawDocId
{
    /** The title of the document which is derived by parsing the body text. */
    protected String title;

    /** The document type which is parsed from the location id. */
    protected LawDocumentType docType;

    /** The last portion of the location id as it appears in the text.
     * For example, if locationId = 'A2T1ST2-B' then the docType will be
     * 'SUBTITLE' and this docTypeId may be '2-B', 'II-B, or TWO-B. */
    protected String docTypeId;

    /** The docTypeId, always as a number if possible. */
    private String labelId = null;

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

    public String getLabelId() {
        return labelId == null ? docTypeId : labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }
}