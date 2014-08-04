package gov.nysenate.openleg.model.base;

import java.util.Date;
import java.util.Objects;

/**
 * Basic info that is common to all pieces of legislative content.
 */
abstract public class BaseLegislativeContent
{
    /** The date the object was most recently modified. */
    protected Date modifiedDate = null;

    /** The date the object was most recently published. */
    protected Date publishDate = null;

    /** The session this object was created in. */
    protected int session;

    /** The calendar year this object was active in. */
    protected int year;

    /** --- Constructors --- */

    public BaseLegislativeContent() {}

    public BaseLegislativeContent(BaseLegislativeContent other) {
        this.modifiedDate = other.modifiedDate;
        this.publishDate = other.publishDate;
        this.session = other.session;
        this.year = other.year;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BaseLegislativeContent other = (BaseLegislativeContent) obj;
        return Objects.equals(this.modifiedDate, other.modifiedDate) &&
               Objects.equals(this.publishDate, other.publishDate) &&
               Objects.equals(this.session, other.session) &&
               Objects.equals(this.year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiedDate, publishDate, session, year);
    }

    /** --- Basic Getters/Setters --- */

    public Date getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public boolean isPublished() {
        return this.getPublishDate() != null;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
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
