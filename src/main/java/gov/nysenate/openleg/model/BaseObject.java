package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.HashSet;

/**
 * Implements the BaseObject interface of all published OpenLegislation content.
 *
 * Does NOT implement getOid(). All base classes are responsible for forming their
 * own unique object IDs.
 *
 * @author GraylinKim
 */
abstract public class BaseObject implements IBaseObject
{
    /**
     * True if the object was not retrieved from storage.
     */
    protected boolean brandNew = true;

    /**
     * The current active status of the object. Inactive objects will
     * generally be excluded from basic searches.
     */
    protected boolean active = true;

    /**
     * The date the object was most recently modified
     */
    protected Date modifiedDate = null;

    /**
     * The date the object was most recently published
     */
    protected Date publishDate = null;

    /**
     * The session this object was created in.
     */
    protected int session;

    /**
     * The calendar year this object was active in.
     */
    protected int year;

    /**
     * A set of SOBI files that contained modifications to this object.
     */
    protected HashSet<String> dataSources;

    /**
     * Initializes BaseObject parameters.
     */
    public BaseObject()
    {
        this.dataSources = new HashSet<String>();
    }

    /**
     * @return - True if the object was not retrieved from storage.
     */
    public boolean isBrandNew() {
        return this.brandNew;
    }

    /**
     * @param brandNew - The new brandNew state.
     */
    public void setBrandNew(boolean brandNew) {
        this.brandNew = brandNew;
    }

    /**
     * @return The current active status.
     */
    public boolean isActive()
    {
        return this.active;
    }

    /**
     * @param active - The new active status
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * @return - The last publish date.
     */
    public Date getPublishDate()
    {
        return this.publishDate;
    }

    /**
     * Set the last publish date.
     *
     * @param publishDate - The new publish date.
     */
    public void setPublishDate(Date publishDate)
    {
        this.publishDate = publishDate;
    }

    /**
     * @return - true if the object has been published
     */
    public boolean isPublished()
    {
        return this.publishDate != null;
    }

    /**
     * Gets the last modified time in milliseconds since epoch.
     */
    public Date getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * Sets the last modified timestamp in milliseconds since epoch.
     */
    public void setModifiedDate(Date modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /**
     * @return - The session this object was created in.
     */
    public int getSession()
    {
        return this.session;
    }

    /**
     * @param session - The new session this object was created in.
     */
    public void setSession(int session)
    {
        this.session = session;
    }

    /**
     * @return - The calendar year this object was active in.
     */
    public int getYear() {
        return this.year;
    }

    /**
     * @param year - The new calendar year for this object.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Get the set of SOBI files that contain modifications for this object.
     */
    public HashSet<String> getDataSources()
    {
        return dataSources;
    }

    /**
     * @param dataSources - The new set of data sources that contain modifications for this object.
     */
    public void setDataSources(HashSet<String> dataSources)
    {
        this.dataSources = dataSources;
    }

    /**
     * Add a new source to the set of data sources. This is preferred to
     * getting a reference to the list and directly adding filenames
     *
     * @param source - The new source to add to the data sources.
     */
    public void addDataSource(String source)
    {
        dataSources.add(source);
    }
}
