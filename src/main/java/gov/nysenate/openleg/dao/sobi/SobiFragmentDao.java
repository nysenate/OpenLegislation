package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;

import java.util.List;

/**
 * DAO interface for managing and persisting SOBIFragments.
 */
public interface SobiFragmentDao
{
    /** --- Retrieval Methods --- */

    public SobiFragment getSOBIFragment(SobiFragmentType fragmentType, String fragmentFileName);

    public List<SobiFragment> getSOBIFragments(SobiFile sobiFile, SortOrder order);

    public List<SobiFragment> getSOBIFragments(SobiFile sobiFile, SobiFragmentType fragmentType, SortOrder order);

    /** --- Processing/Insertion Methods --- */

    /**
     * Persist the sobi fragments into the backing store. It is assumed that the SobiFile objects have already
     * been staged otherwise the fragment cannot be recorded.
     * @param fragments List<SobiFragment>
     */
    public void saveSOBIFragments(List<SobiFragment> fragments);

    /**
     * Delete the fragments that were generated for the given sobiFile.
     * @param sobiFile SobiFile
     */
    public void deleteSOBIFragments(SobiFile sobiFile);
}
