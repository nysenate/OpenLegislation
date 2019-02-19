package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;

/**
 * Defines rules for selecting which {@link LegDataFragment}s and {@link SobiBlock}s to process.
 */
public interface SourceFilter {

   boolean acceptFragment(LegDataFragment legDataFragment);

   boolean acceptBlock(SobiBlock sobiBlock);
}
