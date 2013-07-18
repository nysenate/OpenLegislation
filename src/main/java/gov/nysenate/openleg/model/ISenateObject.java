package gov.nysenate.openleg.model;

import java.util.HashSet;

/**
 * All senate objects are modified via SOBI files received from LBDC and should track back
 * references to these files for debugging purposes.
 *
 * @author GraylinKim
 *
 */
public interface ISenateObject
{
    public int getYear();
    public void setYear(int year);

    /**
     * Get the set of SOBI files that contain modifications for this object.
     */
    public HashSet<String> getSobiReferenceList();

    /**
     * Set the set of SOBI files that contain modifications for this object.
     */
    public void setSobiReferenceList(HashSet<String> sobiReferenceList);

    /**
     * Add a new SOBI filename to the SOBI reference list. This is preferred to
     * getting a reference to the list and directly adding filenames
     */
    public void addSobiReference(String reference);
}
