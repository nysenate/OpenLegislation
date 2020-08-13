package gov.nysenate.openleg.dao.bill.reference.senatesite;

import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;

import java.io.IOException;
import java.util.Collection;

public interface SenateSiteDao {

    /**
     * Get all nysenate.gov dump fragments that have not yet been processed into a report
     * @return Collection<SenateSiteDump>
     */
    Collection<SenateSiteDump> getPendingDumps(SpotCheckRefType refType) throws IOException;

    /**
     * Persists the given nysenate.gov dump fragment
     * @param fragment
     * @param fragmentData
     */
    void saveDumpFragment(SenateSiteDumpFragment fragment, String fragmentData) throws IOException;

    /**
     * Archives a senate site dump such that it will not be picked up by {@link #getPendingDumps(SpotCheckRefType)}.
     *
     * On success: All fragments belonging to {@code dump} will be gzipped and moved into the archive directory.
     * The original fragments (from the incoming directory) will be deleted.
     *
     * On failure: An exception will be thrown and no changes will be made to the file system.
     *
     * @param dump The {@link SenateSiteDump} to be archived.
     * @throws IOException if any IO error occurs while compressing or moving these files.
     */
    void archiveDump(SenateSiteDump dump) throws IOException;
}
