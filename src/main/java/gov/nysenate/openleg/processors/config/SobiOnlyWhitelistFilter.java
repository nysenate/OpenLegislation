package gov.nysenate.openleg.processors.config;

import gov.nysenate.openleg.processors.bill.SourceType;
import gov.nysenate.openleg.processors.bill.sobi.SobiBlock;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

/**
 * Allow only sobi data to be processed.
 */
public class SobiOnlyWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(LegDataFragment legDataFragment) {
        return legDataFragment.getParentLegDataFile().getSourceType() == SourceType.SOBI;
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return true;
    }
}
