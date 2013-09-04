package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.HashSet;

/**
 * Base object model of all published OpenLegislation content.
 *
 * @author GraylinKim
 */
public interface IBaseObject
{
    /**
     * @return - True if the object has never been saved to storage.
     */
    public boolean isBrandNew();

    /**
     * @param brandNew - The new brand new state.
     */
    public void setBrandNew(boolean brandNew);

    /**
     * @return - The current active status.
     */
    public boolean isActive();

    /**
     * @param active - The new active status
     */
    public void setActive(boolean active);

    /**
     * @return - The last publish date.
     */
    public Date getPublishDate();

    /**
     * Set the last publish date.
     *
     * @param publishDate - The new publish date.
     */
    public void setPublishDate(Date publishDate);

    /**
     * @return - true if the object has been published
     */
    public boolean isPublished();

    /**
     * Gets the last modified time in milliseconds since epoch.
     */
    public Date getModifiedDate();

    /**
     * Sets the last modified time stamp in milliseconds since epoch.
     */
    public void setModifiedDate(Date modifiedDate);

    /**
     * @return - The session this object was created in.
     */
    public int getSession();

    /**
     * @param session - The new session this object was created in.
     */
    public void setSession(int session);

    /**
     * @return - The calendar year this object was active in.
     */
    public int getYear();

    /**
     * @param year - The calendar year this object was active in.
     */
    public void setYear(int year);

    /**
     * Get the set of SOBI files that contain modifications for this object.
     */
    public HashSet<String> getDataSources();

    /**
     * @param dataSources - The new set of data sources that contain modifications for this object.
     */
    public void setDataSources(HashSet<String> dataSources);

    /**
     * Add a new source to the set of data sources. This is preferred to
     * getting a reference to the list and directly adding filenames
     *
     * @param source - The new source to add to the data sources.
     */
    public void addDataSource(String source);

    /**
     * Get the unique id for this object for Lucene/web indexing and purging.
     */
    public String getOid();

    /**
     * Get the document type for this object.
     */
    public String getOtype();
}
