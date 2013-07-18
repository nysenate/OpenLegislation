package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.ILuceneObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.lucene.document.Fieldable;

/**
 * Provides default methods and common operations for all indexed OpenLegislation models.
 *
 * @author GraylinKim
 *
 */
abstract public class BaseObject implements ILuceneObject, ISenateObject
{
    /**
     * The current active status of the object. Inactive objects will
     * generally be excluded from basic searches.
     */
    private boolean active = true;

    /**
     * The last modified time of the object in milliseconds since epoch
     */
    private long modified = 0;

    /**
     * A set of SOBI files that contained modifications to this object.
     */
    HashSet<String> sobiReferenceList = new HashSet<String>();

    /**
     * Default implementation of luceneFields which returns an empty
     * collection. Override to add custom document fields.
     */
    public Collection<Fieldable> luceneFields() {
        return new ArrayList<Fieldable>();
    }

    /**
     * Gets the active status of this object.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets the active status of this object
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the last modified time in milliseconds since epoch.
     */
    public long getModified() {
        return this.modified;
    }

    /**
     * Sets the last modified timestamp in milliseconds since epoch.
     */
    public void setModified(long modified) {
        this.modified = modified;
    }

    @Override
    @XmlTransient
    public int getYear() {
        return 0;
    }

    @Override
    public void setYear(int year) {

    }

    /**
     * Get the set of SOBI files that contain modifications for this object.
     */
    public HashSet<String> getSobiReferenceList() {
        return sobiReferenceList;
    }

    /**
     * Set the set of SOBI files that contain modifications for this object.
     */
    public void setSobiReferenceList(HashSet<String> sobiReferenceList) {
        this.sobiReferenceList = sobiReferenceList;
    }

    /**
     * Add a new SOBI filename to the SOBI reference list. This is preferred to
     * getting a reference to the list and directly adding filenames
     */
    public void addSobiReference(String reference) {
        sobiReferenceList.add(reference);
    }
}
