package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;

/**
 * Defines rules for selecting which {@link SobiFragment}s and {@link SobiBlock}s to process.
 */
public interface SourceFilter {

   boolean acceptFragment(SobiFragment sobiFragment);

   boolean acceptBlock(SobiBlock sobiBlock);
}
