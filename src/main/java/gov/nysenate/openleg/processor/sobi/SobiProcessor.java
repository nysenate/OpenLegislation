package gov.nysenate.openleg.processor.sobi;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;

/**
 * Classes that implement functionality for processing a particular type of data
 * sent via sobi files should expose this interface.
 */
public interface SobiProcessor
{
    /**
     * Returns a SobiFragmentType value to indicate that the class will support
     * processing of the given type of fragment.
     *
     * @return SobiFragmentType
     */
    public SobiFragmentType getSupportedType();

    /**
     * Process the given fragment if it's type matches the processor's supported
     * type. The entities that the fragment's data concerns will be modified and
     * persisted, however the supplied fragment instance will not.
     *
     * @param fragment SobiFragment - The fragment to be processed.
     */
    public void process(final SobiFragment fragment);

    /**
     * Perform any additional tasks that must be run prior to finishing processing.
     */
    public void postProcess();
}