package gov.nysenate.openleg.model.law;

import java.time.LocalDate;

public class LawDocId
{
    /** A unique document identifier specified by LBDC.
     *  For example 'EDNA1' indicates article 1 of education law while 'EDN100' indicates section 100. */
    protected String documentId;

    /** The date on which this portion of law was published via LBDC. */
    protected LocalDate publishedDate;

    /** The portion of the document id after the three letter law id. */
    protected String locationId;

    /** The 3 letter law id, e.g ABC, EDN, etc. */
    protected String lawId;

    /** --- Constructors --- */

    public LawDocId() {}

    public LawDocId(String documentId, LocalDate publishedDate) {
        this.documentId = documentId;
        this.locationId = (documentId != null) ? documentId.substring(3) : null;
        this.lawId = (documentId != null) ? documentId.substring(0, 3) : null;
        this.publishedDate = publishedDate;
    }

    /** --- Basic Getters/Setters --- */

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
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
}
