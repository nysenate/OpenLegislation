package gov.nysenate.openleg.processor.legdata;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentNotFoundEx;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.base.ProcessService;

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
     * Retrieves the SobiFragments that are awaiting processing.
     *
     * @param sortByPubDate SortOrder - Sort order for the fragment id.
     * @param limitOffset LimitOffset - Restrict the results list.
     * @return List<LegDataFragment>
     */
    List<LegDataFragment> getPendingFragments(SortOrder sortByPubDate, LimitOffset limitOffset);

    /**
     * Process the list of supplied SobiFragments.
     *  @param fragments List<LegDataFragment> - List of fragments to process.
     * @param options - SobiProcessOptions - Provide custom processing options or
     */
    int processFragments(List<LegDataFragment> fragments, SobiProcessOptions options);

    /**
     * Retrieves all pending fragments and processes them. This is essentially a shorthand
     * for invoking {@link #getPendingFragments} and running {@link #processFragments} on
     * the results.
     *
     * @param options - SobiProcessOptions - Provide custom processing options or
     *                                       set to null to use the default options. TODO
     */
    int processPendingFragments(SobiProcessOptions options);

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
