package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.HashSet;

/**
 * Implements the BaseLegContent interface of all published OpenLegislation content.
 * @author GraylinKim
 */
abstract public class BaseLegContent
{
    /** The date the object was most recently modified. */
    protected Date modifiedDate = null;

    /** The date the object was most recently published. */
    protected Date publishDate = null;

    /** The session this object was created in. */
    protected int session;

    /** The calendar year this object was active in. */
    protected int year;

    /** A set of SOBI files that contained modifications to this object. */
    protected HashSet<String> dataSources;

    /** Initializes BaseLegContent parameters. */
    public BaseLegContent()
    {
        this.dataSources = new HashSet<String>();
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Add a new source to the set of data sources. This is preferred to
     * getting a reference to the list and directly adding filenames
     *
     * @param source - The new source to add to the data sources.
     */
    public void addDataSource(String source) {
        dataSources.add(source);
    }

    /** --- Basic Getters/Setters --- */

    public boolean isBrandNew() {
        return this.brandNew;
    }

    public void setBrandNew(boolean brandNew) {
        this.brandNew = brandNew;
    }

    public Date getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public boolean isPublished() {
        return this.getPublishDate() != null;
    }

    public Date getModifiedDate()
    {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    public int getSession()
    {
        return this.session;
    }

    public void setSession(int session)
    {
        this.session = session;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public HashSet<String> getDataSources()
    {
        return dataSources;
    }

    public void setDataSources(HashSet<String> dataSources)
    {
        this.dataSources = dataSources;
    }
}
