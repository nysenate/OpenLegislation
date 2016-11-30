package gov.nysenate.openleg.processor.sobi;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.base.ProcessService;

import javax.xml.crypto.Data;
import java.util.List;

/**
 * The SobiProcessService interface provides the necessary methods for collating
 * and processing sobi files. These methods should typically be used via a
 * process intended to parse new sobi files.
 */
public interface SobiProcessService extends ProcessService
{
    /**
     * Looks for sobi files that have been placed in the incoming directory and
     * parses them out into SobiFragments. The sobi files are then placed into an
     * archive directory and the SobiFragments are recorded in the backing store
     * as pending processing.
     *
     * @return int - The number of sobi files that have been collated.
     */
    public int collateSobiFiles();

    /**
     * Retrieves the SobiFragments that are awaiting processing.
     *
     * @param sortByPubDate SortOrder - Sort order for the fragment id.
     * @param limitOffset LimitOffset - Restrict the results list.
     * @return List<SobiFragment>
     */
    public List<SobiFragment> getPendingFragments(SortOrder sortByPubDate, LimitOffset limitOffset);

    /**
     * Process the list of supplied SobiFragments.
     *  @param fragments List<SobiFragment> - List of fragments to process.
     * @param options - SobiProcessOptions - Provide custom processing options or
     */
    public int processFragments(List<SobiFragment> fragments, SobiProcessOptions options);

    /**
     * Retrieves all pending fragments and processes them. This is essentially a shorthand
     * for invoking {@link #getPendingFragments} and running {@link #processFragments} on
     * the results.
     *
     * @param options - SobiProcessOptions - Provide custom processing options or
     *                                       set to null to use the default options. TODO
     */
    public int processPendingFragments(SobiProcessOptions options);

    /**
     * Toggle the pending processing status of a SobiFragment via it's fragmentId.
     *
     * @param fragmentId String - The fragment id
     * @param pendingProcessing boolean - Indicate if fragment is pending processing
     * @throws SobiFragmentNotFoundEx - If the fragmentId did not match a stored fragment
     */
    public void updatePendingProcessing(String fragmentId, boolean pendingProcessing)
                                        throws SobiFragmentNotFoundEx;
}