package gov.nysenate.openleg.model.base;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * Basic info that is common to all pieces of legislative content.
 */
abstract public class BaseLegislativeContent
{
    /** The date the object was most recently modified. */
    protected LocalDateTime modifiedDateTime;

    /** The date the object was most recently published. */
    protected LocalDateTime publishedDateTime;

    /** The session this object was created in. */
    protected int session;

    /** The calendar year this object was active in. */
    protected int year;

    /** --- Constructors --- */

    public BaseLegislativeContent() {}

    public BaseLegislativeContent(BaseLegislativeContent other) {
        this.modifiedDateTime = other.modifiedDateTime;
        this.publishedDateTime = other.publishedDateTime;
        this.session = other.session;
        this.year = other.year;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BaseLegislativeContent other = (BaseLegislativeContent) obj;
        return Objects.equals(this.modifiedDateTime, other.modifiedDateTime) &&
               Objects.equals(this.publishedDateTime, other.publishedDateTime) &&
               Objects.equals(this.session, other.session) &&
               Objects.equals(this.year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiedDateTime, publishedDateTime, session, year);
    }

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getPublishedDateTime() {
        return this.publishedDateTime;
    }

    public void setPublishedDateTime(LocalDateTime publishedDateTime) {
        this.publishedDateTime = publishedDateTime;
    }

    public boolean isPublished() {
        return this.getPublishedDateTime() != null;
    }

    public LocalDateTime getModifiedDateTime() {
        return this.modifiedDateTime;
    }

    public void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public int getSession() {
        return this.session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
