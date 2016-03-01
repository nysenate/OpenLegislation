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
     * Marks the designated dump fragment as processed, ensuring it will not be retrieved with <code>getPendingDumps()</code>
     * @see #getPendingDumps(SpotCheckRefType)
     * @param dump SenateSiteBillDumpFragId
     */
    void setProcessed(SenateSiteDump dump) throws IOException;
}
