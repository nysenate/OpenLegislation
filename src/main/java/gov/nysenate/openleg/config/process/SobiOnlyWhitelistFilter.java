package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;

/**
 * Allow only sobi data to be processed.
 */
public class SobiOnlyWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(SobiFragment sobiFragment) {
        return sobiFragment.getParentSobiFile().getSourceType() == SourceType.SOBI;
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return true;
    }
}
