package gov.nysenate.openleg.model.law;

import java.time.LocalDate;
import java.util.Objects;

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

    /** --- Overrides --- */

    @Override
    public int hashCode() {
        return Objects.hash(lawId, publishedDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final LawVersionId other = (LawVersionId) obj;
        return Objects.equals(this.lawId, other.lawId) && Objects.equals(this.publishedDate, other.publishedDate);
    }

    @Override
    public String toString() {
        return lawId + "v" + publishedDate;
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

