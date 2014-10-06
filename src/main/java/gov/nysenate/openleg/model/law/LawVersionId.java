package gov.nysenate.openleg.model.law;

import java.time.LocalDate;

public class LawVersionId
{
    /** The three letter law identifier. */
    protected String lawId;

    /** The published date of this version of the law. */
    protected LocalDate publishedDate;

    /** --- Constructors --- */

    public LawVersionId(String lawId, LocalDate publishedDate) {
        this.lawId = lawId;
        this.publishedDate = publishedDate;
    }

    /** --- Basic Getters/Setters --- */

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
}

