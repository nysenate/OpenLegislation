package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.HashSet;

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
