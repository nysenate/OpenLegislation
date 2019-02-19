package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;

/**
 * Allow only sobi data to be processed.
 */
public class SobiOnlyWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(LegDataFragment legDataFragment) {
        return legDataFragment.getParentSobiFile().getSourceType() == SourceType.SOBI;
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return true;
    }
}
