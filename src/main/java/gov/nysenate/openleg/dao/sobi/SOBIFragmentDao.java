package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.model.sobi.SOBIFragmentType;

import java.util.List;

/**
 * DAO interface for managing and persisting SOBIFragments.
 */
public interface SOBIFragmentDao
{
    /** --- Retrieval Methods --- */

    public SOBIFragment getSOBIFragment(SOBIFragmentType fragmentType, String fragmentFileName);

    public List<SOBIFragment> getSOBIFragments(SOBIFile sobiFile, SortOrder order);

    public List<SOBIFragment> getSOBIFragments(SOBIFile sobiFile, SOBIFragmentType fragmentType, SortOrder order);

    /** --- Processing/Insertion Methods --- */

    /**
     * Persist the sobi fragments into the backing store. It is assumed that the SOBIFile objects have already
     * been staged otherwise the fragment cannot be recorded.
     * @param fragments List<SOBIFragment>
     */
    public void saveSOBIFragments(List<SOBIFragment> fragments);

    /**
     * Delete the fragments that were generated for the given sobiFile.
     * @param sobiFile SOBIFile
     */
    public void deleteSOBIFragments(SOBIFile sobiFile);
}
