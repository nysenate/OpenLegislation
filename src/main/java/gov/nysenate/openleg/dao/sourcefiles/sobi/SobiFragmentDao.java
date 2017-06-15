package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.ImmutableSet;

import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import org.springframework.dao.DataAccessException;

import java.util.List;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;

/**
 * The SobiFragmentDao interface exposes methods for retrieving and modifying SobiFragments.
 * This interface is only concerned with the storage mechanisms of the fragment data and does not perform any parsing
 * related to the content of the data.
 */
public interface SobiFragmentDao {
    /**
     * Retrieves an archived SobiFragment object with the given file name.
     *
     * @param fileName String - The file name of the Fragment file.
     *
     * @return SobiFragment file
     *
     * @throws DataAccessException - If there was an error while retrieving the SobiFragment file.
     */
    SobiFragment getSobiFragment(String fileName) throws DataAccessException;
    
    /**
     * Retrieve the SobiFragments associated with a given SobiFile.
     *
     * @param sobiFile SobiFile - The SobiFile instance to get fragments for.
     * @param sortById SortOrder - Sort order for the fragment id.
     *
     * @return List<SobiFragment>
     */
    List<SobiFragment> getSobiFragments(SourceFile sobiFile, SortOrder sortById);
    
    /**
     * Retrieve the SobiFragments of a specific type associated with a given SobiFile.
     *
     * @param sobiFile     SobiFile - The SobiFile instance to get fragments for.
     * @param fragmentType SobiFragmentType - Get fragments of this type.
     * @param sortById     SortOrder - Sort order for the fragment id.
     *
     * @return List<SobiFragment>
     */
    List<SobiFragment> getSobiFragments(SobiFile sobiFile, SobiFragmentType fragmentType, SortOrder sortById);
    
    /**
     * Retrieves all SobiFragments that are awaiting processing.
     *
     * @param sortById SortOrder - Sort order for the fragment id.
     * @param limOff   LimitOffset - Restrict the results list.
     *
     * @return List<SobiFragment>
     */
    List<SobiFragment> getPendingSobiFragments(SortOrder sortById, LimitOffset limOff);
    
    /**
     * Retrieves the SobiFragments that are awaiting processing and belong to one of the types
     * in the given 'restrict' set.
     *
     * @param restrict ImmutableSet<SobiFragmentType> - Filter result set to only include these types.
     * @param sortById SortOrder - Sort order for the fragment id.
     * @param limOff   LimitOffset - Restrict the results list.
     *
     * @return List<SobiFragment>
     */
    List<SobiFragment> getPendingSobiFragments(ImmutableSet<SobiFragmentType> restrict, SortOrder sortById,
                                               LimitOffset limOff);
    
    /**
     * Persist the sobi fragment into the backing store. The parent SobiFile must be recorded in
     * the backing store prior to invoking this method.
     *
     * @param fragment SobiFragment
     */
    void updateSobiFragment(SobiFragment fragment);
}
