package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawFragment;

import java.util.List;

public interface LawProcessService
{
    /**
     * Identifies all incoming law files and breaks them down into fragments. The fragments are determined
     * based on the document id header that is used to delineate the law files. Each doc header from each file
     * is associated with a single law fragment. These extracted fragments, as well as the file references, are
     * stored in the database and set to await processing.
     *
     * @return int - Number of law files collated
     */
    public int collateLaws();

    /**
     * Retrieve a list of the fragments that are awaiting processing.
     *
     * @return List<LawFragment>
     */
    public List<LawFragment> getPendingLawFragments();

    /**
     * Processes the given law fragments and updates the backing store as necessary.
     *
     * @param fragments List<LawFragment>
     */
    public void processLawFragments(List<LawFragment> fragments);

    /**
     * Processes all the law fragments that are set to await processing. This method will perform batching
     * of the fragments to reduce the chances of memory getting saturated if there are a lot of fragments to
     * process.
     */
    public void processPendingLawFragments();
}
