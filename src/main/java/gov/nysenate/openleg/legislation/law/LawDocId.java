package gov.nysenate.openleg.legislation.law;

import java.time.LocalDate;
import java.util.Objects;

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

    /* --- Constructors --- */

    public LawDocId() {}

    public LawDocId(LawDocId other) {
        this.documentId = other.documentId;
        this.publishedDate = other.publishedDate;
        this.locationId = other.locationId;
        this.lawId = other.lawId;
    }

    public LawDocId(String documentId, LocalDate publishedDate, String locationId, String lawId) {
        this.documentId = documentId;
        this.publishedDate = publishedDate;
        this.locationId = locationId;
        this.lawId = lawId;
    }

    public LawDocId(String documentId, LocalDate publishedDate) {
        this.documentId = documentId;
        this.locationId = (documentId != null) ? documentId.substring(3) : null;
        this.lawId = (documentId != null) ? documentId.substring(0, 3) : null;
        this.publishedDate = publishedDate;
    }


    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LawDocId lawDocId)) return false;
        return Objects.equals(documentId, lawDocId.documentId) &&
                Objects.equals(publishedDate, lawDocId.publishedDate) &&
                Objects.equals(locationId, lawDocId.locationId) &&
                Objects.equals(lawId, lawDocId.lawId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, publishedDate, locationId, lawId);
    }

    /* --- Basic Getters/Setters --- */

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
