package gov.nysenate.openleg.processors.config;

import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.sobi.SobiBlock;

/**
 * Defines rules for selecting which {@link LegDataFragment}s and {@link SobiBlock}s to process.
 */
public interface SourceFilter {

   boolean acceptFragment(LegDataFragment legDataFragment);

   boolean acceptBlock(SobiBlock sobiBlock);
}
