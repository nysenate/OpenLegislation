package gov.nysenate.openleg.dao.bill.reference.senatesite;

import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpFragId;

import java.io.IOException;
import java.util.Collection;

public interface SenateSiteBillDao {

    /**
     * Get all nysenate.gov bill dump fragments that have not yet been processed into a report
     * @return Collection<SenateSiteBillDumpFragment>
     */
    Collection<SenateSiteBillDump> getPendingDumps() throws IOException;

    /**
     * Persists the given nysenate.gov bill dump fragment
     * @param fragmentId
     * @param fragmentData
     */
    void saveDumpFragment(SenateSiteBillDumpFragId fragmentId, Object fragmentData) throws IOException;

    /**
     * Marks the designated bill dump fragment as processed, ensuring it will not be retrieved with
     * @see #getPendingDumps()
     * @param dump SenateSiteBillDumpFragId
     */
    void setProcessed(SenateSiteBillDump dump) throws IOException;
}
