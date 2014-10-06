package gov.nysenate.openleg.processor.law;

import java.time.LocalDate;

/**
 * This class is to be used internally by the law parsers to aid in building up LawDocument instances.
 */
public class LawBlock
{
    /** Typically the first line of the law document which contains all the meta data. */
    private String header = "";

    /** A method which indicates the action to take with the given document. */
    private String method = "";

    /** The unique document id, e.g. ABC1000. */
    private String documentId = "";

    /** The 3 letter law id, e.g ABC, EDN, etc. */
    private String lawId = "";

    /** The published date of this block. */
    private LocalDate publishedDate;

    /** The document location, which is just the document id without the law id. */
    private String locationId = "";

    /** Indicates if the law is consolidated or not. */
    private boolean consolidated = false;

    /** String builder used to construct the text body of the block. */
    private StringBuilder text = new StringBuilder();

    /** --- Basic Getters/Setters --- */

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public StringBuilder getText() {
        return text;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

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

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public boolean isConsolidated() {
        return consolidated;
    }

    public void setConsolidated(boolean consolidated) {
        this.consolidated = consolidated;
    }
}