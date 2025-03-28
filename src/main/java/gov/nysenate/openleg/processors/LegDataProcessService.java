package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentNotFoundEx;

import java.util.List;

/**
 * The LegDataProcessService interface provides the necessary methods for collating
 * and processing sobi files. These methods should typically be used via a
 * process intended to parse new sobi files.
 */
public interface LegDataProcessService extends ProcessService
{
    /**
     * Looks for sobi files that have been placed in the incoming directory and
     * parses them out into SobiFragments. The sobi files are then placed into an
     * archive directory and the SobiFragments are recorded in the backing store
     * as pending processing.
     *
     * @return int - The number of sobi files that have been collated.
     */
    int collateSourceFiles();

    /**
     * Process the list of supplied SobiFragments.
     *  @param fragments List<LegDataFragment> - List of fragments to process.
     */
    int processFragments(List<LegDataFragment> fragments);

    /**
     * Retrieves all pending fragments and processes them.
     * the results.
     */
    int processPendingFragments();

    /**
     * Toggle the pending processing status of a LegDataFragment via it's fragmentId.
     *
     * @param fragmentId String - The fragment id
     * @param pendingProcessing boolean - Indicate if fragment is pending processing
     * @throws LegDataFragmentNotFoundEx - If the fragmentId did not match a stored fragment
     */
    void updatePendingProcessing(String fragmentId, boolean pendingProcessing)
                                        throws LegDataFragmentNotFoundEx;
}
