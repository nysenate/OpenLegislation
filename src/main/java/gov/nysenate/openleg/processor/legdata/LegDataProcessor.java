package gov.nysenate.openleg.processor.legdata;

import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;

/**
 * Classes that implement functionality for processing a particular type of data
 * sent via sobi files should expose this interface.
 */
public interface LegDataProcessor
{
    /**
     * Returns a LegDataFragmentType value to indicate that the class will support
     * processing of the given type of fragment.
     *
     * @return LegDataFragmentType
     */
    LegDataFragmentType getSupportedType();

    /**
     * Process the given fragment if it's type matches the processor's supported
     * type. The entities that the fragment's data concerns will be modified and
     * persisted, however the supplied fragment instance will not.
     *
     * @param fragment LegDataFragment - The fragment to be processed.
     */
    void process(final LegDataFragment fragment);

    /**
     * Perform any additional tasks that must be run prior to finishing processing.
     */
    void postProcess();

    /**
     * All processors must have this method to ensure that the ManagedLegDataProcessService can properly flush the
     * Ingest Cache or caches the processor is specified to flush
     *
     * This is an exmaple of a bill implementation of this method
     * if (!env.isLegDataBatchEnabled() || billIngestCache.exceedsCapacity()) {
     *             flushBillUpdates();
     *         }
     */
    void checkIngestCache();
}
