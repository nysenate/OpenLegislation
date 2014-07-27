package gov.nysenate.openleg.model.base;

import java.util.Date;

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

    /**
     * A session year refers to that start of a 2 year legislative session period.
     * This method ensures that any given year will resolve to the correct session start year.
     * @param year int
     * @return int
     */
    public static int resolveSessionYear(int year) {
        return (year % 2 == 0) ? year - 1 : year;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseLegislativeContent)) return false;
        BaseLegislativeContent that = (BaseLegislativeContent) o;
        if (session != that.session) return false;
        if (year != that.year) return false;
        if (modifiedDate != null ? !modifiedDate.equals(that.modifiedDate) : that.modifiedDate != null) return false;
        if (publishDate != null ? !publishDate.equals(that.publishDate) : that.publishDate != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = modifiedDate != null ? modifiedDate.hashCode() : 0;
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + session;
        result = 31 * result + year;
        return result;
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
